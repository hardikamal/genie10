package com.supergenieapp.android;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.localytics.android.Localytics;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.FontChangeCrawler;
import com.supergenieapp.android.Extras.LoggingBuilder;
import com.supergenieapp.android.Extras.LruBitmapCache;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Pacemaker.Pacemaker;
import com.supergenieapp.android.SecurePreferences.SecurePreferences;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.net.URISyntaxException;

import de.halfbit.tinybus.TinyBus;

@ReportsCrashes(
        formUri = "https://getgenieapp.cloudant.com/acra-getgenieapp/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "handermentlyoughtsepried",
        formUriBasicAuthPassword = "fC5176aYhXy47CaGPRWm77Jb",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.error_toast
)


public class GenieApplication extends Application {
    private LoggingBuilder loggingBuilder;
    private static GenieApplication genieApplication;
    private FontChangeCrawler fontChangerRegular;
    private SecurePreferences mSecurePrefs;
    private TinyBus mBus;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Socket mSocket;
    private DBDataSource dbDataSource;
    {
        try {
            mSocket = IO.socket(DataFields.getChatUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        genieApplication = this;
        setFolders();
        Pacemaker.scheduleExponential(this, 5, 90);
        fontChangerRegular = new FontChangeCrawler(getAssets(), "Roboto-Regular.ttf");
        loggingBuilder = new LoggingBuilder(getApplicationContext())
                .setCanDisplayOnLogCat(true)
                .setWriteToLog(false)
                .setFile(DataFields.logFile.getAbsolutePath())
                .setClassName("Genie Application");
        mSecurePrefs = new SecurePreferences(this);
        mBus = TinyBus.from(this);
        ACRA.init(this);
        dbDataSource = new DBDataSource(this);
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
        registerComponentCallbacks(new MyComponentsLifecycleCallbacks());
        registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this));
        Localytics.setPushDisabled(false);
        Utils utils = new Utils(this);
        Localytics.integrate(this);
        Localytics.setCustomerId(utils.getDeviceSerialNumber());
        Localytics.registerPush(getString(R.string.projectId));
    }

    private void setFolders() {
        if (!DataFields.TempFolder.exists()) {
            DataFields.TempFolder.mkdirs();
        }
    }

    public SecurePreferences getSecurePrefs() {
        return mSecurePrefs;
    }

    public TinyBus getBus() {
        return mBus;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public LoggingBuilder getLoggingBuilder() {
        return loggingBuilder;

    }

    public FontChangeCrawler getFontChangeCrawlerRegular() {
        return fontChangerRegular;
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

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public DBDataSource getDBDataSource() {
        return dbDataSource;
    }

    public void connectToSocket() {
        if (!mSocket.connected())
            mSocket.connect();
    }

    public void disconnectToSocket() {
        mSocket.disconnect();
    }

    private static final class MyComponentsLifecycleCallbacks implements ComponentCallbacks2 {
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
        }

        @Override
        public void onLowMemory() {
        }

        @Override
        public void onTrimMemory(int level) {
        }
    }

    private static final class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        private int numRunningActivities = 0;

        public void onActivityCreated(Activity activity, Bundle bundle) {
            numRunningActivities++;
        }

        public void onActivityDestroyed(Activity activity) {
            numRunningActivities--;
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }
    }
}
