package com.framgia.habt.moviedb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.framgia.habt.moviedb.R;

/**
 * Created by habt on 9/23/16.
 */
public class LoadingViewHolder extends RecyclerView.ViewHolder {
    private ProgressBar mProgressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.item_loading_progress_bar);
        mProgressBar.setIndeterminate(true);
    }
}
