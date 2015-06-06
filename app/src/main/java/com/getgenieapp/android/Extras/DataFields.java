package com.getgenieapp.android.Extras;

import android.os.Environment;

import java.io.File;

public class DataFields {
    public static final int SplashScreenGeneralTimeOut = 3000;
    public static final String TAG = "Get Genie App";

    public static final File root = Environment.getExternalStorageDirectory();
    public static final File mainFolder = new File(root.getAbsolutePath() + "/GetGenie");
    public static final File logFolder = new File(mainFolder.getAbsolutePath() + "/Log");
    public static final File logFile = new File(logFolder.getAbsolutePath() + "/"+GetDate.getLogDate()+"_Log.log");

}
