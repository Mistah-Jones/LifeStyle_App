package com.example.myapplication.views;

import static androidx.navigation.Navigation.findNavController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.example.myapplication.models.UserInfo;
import com.example.myapplication.viewmodels.MainViewModel;
import com.example.myapplication.weatherbackend.NetworkUtils;
import com.example.myapplication.weatherbackend.WeatherData;
import com.google.android.material.snackbar.Snackbar;

public class DashboardFragment extends Fragment {

    private MainViewModel mViewModel;
    private FragmentDashboardBinding binding;
    private View root;

    private NavController navController;

    private TextView tvBMI;
    private TextView tvBMR;
    private TextView tvCalories;
    private TextView tvName;
    private EditText weightLossPicker;

    // The data passer between the fragment and the main activity
    DashboardFragment.OnEdit mEditPasser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // TODO - remove icon from action bar - not working even though it works for UserInfoFragment
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false); // remove the icon

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //Get the text views / edit texts
        tvBMI = (TextView) root.findViewById(R.id.tv_BMI_data);
        tvBMR = (TextView) root.findViewById(R.id.tv_BMR_data);
        tvCalories = (TextView) root.findViewById(R.id.text_calories);
        tvName = (TextView) root.findViewById(R.id.tv_name_data);
        weightLossPicker = root.findViewById(R.id.et_weight);

        mViewModel.getCurrUserData().observe(getViewLifecycleOwner(), observer);

        weightLossPicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                float weightChange = Float.parseFloat(textView.getText().toString());
                float calorieGoal = mViewModel.getCurrUserData().getValue().calculateTargetCalories();
                short gender = mViewModel.getCurrUserData().getValue().getGender();

                tvCalories.setText("" + calorieGoal);
                mViewModel.getCurrUserData().getValue().setWeightchange(weightChange);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weightLossPicker.getWindowToken(), 0);

                //alert the user if it is an unhealthy goal
                boolean wc = (weightChange > 2 | weightChange < -2);
                boolean cm = (calorieGoal < 1200 && gender == 1);
                boolean cf = (calorieGoal < 1000 && gender == 2);
                alertIfUnhealthy(wc, cm, cf);

                return true;
            }
        });

        //The edit button
        Button editBttn = root.findViewById(R.id.b_edituser);
        editBttn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mEditPasser.onEdit();
            }
        });

        return root;
    }

    final Observer<UserInfo> observer = new Observer<UserInfo>() {
        @Override
        public void onChanged(@Nullable final UserInfo userInfo) {
            // Update UI
            if (userInfo != null) {
                tvBMI.setText("" + Math.round(mViewModel.getCurrUserData().getValue().getBmi()));
                tvBMR.setText("" + Math.round(mViewModel.getCurrUserData().getValue().getBmr()));
                tvName.setText("Hello " + mViewModel.getCurrUserData().getValue().getName() + "!");
                tvCalories.setText("" + mViewModel.getCurrUserData().getValue().calculateTargetCalories());
                weightLossPicker.setText("" + mViewModel.getCurrUserData().getValue().getWeightchange());
            }
        }
    };

    //Callback interface
    public interface OnEdit{
        public void onEdit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mEditPasser = (DashboardFragment.OnEdit) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnEdit");
        }
    }

    @Override
    public void onDestroyView() {
        // Save all relevant data that we want here before closing out the screen
        super.onDestroyView();
        binding = null;
    }

    private void alertIfUnhealthy(boolean wc,boolean cm, boolean cf ){
        if (wc | cm | cf) {

            String mess = "This is an unhealthy goal. ";
            if(cm) {
                if(wc) mess += "It is recommended to consume no less than 1200 calories for men and to avoid weight changes of +/- 2 pounds.";
                else mess += "It is recommended to consume no less than 1200 calories for men";

            }
            else if(cf) {
                if(wc) mess += "It is recommended to consume no less than 1000 calories for women and to avoid weight changes of +/- 2 pounds.";
                else mess += "It is recommended to consume no less than 1000 calories for women";

            }
            else if(wc) mess += "It is recommended to stay in the range of losing/gaining no more than 2 pounds.";


            CoordinatorLayout cl = (CoordinatorLayout) root.findViewById(R.id.cl);
            cl.bringToFront();
            Snackbar snackbar = Snackbar.make(cl, mess, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellow));
            TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            tv.setMaxLines(4);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snackbar.show();
        }
    }

}