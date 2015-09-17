package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.model.Fixture;
import barqsoft.footballscores.model.FixtureList;
import barqsoft.footballscores.service.ServiceApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sknutti on 8/10/15.
 */
public class SyncAdapter  extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 60;   // seconds * minutes, sync interval
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final String API = "http://api.football-data.org/alpha";
    public static final String ACTION_DATA_UPDATED = "barqsoft.footballscores.ACTION_DATA_UPDATED";

    private Context mContext;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        ServiceApi api = restAdapter.create(ServiceApi.class);

        api.fetchData(mContext.getString(R.string.api_key), "n3", getFetchDataCallback());
        api.fetchData(mContext.getString(R.string.api_key), "p3", getFetchDataCallback());
    }

    private Callback<FixtureList> getFetchDataCallback() {
        return new Callback<FixtureList>() {

            @Override
            public void success(FixtureList fixtureList, Response response) {
                if (fixtureList != null && fixtureList.getCount() > 0) {
                    Vector<ContentValues> values = new Vector<ContentValues>(fixtureList.getFixtures().size());
                    for (Fixture fixture : fixtureList.getFixtures()) {
                        int league = (int) ContentUris.parseId(Uri.parse(fixture.getLinks().getSoccerseason().getHref()));
                        //This if statement controls which leagues we're interested in the data from.
                        //add leagues here in order to have them be added to the DB.
                        // If you are finding no data in the app, check that this contains all the leagues.
                        // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                        if (league == Utility.PREMIER_LEAGUE ||
                                league == Utility.SERIE_A ||
                                league == Utility.BUNDESLIGA1 ||
                                league == Utility.BUNDESLIGA2 ||
                                league == Utility.PRIMERA_DIVISION ||
                                league == Utility.CHAMPIONS_LEAGUE) {
                            long match_id = ContentUris.parseId(Uri.parse(fixture.getLinks().getSelf().getHref()));

                            ContentValues match_values = new ContentValues();
                            match_values.put(ScoresContract.ScoresTable.MATCH_ID, match_id);

                            String mDate = fixture.getDate();
                            String mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                            mDate = mDate.substring(0, mDate.indexOf("T"));
                            SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                            match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                            try {
                                Date parsedDate = match_date.parse(mDate + mTime);
                                SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                                new_date.setTimeZone(TimeZone.getDefault());
                                mDate = new_date.format(parsedDate);
                                mTime = mDate.substring(mDate.indexOf(":") + 1);
                                mDate = mDate.substring(0, mDate.indexOf(":"));

                            } catch (Exception e) {
                                Log.d(LOG_TAG, "error here!");
                                Log.e(LOG_TAG, e.getMessage());
                            }

                            match_values.put(ScoresContract.ScoresTable.DATE_COL, mDate);
                            match_values.put(ScoresContract.ScoresTable.TIME_COL, mTime);
                            match_values.put(ScoresContract.ScoresTable.HOME_COL, fixture.getHomeTeamName());
                            match_values.put(ScoresContract.ScoresTable.AWAY_COL, fixture.getAwayTeamName());
                            match_values.put(ScoresContract.ScoresTable.HOME_GOALS_COL, fixture.getResult().getGoalsHomeTeam());
                            match_values.put(ScoresContract.ScoresTable.AWAY_GOALS_COL, fixture.getResult().getGoalsAwayTeam());
                            match_values.put(ScoresContract.ScoresTable.LEAGUE_COL, league);
                            match_values.put(ScoresContract.ScoresTable.MATCH_DAY, fixture.getMatchday());
                            values.add(match_values);
                        }
                    }
                    int inserted_data = 0;
                    ContentValues[] insert_data = new ContentValues[values.size()];
                    values.toArray(insert_data);
                    inserted_data = getContext().getContentResolver().bulkInsert(
                            ScoresContract.BASE_CONTENT_URI, insert_data);

                    updateWidgets();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Failed to fetch data from football-data.org...");
            }
        };
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in this app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
