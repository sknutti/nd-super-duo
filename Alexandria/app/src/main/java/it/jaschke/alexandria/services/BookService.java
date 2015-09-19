package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import it.jaschke.alexandria.api.ServiceAPI;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.model.BookList;
import it.jaschke.alexandria.model.IndustryIdentifier;
import it.jaschke.alexandria.model.Item;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    public static final String FETCH_BOOK = "it.jaschke.alexandria.services.action.FETCH_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";

    public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

    private final String API = "https://www.googleapis.com/books/v1";
    private ServiceAPI api;

    public BookService() {
        super("Alexandria");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        api = restAdapter.create(ServiceAPI.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                fetchBook(ean);
            } else if (DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                deleteBook(ean);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String ean) {
        if(ean!=null) {
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchBook(final String ean) {
        api.findBook("isbn:" + ean, new Callback<BookList>(){

            @Override
            public void success(BookList bookList, Response response) {
                if (bookList != null && bookList.getTotalItems() > 0) {
                    for (Item item : bookList.getItems()) {
                        Book book = item.getBookInfo();
                        if (book != null) {
                            boolean exists = checkBook(book.getIndustryIdentifiers());
                            if (exists) {
                                return;
                            }

                            //add to database
                            ContentValues values = new ContentValues();
                            values.put(AlexandriaContract.BookEntry.ISBN, book.getIndustryIdentifiers().get(1).getIdentifier());
                            values.put(AlexandriaContract.BookEntry.TITLE, book.getTitle());
                            values.put(AlexandriaContract.BookEntry.SUBTITLE, book.getSubtitle());
                            values.put(AlexandriaContract.BookEntry.DESC, book.getDescription());
                            values.put(AlexandriaContract.BookEntry.IMAGE_URL, book.getImageLinks().getThumbnail());

                            Uri bookUri = getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI, values);
                            long bookId = ContentUris.parseId(bookUri);

                            for (String author : book.getAuthors()) {
                                values = new ContentValues();
                                values.put(AlexandriaContract.AuthorEntry._ID, ean);
                                values.put(AlexandriaContract.AuthorEntry.AUTHOR, author);
                                getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
                            }

                            for (String category : book.getCategories()) {
                                values = new ContentValues();
                                values.put(AlexandriaContract.CategoryEntry._ID, ean);
                                values.put(AlexandriaContract.CategoryEntry.CATEGORY, category);
                                getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
                            }
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private boolean checkBook(List<IndustryIdentifier> isbnList) {
        if (isbnList != null) {
            for (IndustryIdentifier industryIdentifier : isbnList) {
                if (industryIdentifier.getType().equals("ISBN_13")) {
                    Cursor result = getContentResolver().query(AlexandriaContract.BookEntry.CONTENT_URI,
                            null,
                            AlexandriaContract.BookEntry.ISBN + " = ?",
                            new String[]{industryIdentifier.getIdentifier()},
                            null);
                    int count = result.getCount();
                    result.close();
                    return count > 0;
                }
            }
        }
        return false;
    }
 }