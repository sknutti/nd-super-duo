package barqsoft.footballscores;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.data.ScoresContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public scoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    private View mEmptyView;

    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

    public MainScreenFragment() {
    }

    public void setFragmentDate(String date) {
        fragmentdate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mEmptyView = rootView.findViewById(R.id.empty_view);

//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.scores_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        View emptyView = rootView.findViewById(R.id.empty_view);

//        mAdapter = new ScoresAdapter(getActivity(), new ScoresAdapter.ScoresAdapterOnClickHandler() {
//            @Override
//            public void onClick(Long date, ScoresAdapter.ScoresAdapterViewHolder vh) {
//                ((Callback) getActivity()).onItemSelected(null);
////                mPosition = vh.getAdapterPosition();
//            }
//        }, emptyView);
//
//        recyclerView.setAdapter(mAdapter);
//
//        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new scoresAdapter(getActivity(),null,0);
        score_list.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER,null,this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (fragmentdate[0] != null) {
            return new CursorLoader(getActivity(), ScoresContract.ScoresTable.buildScoreWithDate(),
                    null, null, fragmentdate, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }

        mAdapter.swapCursor(cursor);
        mEmptyView.setVisibility(cursor.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


}
