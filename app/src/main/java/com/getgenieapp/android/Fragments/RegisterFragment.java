package com.getgenieapp.android.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getgenieapp.android.CustomViews.LoadingView;
import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.R;
import com.getgenieapp.android.GenieFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class RegisterFragment extends GenieFragment {

    UIHelpers uiHelpers;
    @InjectView(R.id.topText)
    TextView topText;
    @InjectView(R.id.subText)
    TextView subText;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.inject(this, rootView);

        uiHelpers = new UIHelpers();
        topText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.topText), uiHelpers.getXYPixels(getActivity()).x / 4));
        subText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.topText), uiHelpers.getXYPixels(getActivity()).x / 4));

        fontChangeCrawler.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    @OnClick(R.id.getStarted)
    public void onGetStartedButtonClick()
    {

        parentLoadingView.setLoading(true);

    }

    public interface onRegister
    {
        public void onSuccess(Register register);
        public void onError(Register register);
    }
}
