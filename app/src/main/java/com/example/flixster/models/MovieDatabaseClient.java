package com.example.flixster.models;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

public class MovieDatabaseClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, JsonHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
