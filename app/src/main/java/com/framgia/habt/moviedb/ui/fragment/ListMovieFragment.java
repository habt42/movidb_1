package com.framgia.habt.moviedb.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Movie;
import com.framgia.habt.moviedb.ui.adapter.ListMovieAdapter;
import com.framgia.habt.moviedb.util.ApiConst;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Locale;

public class ListMovieFragment extends Fragment {
    public static final String ARG_JSON_LINK = "ARG_JSON_LINK";
    private static final String URL_FORMAT_WITH_PAGE_PARAMS = "%s%s%d";
    private static final int JSON_FIRST_PAGE = 1;
    private SwipeRefreshLayout mSrl;
    private RecyclerView mRvMovies;
    private String mJsonLink;
    private int mCurrentPage;
    private ListMovieAdapter mMovieAdapter;
    private ArrayList<Movie> mListMovie;

    public ListMovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJsonLink = getArguments().getString(ARG_JSON_LINK);
        mListMovie = new ArrayList<>();
        mCurrentPage = JSON_FIRST_PAGE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_movie, container, false);
        mRvMovies = (RecyclerView) view.findViewById(R.id.fragment_list_movie_rv);
        mRvMovies.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMovieAdapter = new ListMovieAdapter(getActivity(), mRvMovies, mListMovie);
        mMovieAdapter.setOnLoadMoreListener(new ListMovieAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(++mCurrentPage);
            }
        });
        mRvMovies.setAdapter(mMovieAdapter);
        refreshList();
        mSrl = (SwipeRefreshLayout) view.findViewById(R.id.fragment_list_movie_srl);
        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        return view;
    }

    public static ListMovieFragment newInstance(String jsonLink) {
        Bundle args = new Bundle();
        args.putString(ARG_JSON_LINK, jsonLink);
        ListMovieFragment fragment = new ListMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void loadData(int fromPage) {
        String url = String.format(Locale.getDefault(),
                URL_FORMAT_WITH_PAGE_PARAMS, mJsonLink, ApiConst.PAGE_PARAMS, fromPage);
        if (mListMovie.size() > 0) {
            mListMovie.add(null);
            mMovieAdapter.notifyItemInserted(mListMovie.size() - 1);
        }
        StringRequest stringReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().create();
                GsonParse parse = gson.fromJson(response, GsonParse.class);
                if (mListMovie.size() > 0) {
                    mListMovie.remove(mListMovie.size() - 1);
                    mMovieAdapter.notifyItemRemoved(mListMovie.size());
                }
                ArrayList<Movie> tmp = parse.getListMovies();
                int positionStart = mListMovie.size() - 1;
                int itemCount = tmp.size();
                mListMovie.addAll(tmp);
                mMovieAdapter.notifyItemRangeInserted(positionStart, itemCount);
                mMovieAdapter.setLoaded();
                mSrl.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.json_load_error, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(stringReq);
    }

    private void refreshList() {
        int itemCount = mListMovie.size();
        mListMovie.clear();
        mMovieAdapter.notifyItemRangeRemoved(0, itemCount);
        loadData(JSON_FIRST_PAGE);
    }

    private static class GsonParse {
        @SerializedName("page")
        int page;
        @SerializedName("results")
        ArrayList<Movie> listMovies;

        public ArrayList<Movie> getListMovies() {
            return listMovies;
        }

        public int getPage() {
            return page;
        }
    }

}
