package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.ScoresContract;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;


            @Override
            public void onCreate() {
                // Nothing to do
            }


            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the barqsoft.footballscores.widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = mFormat.format(new Date(System.currentTimeMillis()));
                data = getContentResolver().query(ScoresContract.ScoresTable.buildScoreWithDate(),
                        null,
                        null,
                        new String[]{currentDate},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }


            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }


            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }


            @Override
            public RemoteViews getViewAt(int position) {
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = mFormat.format(new Date(System.currentTimeMillis()));
                    views.setTextViewText(R.id.widget_header_text, getString(R.string.todays_matches) + " for " + currentDate);
                    return null;
                }

                views.setTextViewText(R.id.widget_home_name, data.getString(ScoresContract.ScoresTable.COL_HOME));
                views.setTextViewText(R.id.widget_away_name, data.getString(ScoresContract.ScoresTable.COL_AWAY));
                views.setTextViewText(R.id.widget_header_text, getString(R.string.todays_matches) + " - "
                        + data.getString(ScoresContract.ScoresTable.COL_DATE));
                views.setTextViewText(R.id.widget_score, Utility.getScores(data.getInt(ScoresContract.ScoresTable.COL_HOME_GOALS), data.getInt(ScoresContract.ScoresTable.COL_AWAY_GOALS)));
                views.setTextViewText(R.id.empty_view, "");
                int matchId = data.getInt(ScoresContract.ScoresTable.COL_ID);

                final Intent fillInIntent = new Intent();
                Uri uri = ScoresContract.ScoresTable.buildScoreWithId();
                fillInIntent.setData(uri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }


            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }


            @Override
            public int getViewTypeCount() {
                return 1;
            }


            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(ScoresContract.ScoresTable.COL_ID);
                return position;
            }


            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
