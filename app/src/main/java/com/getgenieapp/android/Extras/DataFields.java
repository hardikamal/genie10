package com.getgenieapp.android.Extras;

import android.os.Environment;

import java.io.File;

public class DataFields {
    public static final int ServerSelection = 3;
    // 1 for Staging
    // 2 for Test
    // 3 for Local

    public static final int SplashScreenGeneralTimeOut = 3000;
    public static final int VerifyTimeOut = 2000;
    public static final int small400TimeOut = 400;
    public static final int small600TimeOut = 600;
    public static final int small800TimeOut = 800;
    public static final int small1000TimeOut = 1000;
    public static final String TAG = "Get Genie App";

    public static final File root = Environment.getExternalStorageDirectory();
    public static final File mainFolder = new File(root.getAbsolutePath() + "/GetGenie");
    public static final String profilePicturePath = mainFolder.getAbsolutePath() + "/me.jpg";
    public static final File logFolder = new File(mainFolder.getAbsolutePath() + "/Log");
    public static final File logFile = new File(logFolder.getAbsolutePath() + "/" + GetDate.getLogDate() + "_Log.log");

    public static final String TOKEN = "token";
    public static final String GCM_TOKEN = "gcm_token";

    public static final String UPDATEGCMURL = "/updategcm";
    public static final String REGISTERURL = "/users";

    private static final String CHAT_SERVER_URL = "http://chat.socket.io";

    private static final String StagingServer = "http://staging0.getgenieapp.com";
    private static final String StagingSocket = "http://staging0chat.getgenieapp.com";

    private static final String TestServer = "http://staging0.getgenieapp.com";
    private static final String TestSocket = "http://staging0chat.getgenieapp.com";

    private static final String LocalServer = "http://192.168.1.27:3000";
    private static final String LocalSocket = "http://192.168.1.27:3004";

    private static final String API = "/api";
    private static final String Version = "/v1";
    public static final String VERIFYURL = "/verifyuser";
    public static final String RESENDURL = "/startverification";
    public static final String CATEGORIES = "/categories";
    public static final String UPDATEUSER = "/updateuser";
    public static final String DBName = "getgenieapp";
    public static final int DBVersion = 1;

    public static final int CAMERA_CAPTURE = 1;
    public static final int PICK_IMAGE = 1;
    public static final int PIC_CROP = 2;

    public static final int TEXT = 1;
    public static final int LOCATION = 2;
    public static final int IMAGE = 3;
    public static final int PAYNOW = 5;

    public static final int ALERTMSG = 2;
    public static final int NotificationId = 1;

    public static final int INCOMING = 1;
    public static final int OUTGOING = 0;

    public static final int LOADMORE = 8;
    public static final int DATESHOW = 9;

    public static int QUEUE = 0;
    public static int SENT = 1;
    public static int DELIVERED = 2;
    public static int SEEN = 3;

    public static String getServerUrl() {
        return StagingServer + API + Version;
    }

    public static String getChatUrl() {
        return StagingSocket;
    }
}