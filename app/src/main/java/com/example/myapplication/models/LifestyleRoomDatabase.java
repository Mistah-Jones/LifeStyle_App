package com.example.myapplication.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserInfo.class}, version = 1, exportSchema = false)
public abstract class LifestyleRoomDatabase extends RoomDatabase {
    private static volatile LifestyleRoomDatabase mInstance;
    public abstract LifestyleRoomDoa lifestyleRoomDao();
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    static synchronized LifestyleRoomDatabase getDatabase(final Context context) {
        if(mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    LifestyleRoomDatabase.class, "Lifestyle.db").build();
        }
        return mInstance;
    }
}
