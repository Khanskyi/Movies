package com.example.mymovies.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static MovieDB database;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;

    public MainViewModel(@NonNull @NotNull Application application) {
        super(application);
        database = MovieDB.getInstance(getApplication());
        movies = database.movieDAO().getAllMovies();
        favouriteMovies = database.movieDAO().getAllFavouriteMovies();
    }

    public Movie getMovieById(int id){
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllMovies(){
        new DeleteMoviesTask().execute();
    }

    public void insertMovies(Movie movie){
        new InsertTask().execute(movie);
    }

    public void deleteMovie(Movie movie){
        new DeleteTask().execute(movie);
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void insertFavouriteMovies(FavouriteMovie movie){
        new InsertFavouriteTask().execute(movie);
    }

    public void deleteFavouriteMovie(FavouriteMovie movie){
        new DeleteFavouriteTask().execute(movie);
    }

    public FavouriteMovie getFavouriteMovieById(int id){
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie>{

        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0){
                return database.movieDAO().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDAO().deleteAllMovies();
            return null;
        }
    }

    private static class InsertTask extends AsyncTask<Movie, Void, Void>{

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0){
                database.movieDAO().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Movie, Void, Void>{

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0){
                database.movieDAO().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void>{

        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0){
                database.movieDAO().insertFavouriteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void>{

        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0){
                database.movieDAO().deleteFavouriteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovie>{

        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0){
                return database.movieDAO().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }
}
