package com.getgenieapp.android.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;
import com.getgenieapp.android.GenieFragment;

public class VerifyFragment extends GenieFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify, container, false);
    }

    public interface onVerify
    {
        public void onSuccess(Verify verify);
        public void onError(Verify verify);
    }
}