package com.getgenieapp.android.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.R;
import com.getgenieapp.android.GenieFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RegisterFragment extends GenieFragment {

    UIHelpers uiHelpers;
    @InjectView(R.id.topText)
    TextView topText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.inject(this, rootView);

        uiHelpers = new UIHelpers();
//        uiHelpers.correctWidth(topText, getActivity());

        topText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.topText), uiHelpers.getXYPixels(getActivity()).x/4));
        fontChangeCrawler.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    public interface onRegister
    {
        public void onSuccess(Register register);
        public void onError(Register register);
    }
}
