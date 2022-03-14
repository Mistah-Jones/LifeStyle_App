package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private TextView tvCalories;

    private float bmr;
    private float bmi;
    private String name;
    private float weightChange;

    private EditText weightLossPicker;

    // The data passer between the fragment and the main activity
    DashboardFragment.OnGoalDataPass mDataPasser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // TODO - remove icon from action bar - not working even though it works for UserInfoFragment
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false); // remove the icon

        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Get the text views / edit texts
        TextView tvBMI = (TextView) root.findViewById(R.id.tv_BMI_data);
        TextView tvBMR = (TextView) root.findViewById(R.id.tv_BMR_data);
        tvCalories = (TextView) root.findViewById(R.id.text_calories);
        TextView tvName = (TextView) root.findViewById(R.id.tv_name_data);
        weightLossPicker = root.findViewById(R.id.et_weight);

        try {
            bmi = getArguments().getFloat("BMI_DATA");
            bmr = getArguments().getFloat("BMR_DATA");
            name = getArguments().getString("NAME_DATA");
            weightChange = getArguments().getFloat("WEIGHT_CHANGE_DATA");

            tvBMI.setText("" + Math.round(bmi));
            tvBMR.setText("" + Math.round(bmr));
            tvName.setText("Hello " + name + "!");
            tvCalories.setText("" + calculateTargetCalories(weightChange));
            weightLossPicker.setText("" + weightChange);

        } catch (Exception e) {
            String error = e.getMessage();
        }

        weightLossPicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                weightChange = Float.parseFloat(textView.getText().toString());
                tvCalories.setText("" + calculateTargetCalories(weightChange));

                // Send the data to the main activity to store
                String[] data = {"" + weightChange};
                mDataPasser.onGoalDataPass(data);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weightLossPicker.getWindowToken(), 0);

                return true;
            }
        });

        return root;
    }

    //Callback interface
    public interface OnGoalDataPass{
        public void onGoalDataPass(String[] data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mDataPasser = (DashboardFragment.OnGoalDataPass) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnUserDataPass");
        }
    }

    @Override
    public void onDestroyView() {
        // Save all relevant data that we want here before closing out the screen
        super.onDestroyView();
        binding = null;
    }

    private float calculateTargetCalories(float weightChangePerWeek)
    {
        float calories = weightChangePerWeek + (weightChangePerWeek * 3500f) / 7f;
        return Math.round(Math.round(bmr) + Math.round(calories));
    }
}