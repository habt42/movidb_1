package com.framgia.habt.moviedb.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Cast;
import com.framgia.habt.moviedb.data.model.Movie;
import com.framgia.habt.moviedb.ui.activity.MovieDetailActivity;
import com.framgia.habt.moviedb.ui.adapter.RecyclerViewAdapter;
import com.framgia.habt.moviedb.util.ApiConst;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecyclerViewFragment<T> extends Fragment implements RecyclerViewAdapter.ClickListener {
    public static final int LIST_MOVIE = 100;
    public static final int LIST_CAST = 101;
    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";

    private static final String ARG_SOURCE_LOAD = "ARG_SOURCE_LOAD";
    private static final String ARG_LIST_TYPE = "ARG_LIST_TYPE";
    private static final int LIST_MOVIE_VISIBLE_THRESHOLD = 3;

    private SwipeRefreshLayout mSrl;
    private RecyclerView mRecyclerView;
    private TextView mTvEmptyMessage;

    private String mTitle;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<T> mList;
    private Activity mActivity;
    private String mSourceLoad;
    private int mCurrentPage;
    private int mListType;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSourceLoad = getArguments().getString(ARG_SOURCE_LOAD);
        mListType = getArguments().getInt(ARG_LIST_TYPE);
        mList = new ArrayList<>();
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mTvEmptyMessage = (TextView) view.findViewById(R.id.fragment_recycler_view_tv_empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new RecyclerViewAdapter(mList);
        if (mListType == LIST_MOVIE) {
            mAdapter.setOnLoadMoreListener(new RecyclerViewAdapter.LoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadData(++mCurrentPage);
                }
            });
            mAdapter.setupEndlessScroll(mRecyclerView, LIST_MOVIE_VISIBLE_THRESHOLD);
            mAdapter.setClickListener(this);
        }
        mRecyclerView.setAdapter(mAdapter);
        refreshList();
        mSrl = (SwipeRefreshLayout) view.findViewById(R.id.fragment_recycler_view_srl);
        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTitle != null) {
            getActivity().setTitle(mTitle);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mListType == LIST_MOVIE) {
            Intent intent = new Intent(mActivity, MovieDetailActivity.class);
            intent.putExtra(EXTRA_MOVIE, (Movie) mList.get(position));
            startActivity(intent);
        }
    }

    public static RecyclerViewFragment newInstance(String sourceLoad, int listType) {
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE_LOAD, sourceLoad);
        args.putInt(ARG_LIST_TYPE, listType);
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RecyclerViewFragment setTitle(String title) {
        mTitle = title;
        return this;
    }

    public void reloadFromUrl(String url) {
        mSourceLoad = url;
        refreshList();
    }

    private void loadData(int fromPage) {
        if (mSourceLoad == null) return;
        String url = "";
        switch (mListType) {
            case LIST_MOVIE:
                url = String.format(ApiConst.MOVIE_LIST_URL_WITH_PAGE_PARAMS, mSourceLoad, fromPage);
                break;
            case LIST_CAST:
                url = String.format(ApiConst.MOVIE_CREDITS_URL, mSourceLoad);
                break;
            default:
                Toast.makeText(mActivity, R.string.json_load_error, Toast.LENGTH_SHORT).show();
                return;
        }
        if (mList.size() > 0) {
            mList.add(null);
            mAdapter.notifyItemInserted(mList.size() - 1);
        }
        StringRequest stringReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().create();
                GsonParse parse = gson.fromJson(response, GsonParse.class);
                if (mList.size() > 0) {
                    mList.remove(mList.size() - 1);
                    mAdapter.notifyItemRemoved(mList.size());
                }
                ArrayList tmp = new ArrayList();
                if (mListType == LIST_MOVIE) {
                    tmp = parse.getListMovies();
                } else if (mListType == LIST_CAST) {
                    tmp = parse.getListCast();
                }
                int positionStart = mList.size() == 0 ? 0 : mList.size() - 1;
                int itemCount = tmp.size();
                mList.addAll(tmp);
                if (mList.isEmpty()) {
                    mTvEmptyMessage.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    mTvEmptyMessage.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter.notifyItemRangeInserted(positionStart, itemCount);
                    mAdapter.newDataLoaded();
                }
                mSrl.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mActivity, R.string.json_load_error, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(stringReq);
    }

    private void refreshList() {
        int itemCount = mList.size();
        mList.clear();
        mAdapter.notifyItemRangeRemoved(0, itemCount);
        mCurrentPage = 1;
        loadData(mCurrentPage);
    }

    private static class GsonParse {
        @SerializedName("results")
        ArrayList<Movie> listMovies;
        @SerializedName("cast")
        ArrayList<Cast> listCast;

        public ArrayList<Cast> getListCast() {
            return listCast;
        }

        public ArrayList<Movie> getListMovies() {
            return listMovies;
        }
    }
}
