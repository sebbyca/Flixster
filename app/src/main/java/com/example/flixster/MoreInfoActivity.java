package com.example.flixster;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class MoreInfoActivity extends AppCompatActivity {

    // The movie being on which to display more information
    Movie movie;

    // The viewable objects
    TextView tvTitle;
    TextView tvOverview;
    TextView tvTagline;
    TextView tvDate;
    TextView tvGenre;
    TextView tvRuntime;
    RatingBar rbVoteAverage;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        // Resolve the view objects
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvTagline = findViewById(R.id.tvTagline);

        tvDate = findViewById(R.id.tvDate);
        tvRuntime = findViewById(R.id.tvRuntime);

        tvGenre = findViewById(R.id.tvGenre);

        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        ivPoster = findViewById(R.id.ivPoster);


        // Retrieve the movie passed in via intent
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));


        // Set the title (and year), overview, and tagline
        tvTitle.setText(String.format("%s (%s)", movie.getTitle(), movie.getDate().substring(0, 4)));
        tvOverview.setText(movie.getOverview());
        tvTagline.setText(movie.getTagline());

        // Set the date in the following format: MM/DD/YYYY
        tvDate.setText(String.format("%s/%s/%s", movie.getDate().substring(5, 7), movie.getDate().substring(8), movie.getDate().substring(0, 4)));

        // Set the runtime in the following format: #h #m
        tvRuntime.setText(String.format("%sh %sm", (int)(movie.getRuntime() / 60), movie.getRuntime() % 60));

        // Set the movie's genre(s)
        List<String> genres = movie.getGenres();
        String g = "";
        for (int i = 0; i < genres.size(); i++) {
            g = g + genres.get(i);
            if (i < genres.size() - 1) g = g + ", ";
        }
        tvGenre.setText(g);

        // Convert voter average from 0-10 to 0-5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // Set poster/backdrop movie image
        // By default phone is in portrait and therefore we use backdrop image
        String imageUrl = movie.getBackdropPath();
        int placeholder = R.drawable.flicks_backdrop_placeholder;

        // If phone is in landscape, set imageUrl to poster image
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getPosterPath();
            placeholder = R.drawable.flicks_movie_placeholder;
        }
        Glide.with(this).load(imageUrl).placeholder(placeholder).into(ivPoster);
    }
}