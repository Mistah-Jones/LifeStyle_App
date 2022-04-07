package com.example.myapplication;

import static androidx.navigation.Navigation.findNavController;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.views.DashboardFragment;
import com.example.myapplication.views.UserInfoFragment;
import com.example.myapplication.views.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.Period;

public class MainActivity extends AppCompatActivity
                implements UserInfoFragment.OnUserDataPass, DashboardFragment.OnGoalDataPass, DashboardFragment.OnEdit {

    private ActivityMainBinding binding;

    // User Information
    private String mName = null;
    private int mAge;
    private String mBirthDay;
    private int mWeight, mHeight;
    private short mGender;
    private boolean mActivity;
    private String mThumbnailString;
    private Bitmap mThumbnail;
    private String mCity;

    // Calculated Information
    private float bmi;
    private float bmr;

    // Weight Goal Data
    private float mWeightChange;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.action_bar, null);
        actionBar.setCustomView(view);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setBackground(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_weather, R.id.fab, R.id.navigation_hike)
                .build();
        navController = findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(buttonClickListener);

        BottomNavigationItemView weather = findViewById(R.id.navigation_weather);
        weather.setOnClickListener(buttonClickListener);

    }

    @Override
    public void onUserDataPass(String[] data) {
        mName = data[0];
        mBirthDay = data[1];
        mAge = getAge(data[1]);
        mHeight = Integer.parseInt(data[2]);
        mWeight = Integer.parseInt(data[3]);
        mGender = Short.parseShort(data[4]);
        mActivity = Boolean.parseBoolean(data[5]);
        mThumbnailString = data[6];
        mCity = data[7];

        // get thumbnail image and decode it
        byte [] encodeByte= Base64.decode(mThumbnailString,Base64.DEFAULT);
        mThumbnail = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        // convert thumbnail image to circle shaped version
        Bitmap output = Bitmap.createBitmap(mThumbnail.getWidth(),
                mThumbnail.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, mThumbnail.getWidth(), mThumbnail.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(mThumbnail.getWidth() / 2, mThumbnail.getHeight() / 2,
                mThumbnail.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mThumbnail, rect, rect, paint);
        mThumbnail = output;

        // convert circle thumbnail to drawable and assign to fab
        Drawable d = new BitmapDrawable(getResources(), mThumbnail);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(d);

        // BMI Calculation
        bmi = (float)mWeight / (float)Math.pow(mHeight,2);
        bmi *= 703;

        // BMR Calculation (Harris-Benedict Equation)
        bmr = 0f;
        switch (mGender)
        {
            // Male
            case 1:
                bmr = 66.47f + (6.24f * mWeight) + (12.7f * mHeight) - (6.755f * mAge);
                break;
            // Female
            case 2:
                bmr = 65.51f + (4.35f * mWeight) + (4.7f * mHeight) - (4.7f * mAge);
                break;
            // Non-Binary
            default:
                // Linearly interpolated values between the two equations
                bmr = 65.99f + (5.295f * mWeight) + (8.7f * mHeight) - (5.7275f * mAge);
                break;
        }
        // Since we are only using BMR for weight-loss calculations, we
        // include the activity level modifier in the BMR calculation.
        if (mActivity)
        {
            bmr *= 1.55;
        }
        else
        {
            bmr *= 1.175;
        }

        // Instantiate the fragment
        DashboardFragment dashboardFragment = new DashboardFragment();

        // Send data to fragment
        Bundle sentData = new Bundle();
        sentData.putFloat("BMI_DATA", bmi);
        sentData.putFloat("BMR_DATA", bmr);
        sentData.putString("NAME_DATA", mName);
        sentData.putShort("GENDER_DATA", mGender);

        navController.navigate(R.id.navigation_dashboard, sentData);
    }


    @Override
    public void onGoalDataPass(String[] data) {
        mWeightChange = Float.parseFloat(data[0]);
    }

    @Override
    public void onEdit() {
        UserInfoFragment userInfoFragment = new UserInfoFragment();

        Bundle sentData = new Bundle();
        sentData.putString("NAME_DATA", mName);
        sentData.putShort("GENDER_DATA", mGender);
        sentData.putString("BIRTH_DATA", mBirthDay);
        sentData.putInt("WEIGHT_DATA", mWeight);
        sentData.putInt("HEIGHT_DATA", mHeight);
        sentData.putBoolean("ACTIVITY_DATA", mActivity);
        sentData.putString("LOCATION_DATA", mCity);
        sentData.putString("THUMBNAIL_DATA", mThumbnailString);

        navController.navigate(R.id.navigation_userinfo, sentData);

    }

    // TODO: Update API version minimum to 26 to use LocalDate
    private int getAge(String birthDate)
    {
        //DateTime.Parse(birthDate);
        String[] dateParts = birthDate.split("/");
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1])); // Birth date

        Period p = Period.between(birthday, today);

        return p.getYears();
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    // If the user has not input any data, take them to the user info fragment.
                    if (mName == null) {
                        navController.navigate(R.id.navigation_userinfo);
                    } else {
                        // Instantiate the fragment
                        DashboardFragment dashboardFragment = new DashboardFragment();

                        // Send data to fragment
                        Bundle sentData = new Bundle();
                        sentData.putFloat("BMI_DATA", bmi);
                        sentData.putFloat("BMR_DATA", bmr);
                        sentData.putString("NAME_DATA", mName);
                        sentData.putShort("GENDER_DATA", mGender);
                        sentData.putFloat("WEIGHT_CHANGE_DATA", mWeightChange);


                        navController.navigate(R.id.navigation_dashboard, sentData);
                    }
                    break;
                case R.id.navigation_weather:
                    // If the user has not input any data, we don't want to open the weather fragment
                    // (We need the city for this fragment to function)
                    if (mName != null) {
                        // Instantiate the fragment
                        WeatherFragment weatherFragment = new WeatherFragment();

                        // Send data to fragment
                        Bundle sentData = new Bundle();
                        sentData.putString("CITY_DATA", mCity);

                        navController.navigate(R.id.navigation_weather, sentData);
                    }
            }
        }
    };
}