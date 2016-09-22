package com.framgia.habt.moviedb.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by habt on 9/15/16.
 */
public class Movie implements Parcelable{
    @SerializedName("id")
    private String mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("vote_average")
    private String mVoteAverage;
    @SerializedName("vote_count")
    private String mVoteCount;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("status")
    private String Status;
    @SerializedName("tagline")
    private String mTagline;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("genres")
    private Genre[] mGenres;
    @SerializedName("production_companies")
    private ProductionCompany[] mProductionCompanies;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(String voteCount) {
        mVoteCount = voteCount;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTagline() {
        return mTagline;
    }

    public void setTagline(String tagline) {
        mTagline = tagline;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public Genre[] getGenres() {
        return mGenres;
    }

    public void setGenres(Genre[] genres) {
        mGenres = genres;
    }

    public ProductionCompany[] getProductionCompanies() {
        return mProductionCompanies;
    }

    public void setProductionCompanies(ProductionCompany[] productionCompanies) {
        mProductionCompanies = productionCompanies;
    }

    public Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mBackdropPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mBackdropPath);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
