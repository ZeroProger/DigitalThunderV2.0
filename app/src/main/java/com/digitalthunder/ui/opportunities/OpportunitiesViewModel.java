package com.digitalthunder.ui.opportunities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OpportunitiesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OpportunitiesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is opportunities fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}