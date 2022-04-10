package com.example.myapplication.views;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.models.UserInfo;
import com.example.myapplication.viewmodels.MainViewModel;
import com.example.myapplication.weatherbackend.NetworkUtils;
import com.example.myapplication.weatherbackend.WeatherData;

public class WeatherFragment extends Fragment {

    private MainViewModel mViewModel;

    private TextView mTvLocation;
    private TextView mTvTemp;
    private TextView mTvPress;
    private TextView mTvHum;
    private TextView mTvMaxTemp;
    private TextView mTvMinTemp;
    private ImageView mIvIcon;

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
        mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);

        // Create View Model
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set Observer
        mViewModel.getWeatherData().observe(getViewLifecycleOwner(), observerWeather);
        mViewModel.getCurrUserData().observe(getViewLifecycleOwner(), observerUser);

        return view;
    }

    final Observer<WeatherData> observerWeather = new Observer<WeatherData>() {
        @Override
        public void onChanged(@Nullable final WeatherData weatherData) {
            // Update UI
            if (weatherData != null) {
                mTvTemp.setText("" + Math.round(weatherData.getTemperature().getTemp() - 273.15) + " C");
                mTvHum.setText("" + weatherData.getCurrentCondition().getHumidity() + "%");
                mTvPress.setText("" + weatherData.getCurrentCondition().getPressure() + " hPa");
                mTvMaxTemp.setText("" + Math.round(weatherData.getTemperature().getMaxTemp() - 273.15) + " C");
                mTvMinTemp.setText("" + Math.round(weatherData.getTemperature().getMinTemp() - 273.15) + " C");

                try {
                    new NetworkUtils.DownloadImageTask((ImageView) mIvIcon)
                            .execute(weatherData.getCurrentCondition().getIconCode());
                    // Save Bitmap to WeatherData class
                    weatherData.getCurrentCondition().setIcon(mIvIcon.getDrawingCache());
                }
                // If getting image from web fails, set image using a vector
                catch (Exception e){
                    SetIcon(mIvIcon, weatherData.getCurrentCondition().getCondition());
                    e.printStackTrace();
                }
            }
        }
    };

    final Observer<UserInfo> observerUser = new Observer<UserInfo>() {
        @Override
        public void onChanged(@Nullable final UserInfo userInfo) {
            // Update UI
            if (userInfo != null) {
                String city = userInfo.getLocation();
                mTvLocation.setText(city);
                loadWeatherData(city);

            }
        }
    };

    // Pass location to view model
    void loadWeatherData (String location) {
        mViewModel.setLocation(location);
    }

    // Sets the weather imageview icon to one of five basic vectors
    private static void SetIcon(ImageView mIvIcon, String icon) {
        switch(icon) {
            case "Thunderstorm": { mIvIcon.setImageResource(R.drawable.ic_lightning); break; }
            case "Snow": { mIvIcon.setImageResource(R.drawable.ic_snow); break; }
            case "Rain": { mIvIcon.setImageResource(R.drawable.ic_rain); break; }
            case "Clear": { mIvIcon.setImageResource(R.drawable.ic_sun); break; }
            default: { mIvIcon.setImageResource(R.drawable.ic_cloud); }
        }
    }
}