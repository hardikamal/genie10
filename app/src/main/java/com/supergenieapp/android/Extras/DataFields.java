package com.supergenieapp.android.Extras;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class DataFields {
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
    public static final String configPath = mainFolder.getAbsolutePath() + "/config.txt";
    public static final File logFolder = new File(mainFolder.getAbsolutePath() + "/Log");
    public static final File TempFolder = new File(mainFolder.getAbsolutePath() + "/temp");
    public static final File logFile = new File(logFolder.getAbsolutePath() + "/" + Utils.getLogDate() + "_Log.log");

    public static final String TOKEN = "token";
    public static final String GCM_TOKEN = "gcm_token";

    public static final String UPDATEGCMURL = "/updategcm";
    public static final String REGISTERURL = "/users";

    private static final String StagingServer = "http://staging0.supergenieapp.com";
    private static final String StagingSocket = "http://staging0chat.supergenieapp.com";

    private static final String TestServer = "http://staging0.getgenieapp.com";
    private static final String TestSocket = "http://staging0chat.getgenieapp.com";

    private static final String LocalServer = "http://192.168.1.7:3000";
    private static final String LocalSocket = "http://192.168.1.7:3004";

    private static final String LocalServer1 = "http://114.143.44.164";
    private static final String LocalSocket1 = "http://114.143.44.164:3004";

    private static final String API = "/api";
    private static final String Version = "/v1";

    public static final String VERIFYURL = "/verifyuser";
    public static final String RESENDURL = "/startverification";
    public static final String CATEGORIES = "/categories";
    public static final String USERPROFILE = "/userprofile";
    public static final String ORDERS = "/orders";
    public static final String UPDATEUSER = "/updateuser";

    public static final String DBName = "supergenieapp";
    public static final int DBVersion = 1;

    public static final int TEXT = 1;
    public static final int LOCATION = 2;
    public static final int IMAGE = 3;
    public static final int PAYNOW = 5;
    public static final int PAYASCOD = 6;

    public static final int ALERTMSG = 2;
    public static final int NotificationId = 1;

    public static final int INCOMING = 1;
    public static final int OUTGOING = 0;

    public static final int LOADMORE = 8;
    public static final int DATESHOW = 9;
    public static final int RATEWHENCOUNT = 5;
    public static final int RATEREMINDMERESET = 3;
    public static final int REQ_CODE_SPEECH_INPUT = 100;

    public static int QUEUE = 0;
    public static int SENT = 1;
    public static int DELIVERED = 2;
    public static int SEEN = 3;
    public static int position = -1;
    public static int ScrollDown = 1;
    public static int ScrollPosition = 2;
    public static int NoScroll = 0;

    public static String getServerUrl() {
        String server = StagingServer;
        String serverApi = API;
        String serverVersion = Version;
        if (new File(configPath).exists()) {
            try {
                HashMap<String, String> map = readFile();
                if (map.containsKey("server"))
                    server = map.get("server");
                if (map.containsKey("serverport"))
                    server += map.get("serverport");
                if (map.containsKey("api"))
                    serverApi = map.get("api");
                if (map.containsKey("version"))
                    serverVersion = map.get("version");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return server + serverApi + serverVersion;
    }

    public static String getChatUrl() {
        String socket = StagingSocket;
        if (new File(configPath).exists()) {
            try {
                HashMap<String, String> map = readFile();
                if (map.containsKey("socket"))
                    socket = map.get("socket");
                if (map.containsKey("socketport"))
                    socket += map.get("socketport");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

    private static HashMap<String, String> readFile() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        FileInputStream fis = new FileInputStream(configPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] brakeLines = line.split("-");
            map.put(brakeLines[0], brakeLines[1]);
        }
        br.close();
        return map;
    }
}