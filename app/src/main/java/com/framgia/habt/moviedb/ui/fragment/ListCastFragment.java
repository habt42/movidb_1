package com.framgia.habt.moviedb.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.habt.moviedb.R;

public class ListCastFragment extends Fragment {
    public static String ARG_MOVIE_ID = "ARG_MOVIE_ID";

    public ListCastFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_cast, container, false);
    }

    public static ListCastFragment newInstance(String movieId) {
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_ID, movieId);
        ListCastFragment fragment = new ListCastFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
