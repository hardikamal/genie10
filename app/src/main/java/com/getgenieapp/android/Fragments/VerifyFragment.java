package com.getgenieapp.android.Fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;
import com.getgenieapp.android.GenieFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.halfbit.tinybus.Subscribe;

public class VerifyFragment extends GenieFragment {
    @InjectView(R.id.subText)
    TextView subText;
    @InjectView(R.id.char1)
    EditText char1;
    @InjectView(R.id.char2)
    EditText char2;
    @InjectView(R.id.char3)
    EditText char3;
    @InjectView(R.id.char4)
    EditText char4;
    private UIHelpers uiHelpers;
    final int entryLength = 1;

    @Override
    public void onStart() {
        super.onStart();
        logging.LogV("Showed","on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Subscribe
    public void onSMSReceived(String code) {
        logging.LogV("SMS Received Code", code);
        if(char1!=null&&char2!=null&&char3!=null&&char4!=null)
        {
            char1.setText(code.substring(0,1));
            char2.setText(code.substring(1,2));
            char3.setText(code.substring(2,3));
            char4.setText(code.substring(3,4));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        View rootView = inflater.inflate(R.layout.fragment_verify, container, false);
        ButterKnife.inject(this, rootView);
        moveToNextMech();
        uiHelpers = new UIHelpers();
        subText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.verifycodetext), uiHelpers.getXYPixels(getActivity()).x / 4));
        return rootView;
    }

    private void moveToNextMech() {
        char1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 1","on Text Changed");
                if (char1.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 1","Text length 1");
                    char2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        char2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 2","on Text Changed");
                if (char2.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 2","Text length 1");
                    char3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        char3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 3","on Text Changed");
                if (char3.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 3","Text length 1");
                    char4.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        char4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 4","on Text Changed");
                if (char4.getText().toString().trim().length() == entryLength
                        && char1.getText().toString().trim().length() == entryLength
                        && char2.getText().toString().trim().length() == entryLength
                        && char3.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char","Texts length 1");
                    goNext();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

    private void goNext() {

    }

    @OnClick(R.id.tapToResend)
    public void topToResend() {

    }

    public interface onVerify {
        public void onSuccess(Verify verify);

        public void onError(Verify verify);
    }
}
