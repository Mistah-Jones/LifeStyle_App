package com.example.myapplication.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.time.LocalDate;
import java.time.Period;

public class UserInfo {

    //things received from fragment
    private int weight;
    private String birthdate;
    private String location;
    private int height;
    private String name;
    private short gender;
    private boolean activity;
    private String thumbnailString;

    //things calculated
    private int age;
    private Bitmap thumbnail;
    private float bmi, bmr;

    //things inputted;
    private float weightchange;

    public UserInfo(int weight, String birthdate, String location,
                 int height, String name, short gender, boolean activity,
                 String thumbnailString)
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

        setAge();
        setBmi();
        setBmr();
        this.weightchange = 0;
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

    public boolean isActivity() {
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

    public void setBmi(){
        this.bmi = (float)weight / (float)Math.pow(height,2);
        this.bmi *= 703;
    }

    public void setBmr(){
        // BMR Calculation (Harris-Benedict Equation)
        this.bmr = 0f;
        switch (gender)
        {
            // Male
            case 1:
                this.bmr = 66.47f + (6.24f * weight) + (12.7f * height) - (6.755f * age);
                break;
            // Female
            case 2:
                this.bmr = 65.51f + (4.35f * weight) + (4.7f * height) - (4.7f * age);
                break;
            // Non-Binary
            default:
                // Linearly interpolated values between the two equations
                this.bmr = 65.99f + (5.295f * weight) + (8.7f * height) - (5.7275f * age);
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

    public void setAge() {
        String[] dateParts = birthdate.split("/");
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1])); // Birth date

        Period p = Period.between(birthday, today);

        this.age = p.getYears();
    }
}
