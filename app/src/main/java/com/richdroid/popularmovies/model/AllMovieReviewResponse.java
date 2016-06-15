package com.richdroid.popularmovies.model;

/**
 * Created by richa.khanna on 6/15/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllMovieReviewResponse {

    int id;
    int page;
    private ArrayList<MovieReview> results;
    @SerializedName("total_pages")
    private long totalPages;
    @SerializedName("total_results")
    private long totalResults;

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public ArrayList<MovieReview> getResults() {
        return results;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }
}
