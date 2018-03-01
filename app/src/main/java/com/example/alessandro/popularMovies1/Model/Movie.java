package com.example.alessandro.popularMovies1.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

// Movie class that is used to store Movie data
public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    /**
     * Declare private variables
     */
    private final Long mId;
    private final String mTitle;
    private final String mReleaseDate;
    private final String mVoteAverage;
    private final String mOverview;
    private final Uri mPosterUri;

    // Movie Constructor
    public Movie(Long id, String title, String releaseDate, String voteAverage, String
            overview, Uri posterUri) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mOverview = overview;
        mPosterUri = posterUri;
    }

    // Parcelling part
    private Movie(Parcel in) {
        this.mId = in.readLong();
        this.mTitle = in.readString();
        this.mReleaseDate = in.readString();
        this.mVoteAverage = in.readString();
        this.mOverview = in.readString();
        this.mPosterUri = (Uri) in.readValue(Movie.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mReleaseDate);
        dest.writeString(this.mVoteAverage);
        dest.writeString(this.mOverview);
        dest.writeValue(this.mPosterUri);
    }

    // getter for Movie class variables

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }


    public String getVoteAverage() {
        return mVoteAverage;
    }


    public String getOverview() {
        return mOverview;
    }


    public Uri getPosterUri() {
        return mPosterUri;
    }

}
