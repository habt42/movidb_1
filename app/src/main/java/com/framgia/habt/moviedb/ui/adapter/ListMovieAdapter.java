package com.framgia.habt.moviedb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Movie;
import com.framgia.habt.moviedb.util.ApiConst;

import java.util.ArrayList;

/**
 * Created by habt on 9/19/16.
 */
public class ListMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MOVIE_VIEW = 0;
    private static final int LOADING_VIEW = 1;
    private ArrayList<Movie> mListMovie;
    private Context mContext;
    private ImageLoader mImgLoader;
    private int mVisibleThreshold = 3;
    private int mTotalItemCount;
    private int mLastVisibleItem;
    private boolean mIsLoading;
    private LoadMoreListener mLoadMoreListener;
    private ClickListener mClickListener;

    public ListMovieAdapter(Context context, RecyclerView recView, ArrayList<Movie> movies) {
        mContext = context;
        mListMovie = movies;
        mImgLoader = AppController.getInstance().getImageLoader();
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recView.getLayoutManager();
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = layoutManager.getItemCount();
                mLastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore();
                    }
                    mIsLoading = true;
                }
            }
        });
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
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder vh = (MovieViewHolder) holder;
            Movie movie = mListMovie.get(position);
            vh.imvPoster.setImageUrl(ApiConst.POSTER_PATH_PREFIX_URL + movie.getPosterPath(),
                    mImgLoader);
            vh.tvTitle.setText(movie.getTitle());
            vh.tvReleaseDate.setText(movie.getReleaseDate());
            vh.tvVoteAverage.setText(
                    mContext.getResources().
                            getText(R.string.movie_vote_average) + movie.getVoteAverage());
            vh.tvVoteCount.setText(
                    mContext.getResources().
                            getText(R.string.movie_vote_count) + movie.getVoteCount());
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder vh = (LoadingViewHolder) holder;
            vh.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mListMovie.get(position) == null ? LOADING_VIEW : MOVIE_VIEW;
    }

    @Override
    public int getItemCount() {
        return mListMovie == null ? 0 : mListMovie.size();
    }

    public void setOnLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    public void setLoaded() {
        mIsLoading = false;
    }

    public interface ClickListener {
        void onItemClick(int position);
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.item_loading_progress_bar);
        }
    }

    private class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private NetworkImageView imvPoster;
        private TextView tvTitle;
        private TextView tvReleaseDate;
        private TextView tvVoteAverage;
        private TextView tvVoteCount;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imvPoster = (NetworkImageView) itemView.findViewById(R.id.item_movie_imv_poster);
            tvTitle = (TextView) itemView.findViewById(R.id.item_movie_tv_title);
            tvReleaseDate = (TextView) itemView.findViewById(R.id.item_movie_tv_release_date);
            tvVoteAverage = (TextView) itemView.findViewById(R.id.item_movie_tv_vote_average);
            tvVoteCount = (TextView) itemView.findViewById(R.id.item_movie_tv_vote_count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition());
        }
    }
}
