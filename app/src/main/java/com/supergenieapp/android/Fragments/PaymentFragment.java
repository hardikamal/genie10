package com.supergenieapp.android.Fragments;

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

import com.android.volley.toolbox.NetworkImageView;
import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.supergenieapp.android.GenieFragment;
import com.supergenieapp.android.R;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Raviteja on 7/20/2015.
 */
public class PaymentFragment extends GenieFragment {
    ViewGroup viewGroup;
    @Optional
    @InjectView(R.id.webPage)
    WebView webview;
    @Optional
    @InjectView(R.id.image)
    NetworkImageView imageView;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;
    HashMap<String, Object> mixpanelDataAdd = new HashMap<>();
    String url = "http://imojo.in/mrn77";

    @Override
    public void onStart() {
        super.onStart();
        hideKeyboard(getActivity());
        mixPanelTimerStart(PaymentFragment.class.getName());
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        mixPanelTimerStop(PaymentFragment.class.getName());
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        this.viewGroup = container;
        View rootView;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            url = bundle.getString("url", url);
            if (bundle.getBoolean("image", false)) {
                rootView = inflater.inflate(R.layout.image_view, container, false);
                ButterKnife.inject(this, rootView);
                setImageForm();
            } else {
                rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
                ButterKnife.inject(this, rootView);
                setWebForm();
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
            ButterKnife.inject(this, rootView);
            setWebForm();
        }

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    private void setImageForm() {
        parentLoadingView.setLoading(true);
        parentLoadingView.setLoading(false);
        parentLoadingView.setText(getString(R.string.loading));
        imageView.setImageUrl(url, imageLoader);
    }

    private void setWebForm() {
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

        webview.loadUrl(url);
    }
}
