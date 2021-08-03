package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapters.ReviewAdapter;
import com.example.mymovies.adapters.TrailerAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewFavourite;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private int id;
    private MainViewModel viewModel;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewFavourite = findViewById(R.id.imageViewFavouriteOn);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        }else {
            finish();
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());

        setFavourite();

        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        reviewAdapter = new ReviewAdapter();
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId());
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);

        scrollViewInfo.smoothScrollTo(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourites:
                Intent intentToFavourites = new Intent(this, FavouriteMovie.class);
                startActivity(intentToFavourites);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnClickChangeFavourite(View view) {
        if (favouriteMovie == null){
            viewModel.insertFavouriteMovies(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        }else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null){
            imageViewFavourite.setImageResource(R.drawable.ic_favourite_off_60);
        }else{
            imageViewFavourite.setImageResource(R.drawable.ic_favourite_on_60);
        }

    }
}