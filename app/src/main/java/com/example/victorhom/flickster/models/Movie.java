package com.example.victorhom.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by victorhom on 10/14/16.
 */
public class Movie {
    String posterPath;
    String originalTitle;
    String overview;
    String originalOverview;
    String backdropPath;
    String movieId;
    String voteAverage;

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s",posterPath);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getOriginalOverview() {
        return originalOverview;
    }

    public Movie(JSONObject jsonObject) throws JSONException {
        this.posterPath = jsonObject.getString("poster_path");
        this.originalTitle = jsonObject.getString("original_title");
        String overviewToTruncate = jsonObject.getString("overview");
        // shortening the overview if it's too long
        this.overview = overviewToTruncate.substring(0, Math.min(overviewToTruncate.length(), 300));
        this.originalOverview = overviewToTruncate;
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.movieId = jsonObject.getString("id");
        this.voteAverage = jsonObject.getString("vote_average");
    }

    public static ArrayList<Movie> fromJSONArray(JSONArray array) {
        ArrayList<Movie> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Movie(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
