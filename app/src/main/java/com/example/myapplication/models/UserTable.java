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
    private String UserID;

    @NonNull
    @ColumnInfo(name = "Weight")
    private int Weight;

    @NonNull
    @ColumnInfo(name = "Height")
    private int Height;

    @NonNull
    @ColumnInfo(name = "Birthdate")
    private String Birthdate;

    @NonNull
    @ColumnInfo(name = "Location")
    private String Location;

    @NonNull
    @ColumnInfo(name = "Name")
    private String Name;

    @NonNull
    @ColumnInfo(name = "Sex")
    private short Sex;

    @NonNull
    @ColumnInfo(name = "Activity")
    private boolean Activity;

    @NonNull
    @ColumnInfo(name = "Thumbnail")
    private String Thumbnail;

    public UserTable(@NonNull String UserID, @NonNull int Weight, @NonNull int Height,
                     @NonNull String Birthdate, @NonNull String Location, @NonNull String Name,
                     @NonNull short Sex, @NonNull boolean Activity, @NonNull String Thumbnail) {
        this.UserID = UserID;
        this.Weight = Weight;
        this.Height = Height;
        this.Birthdate = Birthdate;
        this.Location = Location;
        this.Name = Name;
        this.Sex = Sex;
        this.Activity = Activity;
        this.Thumbnail = Thumbnail;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public void setWeight(int Weight) {
        this.Weight = Weight;
    }

    public void setHeight(int Height) {
        this.Height = Height;
    }

    public void setBirthdate(String Birthdate) {
        this.Birthdate = Birthdate;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public  void setName(String Name) {
        this.Name = Name;
    }

    public void setSex(short Sex) {
        this.Sex = Sex;
    }

    public void setActivity(boolean Activity) {
        this.Activity = Activity;
    }

    public void setThumbnail(String Thumbnail) {
        this.Thumbnail = Thumbnail;
    }

    public String getUserID() { return UserID; }

    public int getWeight() { return Weight; }

    public int getHeight() { return Height; }

    public String getBirthdate() { return Birthdate; }

    public String getLocation() { return Location; }

    public String getName() { return Name; }

    public short getSex() { return Sex; }

    public boolean getActivity() { return Activity; }

    public  String getThumbnail() { return Thumbnail; }
}
