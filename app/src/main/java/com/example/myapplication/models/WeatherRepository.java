package com.example.myapplication.models;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.views.WeatherFragment;
import com.example.myapplication.weatherbackend.JSONWeatherUtils;
import com.example.myapplication.weatherbackend.NetworkUtils;
import com.example.myapplication.weatherbackend.WeatherData;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherRepository {
    private static WeatherRepository instance;
    private final MutableLiveData<WeatherData> jsonData = new MutableLiveData<WeatherData>();
    private String mLocation;

    private WeatherRepository(Application application) {
        if (mLocation != null)
            loadData();
    }

    public static synchronized WeatherRepository getInstance(Application application) {
        if (instance == null) {
            instance = new WeatherRepository(application);
        }
        return instance;
    }

    public void setLocation(String location) {
        mLocation = location;
        loadData();
    }

    public MutableLiveData<WeatherData> getData() { return  jsonData; }

    private void loadData() { new FetchWeatherTask().execute(mLocation); }

    private class FetchWeatherTask{
        WeakReference<WeatherFragment> weatherFragmentWeakReference;
        private ExecutorService executorService = Executors.newSingleThreadExecutor();
        private Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        public void execute(String location){

            executorService.execute(new Runnable(){
                @Override
                public void run(){
                    String jsonWeatherData;
                    URL weatherDataURL = NetworkUtils.buildURLFromString(location);
                    jsonWeatherData = null;
                    try{
                        jsonWeatherData = NetworkUtils.getDataFromURL(weatherDataURL);
                        if (jsonWeatherData != null)
                            postToMainThread(jsonWeatherData);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        private void postToMainThread(String jsonWeatherData)
        {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (jsonWeatherData != null) {
                        try {
                            jsonData.setValue(JSONWeatherUtils.getWeatherData(jsonWeatherData));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

}