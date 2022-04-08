package com.example.myapplication.models;

import androidx.lifecycle.LiveData;
import androidx.room.*;


@Dao
public interface LifestyleDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserTable userTable);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("SELECT * from user_table WHERE UserID = :userID")
    LiveData<UserTable> getUser(String userID);
}
