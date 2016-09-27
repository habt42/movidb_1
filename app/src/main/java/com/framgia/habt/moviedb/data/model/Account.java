package com.framgia.habt.moviedb.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by habt on 9/23/16.
 */
public class Account implements Parcelable {
    public static final String GRAVATAR = "gravatar";
    public static final String HASH = "hash";
    @SerializedName("id")
    private String mId;
    @SerializedName("hash")
    private String mGravatarHash;
    @SerializedName("name")
    private String mName;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("avatar")
    private JsonObject mAvatar;

    public JsonObject getAvatar() {
        return mAvatar;
    }

    public void setAvatar(JsonObject avatar) {
        mAvatar = avatar;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getGravatarHash() {
        return mGravatarHash;
    }

    public void setGravatarHash(String gravatarHash) {
        mGravatarHash = gravatarHash;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public Account(Parcel in) {
        mId = in.readString();
        mGravatarHash = in.readString();
        mName = in.readString();
        mUsername = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mGravatarHash);
        parcel.writeString(mName);
        parcel.writeString(mUsername);
    }

    public static final Parcelable.Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel parcel) {
            return new Account(parcel);
        }

        @Override
        public Account[] newArray(int i) {
            return new Account[i];
        }
    };
}
