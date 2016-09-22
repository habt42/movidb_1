package com.framgia.habt.moviedb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Movie;
import com.framgia.habt.moviedb.util.ApiConst;

/**
 * Created by habt on 9/23/16.
 */
public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private NetworkImageView mImvPoster;
    private TextView mTvTitle;
    private TextView mTvReleaseDate;
    private TextView mTvVoteAverage;
    private TextView mTvVoteCount;
    private Context mContext;
    private RecyclerViewAdapter.ClickListener mClickListener;

    public MovieViewHolder(View itemView) {
        super(itemView);
        mImvPoster = (NetworkImageView) itemView.findViewById(R.id.item_movie_imv_poster);
        mTvTitle = (TextView) itemView.findViewById(R.id.item_movie_tv_title);
        mTvReleaseDate = (TextView) itemView.findViewById(R.id.item_movie_tv_release_date);
        mTvVoteAverage = (TextView) itemView.findViewById(R.id.item_movie_tv_vote_average);
        mTvVoteCount = (TextView) itemView.findViewById(R.id.item_movie_tv_vote_count);
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void setValue(Movie movie) {
        ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        mImvPoster.setImageUrl(ApiConst.POSTER_PATH_PREFIX_URL + movie.getPosterPath(),
                imgLoader);
        mTvTitle.setText(movie.getTitle());
        mTvReleaseDate.setText(movie.getReleaseDate());
        mTvVoteAverage.setText(
                mContext.getResources().
                        getText(R.string.movie_vote_average) + movie.getVoteAverage());
        mTvVoteCount.setText(
                mContext.getResources().
                        getText(R.string.movie_vote_count) + movie.getVoteCount());
    }

    public void setClickListener(RecyclerViewAdapter.ClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        mClickListener.onItemClick(getAdapterPosition());
    }
}
