package com.example.myapplication.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.Period;

@Entity(tableName = "User_Info", primaryKeys = {"userName", "password"})
public class UserInfo {

    // Login items
    @NonNull
    @ColumnInfo(name = "userName")
    private String userName;
    @NonNull
    @ColumnInfo(name = "password")
    private String password;
    //things received from fragment
    @NonNull
    @ColumnInfo(name = "weight")
    private int weight;
    @NonNull
    @ColumnInfo(name = "birthdate")
    private String birthdate;
    @NonNull
    @ColumnInfo(name = "location")
    private String location;
    @NonNull
    @ColumnInfo(name = "height")
    private int height;
    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    @NonNull
    @ColumnInfo(name = "gender")
    private short gender;
    @NonNull
    @ColumnInfo(name = "activity")
    private boolean activity;
    @NonNull
    @ColumnInfo(name = "thumbnailString")
    private String thumbnailString;

    //things calculated
    @NonNull
    @ColumnInfo(name = "age")
    private int age;
    @NonNull
    @ColumnInfo(name = "bmi")
    private float bmi;
    @NonNull
    @ColumnInfo(name = "bmr")
    private float bmr;

    // Not in DB
    @Ignore
    private Bitmap thumbnail;

    //things inputted;
    @NonNull
    @ColumnInfo(name = "weightchange")
    private float weightchange;

    public UserInfo(@NonNull int weight,@NonNull String birthdate,@NonNull String location,
                 @NonNull int height,@NonNull String name,@NonNull short gender,@NonNull boolean activity,
                 @NonNull String thumbnailString)
    {
        this.weight = weight;
        this.birthdate = birthdate;
        this.location = location;
        this.height = height;
        this.name = name;
        this.gender = gender;
        this.activity = activity;
        this.thumbnailString = thumbnailString;

        byte [] encodeByte= Base64.decode(thumbnailString,Base64.DEFAULT);
        this.thumbnail = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        calculateAge();
        calculateBmi();
        calculateBmr();
        this.weightchange = 0f;
    }
    //TODO: This is not secure
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    //TODO: this is not secure
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public short getGender() {
        return gender;
    }
    public void setGender(short gender) {
        this.gender = gender;
    }

    public boolean getActivity() {
        return activity;
    }
    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public String getThumbnailString() {
        return thumbnailString;
    }
    public void setThumbnailString(String thumbnailString) {
        this.thumbnailString = thumbnailString;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public float getBmi() {
        return bmi;
    }
    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBmr() {
        return bmr;
    }
    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public float getWeightchange() {
        return weightchange;
    }
    public void setWeightchange(float weightchange) {
        this.weightchange = weightchange;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    // Helper Methods
    public void calculateBmi(){
        this.bmi = (float)this.weight / (float)Math.pow(this.height,2);
        this.bmi *= 703;
    }

    public void calculateBmr(){
        // BMR Calculation (Harris-Benedict Equation)
        this.bmr = 0f;
        switch (this.gender)
        {
            // Male
            case 1:
                this.bmr = 66.47f + (6.24f * this.weight) + (12.7f * this.height) - (6.755f * this.age);
                break;
            // Female
            case 2:
                this.bmr = 65.51f + (4.35f * this.weight) + (4.7f * this.height) - (4.7f * this.age);
                break;
            // Non-Binary
            default:
                // Linearly interpolated values between the two equations
                this.bmr = 65.99f + (5.295f * this.weight) + (8.7f * this.height) - (5.7275f * this.age);
                break;
        }
        // Since we are only using BMR for weight-loss calculations, we
        // include the activity level modifier in the BMR calculation.
        if (activity)
        {
            this.bmr *= 1.55;
        }
        else
        {
            this.bmr *= 1.175;
        }
    }

    public void calculateAge() {
        String[] dateParts = birthdate.split("/");
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1])); // Birth date

        Period p = Period.between(birthday, today);

        this.age = p.getYears();
    }

    public float calculateTargetCalories()
    {
        float calories = this.weightchange + (this.weightchange * 3500f) / 7f;
        return Math.round(Math.round(bmr) + Math.round(calories));
    }




}
