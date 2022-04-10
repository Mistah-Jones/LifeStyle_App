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

public class MainRepository {
    private static MainRepository instance;
    private final MutableLiveData<WeatherData> jsonData = new MutableLiveData<WeatherData>();
    private final MutableLiveData<UserInfo> currUserData = new MutableLiveData<UserInfo>();
    private UserInfo mCurrUser;

    private MainRepository(Application application) {
        if(mCurrUser != null) {
            loadCurrUserData();
            if (mCurrUser.getLocation() != null)
                loadWeatherData();
        }
    }

    public static synchronized MainRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MainRepository(application);
        }
        return instance;
    }

    public void setLocation(String location) {
        mCurrUser.setLocation(location);
        loadWeatherData();
    }

    public void setCurrUser(int weight, String birthdate, String location,
                            int height, String name, short gender, boolean activity,
                            String thumbnailString){
        mCurrUser = new UserInfo(weight, birthdate, location, height, name, gender, activity, thumbnailString);
        loadCurrUserData();
    }

    public MutableLiveData<WeatherData> getWeatherData() { return  jsonData; }
    public MutableLiveData<UserInfo> getCurrUserData() { return currUserData; }

    private void loadWeatherData() { new FetchWeatherTask().execute(mCurrUser.getLocation()); }

    //TODO: update this later to run in background, calc bmi, bmr, age, etc
    private void loadCurrUserData() {
        currUserData.setValue(mCurrUser);}

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

