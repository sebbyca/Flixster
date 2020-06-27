package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;
import com.example.flixster.models.MovieDatabaseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final int DARK_THEME_BACKGROUND = Color.parseColor("#242526");
    public static final int DARK_THEME_HEADERS = Color.parseColor("#D5CDCD");
    public static final int DARK_THEME_TEXT = Color.parseColor("#FFFFFF");
    public static final int DARK_THEME_TOOLBAR = Color.parseColor("#6200EE");

    public static final int LIGHT_THEME_BACKGROUND = Color.parseColor("#FFFFFF");
    public static final int LIGHT_THEME_HEADERS = Color.parseColor("#242526");
    public static final int LIGHT_THEME_TEXT = Color.parseColor("#434446");
    public static final int LIGHT_THEME_TOOLBAR = Color.parseColor("#D7C4F2");

    public static final String API_KEY = "?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";

    List<Movie> movies;
    String dark = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView rvMovies = findViewById(R.id.rvMovies);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final Switch themeSwitch = findViewById(R.id.themeSwitch);

        loadStatus();
        if (dark.equals("true")) {
            themeSwitch.setChecked(true);
            themeSwitch.setText(String.format("%s", "Dark Mode"));
            themeSwitch.setTextColor(LIGHT_THEME_BACKGROUND);
            toolbar.setTitleTextColor(LIGHT_THEME_BACKGROUND);
            toolbar.setBackgroundColor(DARK_THEME_TOOLBAR);
        }

        setSupportActionBar(toolbar);

        movies = new ArrayList<>();

        // Create the adapter
        final MovieAdapter movieAdapter = new MovieAdapter(this, movies, dark);

        // Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        // Retrieval of the 'Now Playing' movies (along w/ basic information)
        MovieDatabaseClient.get("now_playing" + API_KEY, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Successful retrieval of 'Now Playing' Movies");
                final JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));

                    // For every movie that is in 'Now Playing'
                    for (int i = 0; i < movies.size(); i++) {
                        final Movie movie = movies.get(i);

                        // Retrieve information on it using its ID
                        MovieDatabaseClient.get(movie.getMovie_id() + API_KEY, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject movieObject = json.jsonObject;
                                try {
                                    movie.setExtraInfo(movieObject);
                                    movieAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error occurred in retrieving movie's information", e);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.d(TAG, "Unsuccessful retrieval of 'Genre List'");
                            }
                        });

                        MovieDatabaseClient.get(movie.getMovie_id() + "/videos" + API_KEY, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject videoObjects = json.jsonObject;
                                try {
                                    JSONArray results = videoObjects.getJSONArray("results");
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject videoObject = results.getJSONObject(i);

                                        String type = videoObject.getString("type");
                                        String site = videoObject.getString("site");

                                        if (type.equals("Trailer") && site.equals("YouTube")) {
                                            movie.setVideoKey(videoObject.getString("key"));
                                            break;
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "Hit json exception", e);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                    }
                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Unsuccessful retrieval of 'Now Playing' Movies");
            }
        });

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                RecyclerView.LayoutManager lm = rvMovies.getLayoutManager();
                int bg_color, title_color, context_color, toolbar_color;
                String text;
                if (isChecked) {
                    bg_color = DARK_THEME_BACKGROUND;
                    title_color =  DARK_THEME_HEADERS;
                    context_color = DARK_THEME_TEXT;
                    toolbar_color = DARK_THEME_TOOLBAR;

                    themeSwitch.setText(String.format("%s", "Dark Mode"));
                    themeSwitch.setTextColor(LIGHT_THEME_BACKGROUND);
                    toolbar.setTitleTextColor(LIGHT_THEME_BACKGROUND);
                    dark = "true";
                } else {
                    bg_color = LIGHT_THEME_BACKGROUND;
                    title_color = LIGHT_THEME_HEADERS;
                    context_color = LIGHT_THEME_TEXT;
                    toolbar_color = LIGHT_THEME_TOOLBAR;

                    themeSwitch.setText(String.format("%s", "Light Mode"));
                    themeSwitch.setTextColor(DARK_THEME_BACKGROUND);
                    toolbar.setTitleTextColor(DARK_THEME_BACKGROUND);
                    dark = "false";
                }
                toolbar.setBackgroundColor(toolbar_color);

                for (int i = 0; i < lm.getChildCount(); i++) {
                    View rlView = lm.getChildAt(i);
                    TextView tvTitle = rlView.findViewById(R.id.tvTitle);
                    TextView tvOverview = rlView.findViewById(R.id.tvOverview);

                    movieAdapter.setTheme(bg_color, title_color, context_color, dark);

                    rlView.setBackgroundColor(bg_color);
                    tvTitle.setTextColor(title_color);
                    tvOverview.setTextColor(context_color);
                    movieAdapter.notifyDataSetChanged();
                }
                saveStatus();
            }
        });
    }

    private File getDataFile() {
        return new File(getFilesDir(), "status.txt");
    }

    private void loadStatus() {
        try {
            dark = FileUtils.readFileToString(getDataFile(), Charset.defaultCharset());
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            dark = "false";
        }
    }

    private void saveStatus() {
        try {
            FileUtils.write(getDataFile(), dark, Charset.defaultCharset());
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}