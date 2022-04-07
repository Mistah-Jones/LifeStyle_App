package com.example.myapplication.models;

import android.content.Context;

import androidx.room.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserTable.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static volatile RoomDB mInstance;
    public abstract LifestyleDoa lifestyleDao();
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    static synchronized RoomDB getDatabase(final Context context) {
        if(mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, "Lifestyle.db").build();
        }
        return mInstance;
    }
}
