package com.example.myapplication.views;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.UserInfo;
import com.example.myapplication.viewmodels.MainViewModel;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    private MainViewModel mViewModel;
    private EditText mEtUserName;
    private EditText mEtPassword;
    private String mUserID;
    private String mPassword;
    private View root;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_login, container, false);

        // Create View Model
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set Observer
        mViewModel.getCurrUserData().observe(getViewLifecycleOwner(), observer);

        // Set OnClick listener for button
        Button submit = root.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Get EditText
                mEtUserName = root.findViewById(R.id.et_userName);
                mEtPassword = root.findViewById(R.id.et_password);
                mUserID = mEtUserName.getText().toString();
                mPassword = mEtPassword.getText().toString();
                // Check if there's an input
                if (mUserID.equals("") || mPassword.equals("")) {
                    String message = "Enter in Both fields";
                    CoordinatorLayout cl = (CoordinatorLayout) root.findViewById(R.id.cl_login);
                    cl.bringToFront();
                    Snackbar snackbar = Snackbar.make(cl, message, Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellow));
                    TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();
                }
                else {
                    try {
                        loadUser(mUserID, mPassword);
                    }
                    // Couldn't load user
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return root;
    }

    // Navigate to dashboard if there is userInfo object
    final Observer<UserInfo> observer = new Observer<UserInfo>() {
        @Override
        public void onChanged(@Nullable final UserInfo userInfo) {
            if(mViewModel.getCurrUserData().getValue() == null){
                setMessage("Create a New User Profile!");
                Navigation.findNavController(root).navigate(R.id.navigation_userinfo);
            }
            else {
                setMessage("Welcome Back!");
                Navigation.findNavController(root).navigate(R.id.navigation_dashboard);
            }
        }
    };

    void loadUser(String userID, String password) { mViewModel.setCurrUser(userID, password); }

    private void setMessage(String message) { mViewModel.setMessage(message); }
}