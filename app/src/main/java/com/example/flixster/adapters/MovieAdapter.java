package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.MainActivity;
import com.example.flixster.MoreInfoActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;
    String dark;

    int bg_color;
    int title_color;
    int context_color;

    public MovieAdapter(Context context, List<Movie> movies, String dark) {
        this.context = context;
        this.movies = movies;
        this.dark = dark;

        bg_color = dark.equals("true") ? MainActivity.DARK_THEME_BACKGROUND : MainActivity.LIGHT_THEME_BACKGROUND;
        title_color = dark.equals("true") ? MainActivity.DARK_THEME_HEADERS : MainActivity.LIGHT_THEME_HEADERS;
        context_color = dark.equals("true") ? MainActivity.DARK_THEME_TEXT : MainActivity.LIGHT_THEME_TEXT;
    }

    // Inflates the layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
//        MainActivity.
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder");
        // Get the movie at the passed in position
        Movie movie = movies.get(position);

        // Bind the movie data into the VH
        holder.bind(movie);
    }

    public void setTheme(int bg, int title, int context, String dark) {
        this.bg_color = bg;
        this.title_color = title;
        this.context_color = context;
        this.dark = dark;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        RelativeLayout rlView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);

            rlView = itemView.findViewById(R.id.rlView);

            // Creates an on-click listener for each IV
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            // Setting colors as appropriate w/ theme
            rlView.setBackgroundColor(bg_color);
            tvTitle.setTextColor(title_color);
            tvOverview.setTextColor(context_color);

            // By default phone is in portrait and therefore we use poster image
            String imageUrl = movie.getPosterPath();
            int placeholder = R.drawable.flicks_movie_placeholder;

            // If phone is in landscape, set imageUrl to back drop image
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
                placeholder = R.drawable.flicks_backdrop_placeholder;
            }

            int radius = 30; // corner radius, higher value = more rounded
            int margin = 10; // crop margin, set to 0 for corners with no crop
            Glide.with(context).load(imageUrl).placeholder(placeholder).transform(new RoundedCornersTransformation(radius, margin)).into(ivPoster);
        }

        // When the user clicks on a row, show MoreInfoActivity for the selected movie
        @Override
        public void onClick(View view) {
            // Get item position within the view
            int position = getAdapterPosition();

            // Make sure the position exists within the view
            if (position != RecyclerView.NO_POSITION) {
                // Retrieve movie at position
                Movie movie = movies.get(position);

                // Create intent for the "more info" activity
                Intent intent = new Intent(context, MoreInfoActivity.class);

                // Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                intent.putExtra("THEME", Parcels.wrap(dark));

                // Display the activity
                context.startActivity(intent);
            }
        }
    }
}
