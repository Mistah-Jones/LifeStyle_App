package com.example.myapplication.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LifestyleRoomDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserInfo userInfo);

    @Query("SELECT * FROM User_Info WHERE userName = :userName AND password = :password")
    public UserInfo getUser(String userName, String password); //String userName, String password
}
