package it.jaschke.alexandria.api;

import it.jaschke.alexandria.model.BookList;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sknutti on 9/10/15.
 */
public interface ServiceAPI {

    @GET("/volumes")
    void findBook(@Query("q") String isbn, Callback<BookList> response);
}