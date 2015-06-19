package com.getgenieapp.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.LoggingBuilder;
import com.getgenieapp.android.Extras.LruBitmapCache;
import com.getgenieapp.android.SecurePreferences.SecurePreferences;

import de.halfbit.tinybus.TinyBus;

public class GenieApplication extends Application {
    private LoggingBuilder loggingBuilder;
    private static GenieApplication genieApplication;
    private FontChangeCrawler fontChangerRegular;
    private FontChangeCrawler fontChangerMedium;
    private FontChangeCrawler fontChangerLight;
    private SecurePreferences mSecurePrefs;
    private TinyBus mBus;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        genieApplication = this;
        fontChangerRegular = new FontChangeCrawler(getAssets(), "Roboto-Regular.ttf");
        fontChangerMedium = new FontChangeCrawler(getAssets(), "Roboto-Medium.ttf");
        fontChangerLight = new FontChangeCrawler(getAssets(), "Roboto-Light.ttf");
        loggingBuilder = new LoggingBuilder(getApplicationContext())
                .setCanDisplayOnLogCat(true)
                .setWriteToLog(false)
                .setFile(DataFields.logFile.getAbsolutePath())
                .setClassName("Genie Application");
        mSecurePrefs = new SecurePreferences(this);
        mBus = TinyBus.from(this);
    }

    public SecurePreferences getSecurePrefs() {
        return mSecurePrefs;
    }

    public TinyBus getBus() {
        return mBus;
    }

    public LoggingBuilder getLoggingBuilder() {
        return loggingBuilder;

    }

    public FontChangeCrawler getFontChangeCrawlerRegular() {
        return fontChangerRegular;
    }
    public FontChangeCrawler getFontChangeCrawlerMedium() {
        return fontChangerMedium;
    }
    public FontChangeCrawler getFontChangeCrawlerLight() {
        return fontChangerLight;
    }

    public static synchronized GenieApplication getInstance() {
        return genieApplication;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? DataFields.TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(DataFields.TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
