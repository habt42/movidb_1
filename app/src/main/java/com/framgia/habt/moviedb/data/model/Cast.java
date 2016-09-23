package com.framgia.habt.moviedb.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by habt on 9/22/16.
 */
public class Cast {
    @SerializedName("name")
    private String mName;
    @SerializedName("character")
    private String mCharacter;
    @SerializedName("profile_path")
    private String mProfilePath;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String character) {
        mCharacter = character;
    }

    public String getProfilePath() {
        return mProfilePath;
    }

    public void setProfilePath(String profilePath) {
        mProfilePath = profilePath;
    }
}

