package com.example.myapplication;

import static androidx.navigation.Navigation.findNavController;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.viewmodels.MainViewModel;
import com.example.myapplication.views.DashboardFragment;
import com.example.myapplication.views.UserInfoFragment;
import com.example.myapplication.views.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity
                implements UserInfoFragment.OnUserDataPass, DashboardFragment.OnEdit {

    private ActivityMainBinding binding;
    private MainViewModel mViewModel;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

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
    public void onUserDataPass() {

        Bitmap mThumbnail = mViewModel.getCurrUserData().getValue().getThumbnail();

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
        canvas.drawCircle(mThumbnail.getWidth() / 2f, mThumbnail.getHeight() / 2f,
                mThumbnail.getWidth() / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mThumbnail, rect, rect, paint);
        mThumbnail = output;

        // convert circle thumbnail to drawable and assign to fab
        Drawable d = new BitmapDrawable(getResources(), mThumbnail);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(d);

        // Instantiate the fragment
        DashboardFragment dashboardFragment = new DashboardFragment();
        navController.navigate(R.id.navigation_dashboard);
    }

    @Override
    public void onEdit() {
        UserInfoFragment userInfoFragment = new UserInfoFragment();
        navController.navigate(R.id.navigation_userinfo);

    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    // If the user has not input any data, take them to the user info fragment.
                    if (mViewModel.getCurrUserData().getValue() == null) {
                        navController.navigate(R.id.navigation_userinfo);
                    } else {
                        // Instantiate the fragment
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        navController.navigate(R.id.navigation_dashboard);
                    }
                    break;
                case R.id.navigation_weather:
                    // If the user has not input any data, we don't want to open the weather fragment
                    // (We need the city for this fragment to function)
                    if (mViewModel.getCurrUserData().getValue() == null && mViewModel.getCurrUserData().getValue().getLocation() != null) {
                        // Instantiate the fragment
                        WeatherFragment weatherFragment = new WeatherFragment();
                        navController.navigate(R.id.navigation_weather);
                    }
            }
        }
    };
}