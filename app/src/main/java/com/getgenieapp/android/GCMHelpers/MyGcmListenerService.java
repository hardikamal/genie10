package com.getgenieapp.android.GCMHelpers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Raviteja on 6/6/2015.
 */
public class MyGcmListenerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
