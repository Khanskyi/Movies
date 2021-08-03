package com.example.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {


    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    private static final String API_KEY = "4829508b1cab58acd939b79b47e52342";
    private static final String LANGUAGE_VALUE = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "200";

    public static final int TOP_RATED = 1;
    public static final int POPULARITY = 0;

    public static URL buildURLToVideos(int id){
        URL result = null;
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE).build();
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static URL buildURLToReviews(int id){
        URL result = null;
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY).build();
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForReviews(int id){
        JSONObject result = null;
        URL url = buildURLToReviews(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static JSONObject getJSONForVideos(int id){
        JSONObject result = null;
        URL url = buildURLToVideos(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static URL buildURL (int sortBy, int page) {

        URL result = null;
        String sortVale;
        if (sortBy == TOP_RATED){
            sortVale = SORT_BY_TOP_RATED;
        }else{
            sortVale = SORT_BY_POPULARITY;
        }

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, sortVale)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return result;

    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page){
        JSONObject result = null;
        URL url = buildURL(sortBy, page);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static class JSONLoader extends AsyncTaskLoader<JSONObject>{

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener{
            void onStartLoading();
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull @NotNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null){
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null){
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;

            if (url == null){
                return null;
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader); //for reading in strings
                String line  = bufferedReader.readLine();
                StringBuilder builder = new StringBuilder();
                while (line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                result = new JSONObject(builder.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if (connection != null){
                    connection.disconnect();
                }
            }

            return result;
        }
    }
    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(URL... urls) {

            JSONObject result = null;

            if (urls == null || urls.length == 0){
                return result;
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader); //for reading in strings
                String line  = bufferedReader.readLine();
                StringBuilder builder = new StringBuilder();
                while (line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                result = new JSONObject(builder.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if (connection != null){
                    connection.disconnect();
                }
            }

            return result;
        }
    }


}
