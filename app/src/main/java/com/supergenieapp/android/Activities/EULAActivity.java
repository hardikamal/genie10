package com.supergenieapp.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.supergenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.supergenieapp.android.CustomViews.TextView.AutoResizeTextView;
import com.supergenieapp.android.GenieBaseActivity;
import com.supergenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EULAActivity extends GenieBaseActivity {
    @InjectView(R.id.parentLoadingView)
    LoadingView loadingView;
    @InjectView(R.id.webPage)
    WebView webPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_web_view);
        ButterKnife.inject(this);

        loadingView.setLoading(true);
        loadingView.setText(getString(R.string.loadingtermsandconditions));
        webPage.getSettings().setJavaScriptEnabled(true);

        webPage.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (view != null) {
                    if (progress == 100) {
                        loadingView.setLoading(false);
                    }
                }
            }
        });
        webPage.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });

        webPage.loadUrl("http://www.supergenieapp.com/terms.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}