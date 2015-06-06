package com.getgenieapp.android.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.R;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RegisterFragment extends Fragment {

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
        return rootView;
    }
}
