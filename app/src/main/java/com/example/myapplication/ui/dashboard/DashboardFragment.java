package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.os.Debug;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

    private EditText weightLossPicker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Get the text views
        TextView tvBMI = (TextView) root.findViewById(R.id.tv_BMI_data);
        TextView tvBMR = (TextView) root.findViewById(R.id.tv_BMR_data);
        tvCalories = (TextView) root.findViewById(R.id.text_calories);
        TextView tvName = (TextView) root.findViewById(R.id.tv_name_data);

        try {
            bmi = getArguments().getFloat("BMI_DATA");
            bmr = getArguments().getFloat("BMR_DATA");
            name = getArguments().getString("NAME_DATA");
            tvBMI.setText("" + Math.round(bmi));
            tvBMR.setText("" + Math.round(bmr));
            tvName.setText("Hello " + name + "!");
        } catch (Exception e) {
            String error = e.getMessage();
        }

        weightLossPicker = root.findViewById(R.id.et_weight);
        weightLossPicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                float weightChange = Float.parseFloat(textView.getText().toString());
                tvCalories.setText("" + calculateTargetCalories(weightChange));
                return true;
            }
        });
        return root;
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