package com.example.myapplication.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class UserTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "UserID")
    private int UserID;

    //TODO: Break up user data into individual column fields, non sql type
    @NonNull
    @ColumnInfo(name = "UserData")
    private UserInfo UserData;

    public UserTable(@NonNull int UserID, @NonNull UserInfo UserData) {
        this.UserID = UserID;
        this.UserData = UserData;
    }
}
