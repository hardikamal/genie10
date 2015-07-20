package com.getgenieapp.android.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getgenieapp.android.Activities.BaseActivity;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.R;
import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast;

/**
 * Created by Raviteja on 7/20/2015.
 */
public class PaymentFragment extends GenieFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        WebView webview = new WebView(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);

        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                getActivity().setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                SuperCardToast superCardToast = new SuperCardToast((BaseActivity) getActivity());
                superCardToast.setText(getString(R.string.ohno) + description);
                superCardToast.setDuration(SuperToast.Duration.LONG);
                superCardToast.setBackground(SuperToast.Background.RED);
                superCardToast.setTextColor(Color.WHITE);
                superCardToast.setSwipeToDismiss(true);
                superCardToast.show();
            }
        });

        webview.loadUrl("http://imojo.in/mrn77");

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }
}
