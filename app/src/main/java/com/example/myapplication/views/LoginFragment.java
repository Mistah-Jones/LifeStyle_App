package com.example.myapplication.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.viewmodels.MainViewModel;

public class LoginFragment extends Fragment {

    private MainViewModel mViewModel;
    private EditText mEtUserName;
    private String mUserID;

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

        // Set OnClick listener for button
        Button submit = view.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Get EditText
                mEtUserName = view.findViewById(R.id.et_userName);
                mUserID = mEtUserName.getText().toString();
                // Check if there's an input
                if (mUserID.equals("")) {
                    // TODO: Insert error or toast
                }
                else {
                    try {
                        loadUser(mUserID);
                        Navigation.findNavController(v).navigate(R.id.navigation_dashboard);
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

    void loadUser(String userID) {
        mViewModel.setCurrUser(userID);
    }
}