package com.framgia.habt.moviedb.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by habt on 9/20/16.
 */
public class Genre {
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
