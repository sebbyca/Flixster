package com.example.flixster;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.flixster.models.DeviceDimensionsHelper;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Headers;

public class MoreInfoActivity extends AppCompatActivity {

    // The movie being on which to display more information
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

    ImageView ivPoster;
    ImageView yt_icon;

    String dark;
    int bg_color;
    int title_color;
    int context_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

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

        ivPoster = findViewById(R.id.ivPoster);
        yt_icon = findViewById(R.id.yt_icon);


        // Retrieve the movie passed in via intent
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        dark = Parcels.unwrap(getIntent().getParcelableExtra("THEME"));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

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

        // Set poster/backdrop movie image
        // By default phone is in portrait and therefore we use backdrop image
        String imageUrl = movie.getBackdropPath();
        int placeholder = R.drawable.flicks_backdrop_placeholder;

        // If phone is in landscape, set imageUrl to poster image
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getPosterPath();
            placeholder = R.drawable.flicks_movie_placeholder;
        }

        if (movie.getVideoKey() != null) {
            Glide.with(this).load(imageUrl).placeholder(placeholder).transform(new BlurTransformation(15)).into(ivPoster);
        } else {
            Glide.with(this).load(imageUrl).placeholder(placeholder).into(ivPoster);
            yt_icon.setVisibility(View.INVISIBLE);
        }

        ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movie.getVideoKey() != null) {
                    Intent intent = new Intent(getApplicationContext(), MovieTrailerActivity.class);

                    intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                    intent.putExtra("THEME", Parcels.wrap(dark));

                    startActivity(intent);
                }
            }
        });
    }
}