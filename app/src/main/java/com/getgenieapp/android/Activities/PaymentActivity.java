package com.getgenieapp.android.Activities;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

public class PaymentActivity extends GenieBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                SnackBar snackBar = new SnackBar(activity, "Oh no! " + description);
                snackBar.show();
                onBackPressed();
            }
        });

        webview.loadUrl("http://developer.android.com/");
    }
}
