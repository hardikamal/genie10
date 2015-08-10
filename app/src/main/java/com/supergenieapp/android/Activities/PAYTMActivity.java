package com.supergenieapp.android.Activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.PaytmWebView;
import com.supergenieapp.android.R;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PAYTMActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOG", "onCreate of MainActivity");
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
//        WebView webView = new WebView(this);
//
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//
//        WebSettings ws = webView.getSettings();
//        ws.setSaveFormData(true);

        setContentView(R.layout.activity_paytm);

//        webView.getSettings().setJavaScriptEnabled(true);
//
//        webView.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                // Activities and WebViews measure progress with different scales.
//                // The progress meter will automatically disappear when we reach 100%
//
//            }
//        });
//        webView.setWebViewClient(new WebViewClient() {
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//
//            }
//        });
//
//        Map<String, String> request = new HashMap<>();
//        request.put("Cookie", getCookieFromAppCookieManager("https://www.facebook.com"));
//
//        webView.loadUrl("https://www.facebook.com", request);
//    }
//
//    private String getCookieFromAppCookieManager(String url) {
//        CookieManager cookieManager = CookieManager.getInstance();
//        if (cookieManager == null)
//            return null;
//        String rawCookieHeader = null;
//        URL parsedURL = null;
//        try {
//            parsedURL = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        // Extract Set-Cookie header value from Android app CookieManager for this URL
//        assert parsedURL != null;
//        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
//        if (rawCookieHeader == null)
//            return null;
//        return rawCookieHeader;
//    }
    }
}
