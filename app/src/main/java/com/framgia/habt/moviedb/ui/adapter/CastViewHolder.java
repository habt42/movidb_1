package com.framgia.habt.moviedb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Cast;
import com.framgia.habt.moviedb.util.ApiConst;

/**
 * Created by habt on 9/23/16.
 */
public class CastViewHolder extends RecyclerView.ViewHolder {
    private NetworkImageView mImvPicture;
    private TextView mTvName;
    private TextView mTvCharacter;
    private Context mContext;

    public CastViewHolder(View itemView) {
        super(itemView);
        mImvPicture = (NetworkImageView) itemView.findViewById(R.id.item_cast_imv_picture);
        mTvName = (TextView) itemView.findViewById(R.id.item_cast_tv_name);
        mTvCharacter = (TextView) itemView.findViewById(R.id.item_cast_tv_character);
        mContext = itemView.getContext();
    }

    public void setValue(Cast cast) {
        ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        mImvPicture.setImageUrl(ApiConst.POSTER_PATH_PREFIX_URL + cast.getProfilePath(),
                imgLoader);
        mTvName.setText(cast.getName());
        mTvCharacter.setText(
                mContext.getResources().
                        getText(R.string.item_cast_character) + cast.getCharacter());
    }
}
