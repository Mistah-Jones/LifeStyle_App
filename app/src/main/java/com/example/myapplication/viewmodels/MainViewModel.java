package com.example.myapplication.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.models.MainRepository;
import com.example.myapplication.models.UserInfo;
import com.example.myapplication.weatherbackend.WeatherData;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<WeatherData> jsonData;
    private MutableLiveData<UserInfo> currUserData;
    private MainRepository mMainRepository;

    public MainViewModel(Application application) {
        super(application);
        mMainRepository = MainRepository.getInstance(application);
        jsonData = mMainRepository.getWeatherData();
        currUserData = mMainRepository.getCurrUserData();
    }

    public void setLocation(String location) {
        mMainRepository.setLocation(location);
    }
    public void setCurrUser(int weight, String birthdate, String location,
                            int height, String name, short gender, boolean activity,
                            String thumbnailString)
    {
        mMainRepository.setCurrUser(weight, birthdate, location, height, name, gender, activity, thumbnailString);
    }
    public void setCurrUser(String userID, String password) { mMainRepository.setCurrUser(userID, password); }

    public  LiveData<WeatherData> getWeatherData() { return jsonData; }
    public LiveData<UserInfo> getCurrUserData() {return currUserData; }
}