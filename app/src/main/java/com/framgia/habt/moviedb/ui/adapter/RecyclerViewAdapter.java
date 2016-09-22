package com.framgia.habt.moviedb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Cast;
import com.framgia.habt.moviedb.data.model.Movie;

import java.util.ArrayList;

/**
 * Created by habt on 9/19/16.
 */
public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MOVIE_VIEW = 0;
    private static final int LOADING_VIEW = 1;
    private static final int CAST_VIEW = 2;
    private ArrayList<T> mList;
    private boolean mIsLoadingNewData;
    private LoadMoreListener mLoadMoreListener;
    private ClickListener mClickListener;

    public RecyclerViewAdapter(ArrayList<T> list) {
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType) {
            case MOVIE_VIEW:
                view = inflater.inflate(R.layout.item_movie, parent, false);
                return new MovieViewHolder(view);
            case LOADING_VIEW:
                view = inflater.inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(view);
            case CAST_VIEW:
                view = inflater.inflate(R.layout.item_cast, parent, false);
                return new CastViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder vh = (MovieViewHolder) holder;
            vh.setValue((Movie) mList.get(position));
            vh.setClickListener(mClickListener);
        } else if (holder instanceof CastViewHolder) {
            CastViewHolder vh = (CastViewHolder) holder;
            vh.setValue((Cast) mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) == null) {
            return LOADING_VIEW;
        } else if (mList.get(position) instanceof Movie) {
            return MOVIE_VIEW;
        } else {
            return CAST_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setupEndlessScroll(RecyclerView rv, final int visibleThreshold) {
        final LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!mIsLoadingNewData && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore();
                    }
                    mIsLoadingNewData = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    public void newDataLoaded() {
        mIsLoadingNewData = false;
    }

    public interface ClickListener {
        void onItemClick(int position);
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
