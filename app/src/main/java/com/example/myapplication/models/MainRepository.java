package com.example.myapplication.models;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
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
    private String mLocation;
    private UserInfo mCurrUser;
    private LifestyleDoa mDao;
    private String mUserID;
    private LiveData<UserTable> currUserTable;

    private MainRepository(Application application) {
        RoomDB db = RoomDB.getDatabase(application);
        mDao = db.lifestyleDao();
        if (mLocation != null)
            loadWeatherData();
        if(mCurrUser != null)
            loadCurrUserData();
    }

    public static synchronized MainRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MainRepository(application);
        }
        return instance;
    }

    public void setLocation(String location) {
        mLocation = location;
        loadWeatherData();
    }

    public void setCurrUser(int weight, String birthdate, String location,
                            int height, String name, short sex, boolean activity,
                            String thumbnailString){
        mCurrUser = new UserInfo(weight, birthdate, location, height, name, sex, activity, thumbnailString);
        loadCurrUserData();
        //Insert user into DB
        insert(weight, height, birthdate, location, name, sex, activity, thumbnailString);
    }

    public void setCurrUser(String userID) {
        // Populates currUserTable LiveData Object
        mUserID = userID;
        selectUser(userID);
    }

    private void insert(int weight, int height, String birthdate, String location, String name, short sex, boolean activity, String thumbnailString) {
        if(mCurrUser != null) {
            UserTable userTable = new UserTable(mUserID, weight, height, birthdate, location, name, sex, activity, thumbnailString);
            RoomDB.databaseExecutor.execute(() -> {
                mDao.insert(userTable);
            });
        }
    }

    private void selectUser(String userID) {
        RoomDB.databaseExecutor.execute(() -> {
            currUserTable = mDao.getUser(userID);
        });
    }

    public MutableLiveData<WeatherData> getWeatherData() { return  jsonData; }
    public MutableLiveData<UserInfo> getCurrUserData() { return currUserData; }
    public LiveData<UserTable> getCurrUserTable() { return currUserTable; }

    private void loadWeatherData() { new FetchWeatherTask().execute(mLocation); }

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
            mainThreadHandler.post(() -> {
                    if (jsonWeatherData != null) {
                        try {
                            jsonData.setValue(JSONWeatherUtils.getWeatherData(jsonWeatherData));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            });
        }
    }

}

