package com.danielecampogiani.mynativephotodiary.fragments;



import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielecampogiani.mynativephotodiary.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TimelineFragment extends ListFragment {


    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }


}
