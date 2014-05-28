package com.danielecampogiani.mynativephotodiary.fragments;


import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.adapters.TimelinePicturesAdapter;
import com.danielecampogiani.mynativephotodiary.persistence.PicturesProvider;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimelineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TimelinePicturesAdapter adapter;
    private AlphaInAnimationAdapter animatedAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new TimelinePicturesAdapter(getActivity(),null,0);
        animatedAdapter = new AlphaInAnimationAdapter(adapter);
        animatedAdapter.setAbsListView(getListView());
        setListAdapter(animatedAdapter);
        getLoaderManager().initLoader(0,null,this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{PicturesProvider.KEY_ID,PicturesProvider.KEY_DESCRIPTION,PicturesProvider.KEY_URI};
        return new CursorLoader(getActivity(), PicturesProvider.CONTENT_URI,projection,null,null,PicturesProvider.KEY_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
