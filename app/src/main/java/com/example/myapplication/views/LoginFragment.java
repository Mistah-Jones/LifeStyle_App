package com.example.myapplication.views;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.models.UserInfo;
import com.example.myapplication.viewmodels.MainViewModel;

public class LoginFragment extends Fragment {

    private MainViewModel mViewModel;
    private EditText mEtUserName;
    private EditText mEtPassword;
    private String mUserID;
    private String mPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Create View Model
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set Observer
        mViewModel.getCurrUserData().observe(getViewLifecycleOwner(), observer);

        // Set OnClick listener for button
        Button submit = view.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Get EditText
                mEtUserName = view.findViewById(R.id.et_userName);
                mEtPassword = view.findViewById(R.id.et_password);
                mUserID = mEtUserName.getText().toString();
                mPassword = mEtPassword.getText().toString();
                // Check if there's an input
                if (mUserID.equals("") || mPassword.equals("")) {
                    // TODO: Insert error or toast
                }
                else {
                    try {
                        loadUser(mUserID, mPassword);
                        Navigation.findNavController(v).navigate(R.id.navigation_userinfo);
                    }
                    // Couldn't load user
                    catch (Exception e) {
                        e.printStackTrace();
                        Navigation.findNavController(v).navigate(R.id.navigation_userinfo);
                    }
                }
            }
        });

        return view;
    }

    // Navigate to dashboard if there is userInfo object
    final Observer<UserInfo> observer = new Observer<UserInfo>() {
        @Override
        public void onChanged(@Nullable final UserInfo userInfo) {
            // Update UI
            if (userInfo != null) {
                Navigation.findNavController(getView()).navigate(R.id.navigation_dashboard);
            }
        }
    };

    void loadUser(String userID, String password) {
        mViewModel.setCurrUser(userID, password);
    }
}