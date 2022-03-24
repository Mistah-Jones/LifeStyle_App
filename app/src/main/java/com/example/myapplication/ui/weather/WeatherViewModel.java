package com.example.myapplication.ui.weather;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.weatherbackend.WeatherData;

public class WeatherViewModel extends AndroidViewModel {
    private MutableLiveData<WeatherData> jsonData;
    private WeatherRepository mWeatherRepository;

    public WeatherViewModel(Application application) {
        super(application);
        mWeatherRepository = WeatherRepository.getInstance(application);
        jsonData = mWeatherRepository.getData();
    }

    public void setLocation(String location) {
        mWeatherRepository.setLocation(location);
    }

    public  LiveData<WeatherData> getData() { return jsonData; }
}