package com.getgenieapp.android.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

// Payment activity is used to access the payment web view from application
// Use TinyBus to send signal of payment received, so the activity can close.
// Web view
//

public class PaymentActivity extends GenieBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Socket connection status : " + genieApplication.getSocket().connected());
        WebView webview = new WebView(this);

        setContentView(webview);

        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Crouton.makeText(PaymentActivity.this, getString(R.string.ohno) + description, Style.ALERT).show();
                onBackPressed();
            }
        });

        webview.loadUrl("http://imojo.in/mrn77");
    }
}
