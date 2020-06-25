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
import java.util.Map;

public class MoreInfoActivity extends AppCompatActivity {
    // The movie being on which to display more information
    Movie movie;
    Map<Integer, String> genreIDs;

    // The viewable objects
    TextView tvTitle;
    TextView tvOverview;
    TextView tvDate;
    TextView tvGenre;
    RatingBar rbVoteAverage;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        // Resolve the view objects
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        ivPoster = findViewById(R.id.ivPoster);
        tvDate = findViewById(R.id.tvDate);
        tvGenre = findViewById(R.id.tvGenre);

        // Retrieve the movie passed in via intent
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // Retrieve genre list
        genreIDs = Parcels.unwrap(getIntent().getParcelableExtra(MainActivity.GENRES));

        // Set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvDate.setText(String.format("Release Date: %s", movie.getDate()));

        // Convert voter average from 0-10 to 0-5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // By default phone is in portrait and therefore we use backdrop image
        String imageUrl = movie.getBackdropPath();

        // If phone is in landscape, set imageUrl to poster image
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getPosterPath();
        }
        Glide.with(this).load(imageUrl).into(ivPoster);

        List<Integer> moviesGenres = movie.getGenres();
        String g = "";
        for (int i = 0; i < moviesGenres.size(); i++) {
            g = g + genreIDs.get(moviesGenres.get(i));
            if (i < moviesGenres.size() - 1) g = g + ", ";
        }
        tvGenre.setText(g);
    }
}