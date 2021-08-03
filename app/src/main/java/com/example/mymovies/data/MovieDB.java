package com.example.mymovies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class, FavouriteMovie.class}, version = 4, exportSchema = false)
public abstract class MovieDB extends RoomDatabase {

    private static final String DB_NAME = "movies.db";
    private static MovieDB database;
    private static final Object LOCK = new Object();

    public static MovieDB getInstance(Context context){
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, MovieDB.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return database;
    }

    public abstract MovieDAO movieDAO();
}
