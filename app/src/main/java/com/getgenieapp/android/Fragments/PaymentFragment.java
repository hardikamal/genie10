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
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Raviteja on 7/20/2015.
 */
public class PaymentFragment extends GenieFragment {
    ViewGroup viewGroup;
    @InjectView(R.id.webPage)
    WebView webview;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        this.viewGroup = container;
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
        ButterKnife.inject(this, rootView);

        parentLoadingView.setLoading(true);
        parentLoadingView.setText(getString(R.string.paymentpageload));
        Crouton.makeText((Activity) getActivity(), getString(R.string.finishordertext), Style.INFO, R.id.body).show();
        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (progress == 100) {
                    parentLoadingView.setLoading(false);
                }
                getActivity().setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Crouton.makeText((BaseActivity) getActivity(), getString(R.string.ohno) + description, Style.ALERT, viewGroup).show();
            }
        });

        webview.loadUrl("http://imojo.in/mrn77");

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }
}