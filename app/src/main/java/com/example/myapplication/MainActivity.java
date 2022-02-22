package com.example.myapplication;

import static androidx.navigation.Navigation.findNavController;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.dashboard.DashboardFragment;
import com.example.myapplication.ui.userInfo.UserInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.Period;

public class MainActivity extends AppCompatActivity
                implements UserInfoFragment.OnUserDataPass{

    private ActivityMainBinding binding;
    private String mName = null;
    private int mAge;
    private int mWeight, mHeight;
    private short mGender;
    private boolean mActivity;
    private String mThumbnailString;
    private Bitmap mThumbnail;

    private float bmi;
    private float bmr;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setBackground(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.fab, R.id.navigation_hike)
                .build();
        navController = findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user has not input any data, take them to the user info fragment.
                if (mName == null)
                {
                    navController.navigate(R.id.navigation_home);
                }
                else
                {
                    // Instantiate the fragment
                    DashboardFragment dashboardFragment = new DashboardFragment();

                    // Send data to fragment
                    Bundle sentData = new Bundle();
                    sentData.putFloat("BMI_DATA", bmi);
                    sentData.putFloat("BMR_DATA", bmr);

                    navController.navigate(R.id.navigation_dashboard, sentData);
                }
            }
        });
    }

    @Override
    public void onUserDataPass(String[] data) {
        mName = data[0];
        mAge = getAge(data[1]);
        mHeight = Integer.parseInt(data[2]);
        mWeight = Integer.parseInt(data[3]);
        mGender = Short.parseShort(data[4]);
        mActivity = Boolean.parseBoolean(data[5]);
        mThumbnailString = data[6];

        byte [] encodeByte= Base64.decode(mThumbnailString,Base64.DEFAULT);
        mThumbnail = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
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

        navController.navigate(R.id.navigation_dashboard, sentData);
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
}