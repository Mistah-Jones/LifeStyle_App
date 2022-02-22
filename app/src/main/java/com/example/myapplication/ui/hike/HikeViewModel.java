package com.example.myapplication.ui.hike;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HikeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HikeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hike fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}