package com.example.myapplication.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserinfoBinding;
import com.example.myapplication.models.UserInfo;
import com.example.myapplication.viewmodels.MainViewModel;
import com.google.android.material.snackbar.Snackbar;

public class UserInfoFragment extends Fragment {

    private MainViewModel mViewModel;
    private FragmentUserinfoBinding binding;
    private View root;

    // Name Information
    private EditText mEtName;

    // Image Information
    private Button mButtonCamera;
    private ImageView mIvThumbnail;

    // Birth date information
    private EditText datePickerEText;
    private DatePickerDialog picker;

    // radio buttons
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupActivity;

    private Spinner heightSP;

    private final String[] heights = { "3 ft 0 in", "3 ft 1 in", "3 ft 2 in", "3 ft 3 in", "3 ft 4 in", "3 ft 5 in", "3 ft 6 in",
            "3 ft 7 in", "3 ft 8 in", "3 ft 9 in", "3 ft 10 in", "3 ft 11 in", "4 ft 0 in", "4 ft 1 in", "4 ft 2 in",
            "4 ft 3 in", "4 ft 4 in", "4 ft 5 in", "4 ft 6 in", "4 ft 7 in", "4 ft 8 in", "4 ft 9 in", "4 ft 10 in",
            "4 ft 11 in", "5 ft 0 in", "5 ft 1 in", "5 ft 2 in", "5 ft 3 in", "5 ft 4 in", "5 ft 5 in", "5 ft 6 in",
            "5 ft 7 in", "5 ft 8 in", "5 ft 9 in", "5 ft 10 in", "5 ft 11 in", "6 ft 0 in", "6 ft 1 in", "6 ft 2 in",
            "6 ft 3 in", "6 ft 4 in", "6 ft 5 in", "6 ft 6 in", "6 ft 7 in", "6 ft 8 in", "6 ft 9 in", "6 ft 10 in",
            "6 ft 11 in" };

    // Weight information
    private EditText weightPicker;

    // City information
    private EditText mEtCity;

    //Define a bitmap
    Bitmap mThumbnailImage;

    // The data passer between the fragment and the main activity
    OnUserDataPass mDataPasser;

    //Define a request code for the camera
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Callback interface
    public interface OnUserDataPass{
        void onUserDataPass();
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

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding = FragmentUserinfoBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        mViewModel.getCurrUserData().observe(getViewLifecycleOwner(), observer);

