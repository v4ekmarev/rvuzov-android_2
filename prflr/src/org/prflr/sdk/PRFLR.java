package org.prflr.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class PRFLR {

    private static final String TAG = "PRFLR";
    static String UID;

    /**
     * Initializes PRFLR.
     */
    public static void init(Context c) {
        Log.d(TAG, "init called.");

        if (PRFLRSender.initialized()) {
            Log.w(TAG, "Already initialized!");
            return;
        }

        try {

            Log.d(TAG, "Initializing PRFLR.");

            PackageManager pm = c.getPackageManager();
            assert pm != null;

            String version = pm.getPackageInfo(c.getPackageName(), 0).versionName;

            ApplicationInfo inf = pm.getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = inf.metaData.getString("org.prflr.apikey");

            UID = getDeviceUID(c);

            String source = version + " " + getDeviceName();

            PRFLRSender.init(source, apiKey);

            Log.d(TAG, "Successfully initialized, UID is " + UID);

        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
        }

    }

    /**
     * Sets maximum amount of idle timers. Timer storage will be cleared
     */
    public static void setOveflowCounter(int value) {
        PRFLRSender.setOverflowCount(value);
    }

    public static void begin(String timerName) {
        try {
            PRFLRSender sender = PRFLRSender.getInstance();
            if (sender != null)
                sender.begin(timerName);
        } catch (Exception e) {
            Log.e(TAG, "Error while executing begin()", e);
        }
    }

    public static void end(String timerName) {
        try {
            PRFLRSender sender = PRFLRSender.getInstance();
            if (sender != null)
                sender.end(timerName);
        } catch (Exception e) {
            Log.e(TAG, "Error while executing end()", e);
        }
    }

    public static void end(String timerName, String info) {
        try {
            PRFLRSender sender = PRFLRSender.getInstance();
            if (sender != null)
                sender.end(timerName, info);
        } catch (Exception e) {
            Log.e(TAG, "Error while executing end()", e);
        }
    }

    private static String byteToString(byte b) {
        String ready = Integer.toString(255 & b, 16);
        if (ready.length() == 1)
            ready = "0" + ready;
        return ready;
    }

    private static String md5(String str) {
        try {
            MessageDigest digest;

            digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes("UTF-8"));

            StringBuilder hash = new StringBuilder();
            for (byte b : digest.digest()) {
                hash.append(byteToString(b));
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDeviceName() {
        String manufacturer = Build.VERSION.SDK_INT >= 4 ? Build.MANUFACTURER : "";
        String model = Build.MODEL;
        String version = Build.VERSION.RELEASE;

        if (model.startsWith(manufacturer)) {
            return "[" + model + "] " + version;
        } else {
            return "[" + cut(manufacturer, 5) + "] " + version;
        }
    }

    private static String getDeviceUID(Context context)
    throws NoSuchAlgorithmException {
        return
                md5(""
                                + (Build.VERSION.SDK_INT >= 9 ? Build.SERIAL : "")
                                + ((WifiManager) context.getSystemService(Activity.WIFI_SERVICE)).getConnectionInfo().getMacAddress()
                );
    }

    private static String cut(String s, Integer maxLength) {
        if (s == null)
            return "";
        if (s.length() < maxLength)
            return s;
        else
            return s.substring(0, maxLength);
    }
}
