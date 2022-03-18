package com.example.myapplication.ui.dashboard;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.example.myapplication.ui.userInfo.UserInfoFragment;
import com.google.android.material.snackbar.Snackbar;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private TextView tvCalories;

    private float bmr;
    private float bmi;
    private String name;
    private float weightChange;
    private float calorieGoal;
    private short gender;

    private EditText weightLossPicker;
    private NavController navController;

    // The data passer between the fragment and the main activity
    DashboardFragment.OnGoalDataPass mDataPasser;
    DashboardFragment.OnEdit mEditPasser;

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
            gender = getArguments().getShort("GENDER_DATA");
            weightChange = getArguments().getFloat("WEIGHT_CHANGE_DATA");
            calorieGoal = calculateTargetCalories(weightChange);

            tvBMI.setText("" + Math.round(bmi));
            tvBMR.setText("" + Math.round(bmr));
            tvName.setText("Hello " + name + "!");
            tvCalories.setText("" + calorieGoal);
            weightLossPicker.setText("" + weightChange);

        } catch (Exception e) {
            String error = e.getMessage();
        }

        weightLossPicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                weightChange = Float.parseFloat(textView.getText().toString());
                calorieGoal = calculateTargetCalories(weightChange);
                tvCalories.setText("" + calorieGoal);

                // Send the data to the main activity to store
                String[] data = {"" + weightChange};
                mDataPasser.onGoalDataPass(data);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weightLossPicker.getWindowToken(), 0);

                boolean wc = (weightChange > 2 | weightChange < -2);
                boolean cm = (calorieGoal < 1200 && gender == 1);
                boolean cf = (calorieGoal < 1000 && gender == 2);
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

    //Callback interface
    public interface OnGoalDataPass{
        public void onGoalDataPass(String[] data);
    }

    //Callback interface
    public interface OnEdit{
        public void onEdit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mDataPasser = (DashboardFragment.OnGoalDataPass) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnUserDataPass");
        }
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

    private float calculateTargetCalories(float weightChangePerWeek)
    {
        float calories = weightChangePerWeek + (weightChangePerWeek * 3500f) / 7f;
        return Math.round(Math.round(bmr) + Math.round(calories));
    }
}