        //final TextView textView = binding.textHome;

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
         });

        // The name Field
        mEtName = (EditText) root.findViewById(R.id.et_name);

        // The Birthday Field
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
        heightSP = root.findViewById(R.id.sp_height);
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
        radioGroupGender = (RadioGroup) root.findViewById(R.id.rg_gender);

        // The Activity Level Field
        radioGroupActivity = (RadioGroup) root.findViewById(R.id.rg_activity);

        // The city Field
        mEtCity = (EditText) root.findViewById(R.id.et_city);

        // The Submit Button
        Button submitBttn = root.findViewById(R.id.button_submit);
        submitBttn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                boolean g= true, a= true, n= true, b= true, h= true, w = true, c = true, p = true;

                // Get selected radio button from gender group
                try {
                    int genderSelectedId = radioGroupGender.getCheckedRadioButtonId();
                    RadioButton genderBttn = (RadioButton) root.findViewById(genderSelectedId);
                    if(genderBttn == null)
                    {
                        g = false;
                    }
                    if(g) {
                        if (genderBttn.getText().charAt(0) == 'M') {
                            genderSelectedId = 1;
                        } else if (genderBttn.getText().charAt(0) == 'F') {
                            genderSelectedId = 2;
                        }

                    }

                    // Get selected radio button from activity group
                    int activitySelectedId = radioGroupActivity.getCheckedRadioButtonId();
                    RadioButton activityBttn = (RadioButton) root.findViewById(activitySelectedId);
                    String isActive = "";
                    if(activityBttn == null){
                        a = false;
                    }
                    if(a) {
                        if (activityBttn.getText().charAt(0) == 'A') {
                            isActive = "True";
                        } else if (activityBttn.getText().charAt(0) == 'S') {
                            isActive = "False";
                        }
                    }

                    if(mThumbnailImage == null) p = false;
                    if(mEtName.getText().toString().equals("")) n = false;
                    if(datePickerEText.getText().toString().equals("")) b = false;
                    if(heightSP.getSelectedItem().toString().equals("")) h = false;
                    if(weightPicker.getText().toString().equals("")) w = false;
                    if(mEtCity.getText().toString().equals("")) c = false;

                    //check if birth date is in the right form
                    String[] values = datePickerEText.getText().toString().split("/");
                    if(values.length != 3 ||
                            Integer.parseInt(values[0]) > 12 ||
                            Integer.parseInt(values[0]) <= 0 ||
                            Integer.parseInt(values[1]) > 31 ||
                            Integer.parseInt(values[1]) <= 0 ||
                            values[2].length() != 4 ) b = false;

                    // Send the inputted data
                    if(g && a && n && b && h && w && c && p) {
                        ByteArrayOutputStream baos =new  ByteArrayOutputStream();
                        mThumbnailImage.compress(Bitmap.CompressFormat.PNG,100, baos);
                        byte [] byteImage = baos.toByteArray();
                        String userPhotoString = Base64.encodeToString(byteImage, Base64.DEFAULT);
                        float weightChange = 0f;
                        if (mViewModel.getCurrUserData().getValue() != null)
                            weightChange = mViewModel.getCurrUserData().getValue().getWeightchange();
                        mViewModel.setCurrUser(Integer.parseInt(weightPicker.getText().toString()),
                                datePickerEText.getText().toString(),
                                mEtCity.getText().toString(),
                                convertHeightToInches(heightSP.getSelectedItem().toString()),
                                mEtName.getText().toString(),
                                (short)genderSelectedId,
                                Boolean.parseBoolean(isActive),
                                userPhotoString);
                        mViewModel.getCurrUserData().getValue().setThumbnail(mThumbnailImage);
                        mViewModel.getCurrUserData().getValue().setWeightchange(weightChange);
                        mDataPasser.onUserDataPass();
                    }
                    else{
                        String mess = "Please include the following: ";
                        if(!p) mess += "photo, ";
                        if(!g) mess += "gender, ";
                        if(!a) mess += "activity level, ";
                        if(!n) mess += "name, ";
                        if(!b) mess += "birthday, ";
                        if(!h) mess += "height, ";
                        if(!w) mess += "weight, ";
                        if(!c) mess += "city, ";

                        mess = mess.substring(0, mess.length()-2);
                        mess += ".";

                        Log.d("userinfofragment_error", mess);
                        CoordinatorLayout cl = (CoordinatorLayout) root.findViewById(R.id.cl);
                        cl.bringToFront();
                        Snackbar snackbar = Snackbar.make(cl, mess, Snackbar.LENGTH_LONG);
                        View view = snackbar.getView();
                        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellow));
                        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                        params.gravity = Gravity.TOP;
                        view.setLayoutParams(params);
                        snackbar.show();
                    }
                }
                catch (Exception e){
                    Log.d("userinfofragment_error", e.getMessage());
                    throw e;
                }
            }
        });

        return root;
    }

    final Observer<UserInfo> observer = new Observer<UserInfo>() {
        @Override
        public void onChanged(@Nullable final UserInfo userInfo) {
            // Update UI
            if (userInfo != null) {
                mEtName.setText(mViewModel.getCurrUserData().getValue().getName());
                weightPicker.setText("" + mViewModel.getCurrUserData().getValue().getWeight());
                datePickerEText.setText(mViewModel.getCurrUserData().getValue().getBirthdate());
                mEtCity.setText(mViewModel.getCurrUserData().getValue().getLocation());

                if (mViewModel.getCurrUserData().getValue().getGender() == 1) {
                    radioGroupGender.check(R.id.rb_male);
                } else {
                    radioGroupGender.check(R.id.rb_female);
                }

                if (mViewModel.getCurrUserData().getValue().getActivity()){
                    radioGroupActivity.check(R.id.rb_active);
                } else {
                    radioGroupActivity.check(R.id.rb_sedentary);
                }

                heightSP.setSelection(mViewModel.getCurrUserData().getValue().getHeight()-36);
                mThumbnailImage = mViewModel.getCurrUserData().getValue().getThumbnail();
                mIvThumbnail.setImageBitmap(mThumbnailImage);
            }
        }
    };

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