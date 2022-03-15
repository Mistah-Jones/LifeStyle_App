package com.example.myapplication.ui.weather;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.weatherbackend.JSONWeatherUtils;
import com.example.myapplication.weatherbackend.NetworkUtils;
import com.example.myapplication.weatherbackend.WeatherData;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherFragment extends Fragment {

    private WeatherViewModel mViewModel;

    private TextView mTvLocation;
    private TextView mTvTemp;
    private TextView mTvPress;
    private TextView mTvHum;
    private TextView mTvMaxTemp;
    private TextView mTvMinTemp;
    private WeatherData mWeatherData;
    private String city;
    private static FetchWeatherTask mFetchWeatherTask = new FetchWeatherTask();

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the Layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        // Get the edit / view texts
        mTvLocation = view.findViewById(R.id.tv_location);
        mTvTemp = (TextView) view.findViewById(R.id.tv_temp);
        mTvPress = (TextView) view.findViewById(R.id.tv_pressure);
        mTvHum = (TextView) view.findViewById(R.id.tv_humidity);
        mTvMaxTemp = (TextView) view.findViewById(R.id.tv_max_temp);
        mTvMinTemp = (TextView) view.findViewById(R.id.tv_min_temp);

        // Get the data passed from the Main Activity
        try {
            city = getArguments().getString("CITY_DATA");
            mTvLocation.setText(city);
        } catch (Exception e) {
            String error = e.getMessage();
        }

        // Check if there is any saved data from the fragment's current life cycle
        if(savedInstanceState!=null) {
            String temp = savedInstanceState.getString("tvTemp");
            String hum = savedInstanceState.getString("tvHum");
            String press = savedInstanceState.getString("tvPress");
            String tmax = savedInstanceState.getString("tvMaxTemp");
            String tmin = savedInstanceState.getString("tvMinTemp");
            if (temp != null)
                mTvTemp.setText(""+temp);
            if (hum != null)
                mTvHum.setText(""+hum);
            if (press != null)
                mTvPress.setText(""+press);
            if (tmax != null)
                mTvPress.setText(""+tmax);
            if (tmin != null)
                mTvPress.setText(""+tmin);
        }
        // If there is no weather data saved, fetch it
        else
        {
            String inputFromEt = mTvLocation.getText().toString().replace(' ','&');
            loadWeatherData(inputFromEt);
        }
        mFetchWeatherTask.setWeakReference(this); //make sure we're always pointing to current version of fragment

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tvTemp",mTvTemp.getText().toString());
        outState.putString("tvHum",mTvHum.getText().toString());
        outState.putString("tvPress",mTvPress.getText().toString());
        outState.putString("tvMaxTemp",mTvMaxTemp.getText().toString());
        outState.putString("tvMinTemp",mTvMinTemp.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        // TODO: Use the ViewModel
    }
    private void loadWeatherData(String location){
        mFetchWeatherTask.execute(this,location);
    }
    private static class FetchWeatherTask{
        WeakReference<WeatherFragment> weatherFragmentWeakReference;
        private ExecutorService executorService = Executors.newSingleThreadExecutor();
        private Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        public void setWeakReference(WeatherFragment ref)
        {
            weatherFragmentWeakReference = new WeakReference<WeatherFragment>(ref);
        }
        public void execute(WeatherFragment ref, String location){

            executorService.execute(new Runnable(){
                @Override
                public void run(){
                    String jsonWeatherData;
                    URL weatherDataURL = NetworkUtils.buildURLFromString(location);
                    jsonWeatherData = null;
                    try{
                        jsonWeatherData = NetworkUtils.getDataFromURL(weatherDataURL);
                        postToMainThread(jsonWeatherData);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        private void postToMainThread(String jsonWeatherData)
        {
            WeatherFragment localRef = weatherFragmentWeakReference.get();
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (jsonWeatherData != null) {
                        try {
                            localRef.mWeatherData = JSONWeatherUtils.getWeatherData(jsonWeatherData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (localRef.mWeatherData != null) {
                            localRef.mTvTemp.setText("" + Math.round(localRef.mWeatherData.getTemperature().getTemp() - 273.15) + " C");
                            localRef.mTvHum.setText("" + localRef.mWeatherData.getCurrentCondition().getHumidity() + "%");
                            localRef.mTvPress.setText("" + localRef.mWeatherData.getCurrentCondition().getPressure() + " hPa");
                            localRef.mTvMaxTemp.setText("" + Math.round(localRef.mWeatherData.getTemperature().getMaxTemp() - 273.15) + " C");
                            localRef.mTvMinTemp.setText("" + Math.round(localRef.mWeatherData.getTemperature().getMinTemp() - 273.15) + " C");
                        }
                    }
                }
            });
        }
    }
}