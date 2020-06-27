package com.example.flixster;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.parceler.Parcels;

import java.util.List;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    // Current movie
    Movie movie;

    // The viewable objects
    RelativeLayout rlView;
    TextView tvTitle;
    TextView tvDate;
    TextView tvGenre;
    TextView tvRuntime;

    RatingBar rbVoteAverage;
    TextView tvTagline;

    TextView textView;
    TextView textView2;
    TextView textView3;

    TextView tvOverview;

    String videoId;

    String dark;
    int bg_color;
    int title_color;
    int context_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        // Resolve the view objects
        rlView = findViewById(R.id.rlView);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvGenre = findViewById(R.id.tvGenre);
        tvRuntime = findViewById(R.id.tvRuntime);

        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        tvTagline = findViewById(R.id.tvTagline);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        tvOverview = findViewById(R.id.tvOverview);

        // Retrieve video ID of trailer
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        dark = Parcels.unwrap(getIntent().getParcelableExtra("THEME"));
        videoId = movie.getVideoKey();

        // Set colors according to theme
        bg_color = dark.equals("true") ? MainActivity.DARK_THEME_BACKGROUND : MainActivity.LIGHT_THEME_BACKGROUND;
        title_color = dark.equals("true") ? MainActivity.DARK_THEME_HEADERS : MainActivity.LIGHT_THEME_HEADERS;
        context_color = dark.equals("true") ? MainActivity.DARK_THEME_TEXT : MainActivity.LIGHT_THEME_TEXT;

        rlView.setBackgroundColor(bg_color);

        tvTitle.setText(String.format("%s (%s)", movie.getTitle(), movie.getDate().substring(0, 4)));
        tvTitle.setTextColor(title_color);

        // Set the date in the following format: MM/DD/YYYY
        tvDate.setText(String.format("%s/%s/%s", movie.getDate().substring(5, 7), movie.getDate().substring(8), movie.getDate().substring(0, 4)));
        tvDate.setTextColor(context_color);

        List<String> genres = movie.getGenres();
        String g = "";
        for (int i = 0; i < genres.size(); i++) {
            g = g + genres.get(i);
            if (i < genres.size() - 1) g = g + ", ";
        }
        tvGenre.setText(g);
        tvGenre.setTextColor(context_color);

        // Set the runtime in the following format: #h #m
        tvRuntime.setText(String.format("%sh %sm", (int)(movie.getRuntime() / 60), movie.getRuntime() % 60));
        tvRuntime.setTextColor(context_color);

        // Convert voter average from 0-10 to 0-5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        rbVoteAverage.setProgressBackgroundTintList(ColorStateList.valueOf(dark.equals("true") ? MainActivity.LIGHT_THEME_BACKGROUND : MainActivity.DARK_THEME_BACKGROUND));

        tvTagline.setText(movie.getTagline());
        tvTagline.setTextColor(context_color);

        textView.setTextColor(context_color);
        textView2.setTextColor(context_color);
        textView3.setTextColor(title_color);

        tvOverview.setText(movie.getOverview());
        tvOverview.setTextColor(context_color);


        // resolve the player view from the layout
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);

        // initialize with API key stored in secrets.xml
        playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }
}