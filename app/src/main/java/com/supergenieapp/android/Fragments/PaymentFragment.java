package com.supergenieapp.android.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.localytics.android.Localytics;
import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.GenieFragment;
import com.supergenieapp.android.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    SubsamplingScaleImageView imageView;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;
    HashMap<String, Object> mixpanelDataAdd = new HashMap<>();
    String url = "http://supergenieapp.com";
    long created_at = Utils.getCurrentTimeMillis();
    boolean goBack = true;

    @Override
    public void onStart() {
        super.onStart();
        Localytics.openSession();
        Localytics.tagScreen("Payment Fragment");
        hideKeyboard(getActivity());
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        logging.LogV("Showed", "on Stop");
        super.onStop();
        ((BaseActivity) getActivity()).emitPayOnline(created_at, "back");
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
            created_at = bundle.getLong("created_at", created_at);
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
        final String path = DataFields.TempFolder + "/" + utils.hashString(url);
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImage(ImageSource.uri(path));
        } else {
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response != null && response.getBitmap() != null) {
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(path);
                            response.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        imageView.setImage(ImageSource.uri(path));
                    }
                }
            });
        }
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
                if (view != null) {
                    if (progress == 100) {

                        parentLoadingView.setLoading(false);
                        if (view.getUrl().contains("http://getgenieapp.com") || view.getUrl().contains("http://supergenieapp.com")) {
                            if (goBack) {
                                goBack = false;
                                getActivity().onBackPressed();
                            }
                        }
                    }
                }
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
