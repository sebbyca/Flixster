package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@Parcel // annotation indicates class is Parcelable
public class Movie {

    int movie_id;

    String backdropPath;
    String posterPath;
    String title;
    String overview;

    String tagline;
    String date;
    Double voteAverage;

    String videoKey;
    int runtime;
    List<String> genres;

    public Movie() {}

    public Movie(JSONObject movieObject) throws JSONException {
        // First GET call retrieves a movie's ID (+ other crucial information)
        movie_id = movieObject.getInt("id");
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public void setExtraInfo(JSONObject movieObject) throws JSONException {
        backdropPath = movieObject.getString("backdrop_path");
        posterPath = movieObject.getString("poster_path");

        title = movieObject.getString("title");
        overview = movieObject.getString("overview");

        tagline = movieObject.getString("tagline");

        voteAverage = movieObject.getDouble("vote_average");
        date = movieObject.getString("release_date");
        runtime = movieObject.getInt("runtime");

        genres = new ArrayList<>();
        JSONArray pairs = movieObject.getJSONArray("genres");
        for (int i = 0; i < pairs.length(); i++) {
            genres.add(pairs.getJSONObject(i).getString("name"));
        }
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/original/%s", backdropPath);
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/original/%s", posterPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getDate() {
        return date;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public String getTagline() {
        return tagline;
    }

    public int getRuntime() {
        return runtime;
    }
}
