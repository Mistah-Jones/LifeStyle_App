package com.example.myapplication.ui.userInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.text.InputType;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserinfoBinding;

public class UserInfoFragment extends Fragment {

    private UserInfoViewModel homeViewModel;
    private FragmentUserinfoBinding binding;

    // Name Information
    private EditText mEtName;
    private String mStringName;

    // Image Information
    private Button mButtonCamera;
    private ImageView mIvThumbnail;

    // Birth date information
    private EditText datePickerEText;
    private DatePickerDialog picker;

    private String[] heights = { "3 ft 0 in", "3 ft 1 in", "3 ft 2 in", "3 ft 3 in", "3 ft 4 in", "3 ft 5 in", "3 ft 6 in",
            "3 ft 7 in", "3 ft 8 in", "3 ft 9 in", "3 ft 10 in", "3 ft 11 in", "4 ft 0 in", "4 ft 1 in", "4 ft 2 in",
            "4 ft 3 in", "4 ft 4 in", "4 ft 5 in", "4 ft 6 in", "4 ft 7 in", "4 ft 8 in", "4 ft 9 in", "4 ft 10 in",
            "4 ft 11 in", "5 ft 0 in", "5 ft 1 in", "5 ft 2 in", "5 ft 3 in", "5 ft 4 in", "5 ft 5 in", "5 ft 6 in",
            "5 ft 7 in", "5 ft 8 in", "5 ft 9 in", "5 ft 10 in", "5 ft 11 in", "6 ft 0 in", "6 ft 1 in", "6 ft 2 in",
            "6 ft 3 in", "6 ft 4 in", "6 ft 5 in", "6 ft 6 in", "6 ft 7 in", "6 ft 8 in", "6 ft 9 in", "6 ft 10 in",
            "6 ft 11 in" };

    // Weight information
    private EditText weightPicker;

    //Define a bitmap
    Bitmap mThumbnailImage;
    OnUserDataPass mDataPasser;

    //Define a request code for the camera
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Callback interface
    public interface OnUserDataPass{
        public void onUserDataPass(String[] data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mDataPasser = (OnUserDataPass) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnUserDataPass");
        }
    }

    // TODO: Break into helper methods!
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(UserInfoViewModel.class);

        binding = FragmentUserinfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // The Profile Picture Field
        //The button press should open a camera
        mButtonCamera = (Button) root.findViewById(R.id.button_pic);
        //TODO: Put a placeholder image in the empty thumbnail before the user takes a picture
        mIvThumbnail = (ImageView) root.findViewById(R.id.iv_pic);

        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //The button press should open a camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
            }
        );

        // The name Field
        mEtName = (EditText) root.findViewById(R.id.et_name);

        // The Birthday Field
        //TODO: Add spinner instead of calendar?
        datePickerEText=(EditText) root.findViewById(R.id.et_date);
        datePickerEText.setInputType(InputType.TYPE_NULL);
        datePickerEText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                datePickerEText.setText( (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        // The Height Field
        //TODO: Replace hard-coded strings with two integer spinners for feet and inches
        //TODO: Add prompt to spinner dialog
        Spinner heightSP = root.findViewById(R.id.sp_height);
        ArrayAdapter ad
                = new ArrayAdapter(
                getActivity(),
                android.R.layout.simple_spinner_item,
                heights);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        heightSP.setAdapter(ad);

        // The Weight Field
        weightPicker = root.findViewById(R.id.et_weight);

        // The Gender Field
        RadioGroup radioGroupGender = (RadioGroup) root.findViewById(R.id.rg_gender);

        // The Activity Level Field
        RadioGroup radioGroupActivity = (RadioGroup) root.findViewById(R.id.rg_activity);

        // The Submit Button
        Button submitBttn = root.findViewById(R.id.button_submit);
        submitBttn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Add warning messages about empty fields

                // Get selected radio button from gender group
                int genderSelectedId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton genderBttn = (RadioButton) root.findViewById(genderSelectedId);
                if (genderBttn.getText().charAt(0) == 'M')
                {
                    genderSelectedId = 1;
                }
                else if (genderBttn.getText().charAt(0) == 'F')
                {
                    genderSelectedId = 2;
                }

                // Get selected radio button from activity group
                int activitySelectedId = radioGroupActivity.getCheckedRadioButtonId();
                RadioButton activityBttn = (RadioButton) root.findViewById(activitySelectedId);
                String isActive = "";
                if (activityBttn.getText().charAt(0) == 'A')
                {
                    isActive = "True";
                }
                else if (activityBttn.getText().charAt(0) == 'S')
                {
                    isActive = "False";
                }

                // Send the inputted data
                String[] data = {mEtName.getText().toString(),
                        datePickerEText.getText().toString(),
                        "" + convertHeightToInches(heightSP.getSelectedItem().toString()),
                        weightPicker.getText().toString(),
                        "" + genderSelectedId,
                        isActive};

                mDataPasser.onUserDataPass(data);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){

            //Get the bitmap
            Bundle extras = data.getExtras();
            mThumbnailImage = (Bitmap) extras.get("data");

            //Open a file and write to it
            if(isExternalStorageWritable()){
                String filePathString = saveImage(mThumbnailImage);
                //mDisplayIntent.putExtra("imagePath",filePathString);
            }
            else{
                Toast.makeText(getActivity(),"External storage not writable.",Toast.LENGTH_SHORT).show();
            }
            mIvThumbnail.setImageBitmap(mThumbnailImage);

        }
    }

    /*
    A helper method to convert the current height string format into an integer
    format of inches.
     */
    private int convertHeightToInches(String heightString)
    {
        heightString = heightString.replaceAll(" ft", "").replaceAll(" in","");
        String[] ftAndIn = heightString.split(" ");
        return (Integer.parseInt(ftAndIn[0]) * 12) + Integer.parseInt(ftAndIn[1]);
    }

    private String saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Thumbnail_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(getActivity(),"file saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}