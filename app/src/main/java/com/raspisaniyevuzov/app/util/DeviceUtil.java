package com.raspisaniyevuzov.app.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Patterns;

import com.raspisaniyevuzov.app.RVuzovApp;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class DeviceUtil {

    /**
     * Gets version from package
     *
     * @param context
     * @return
     */
    private static String getPackageVersion(Context context) {
        String pkg = context.getPackageName();
        String sPackageVersion;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkg, 0);
            sPackageVersion = packageInfo.versionName + "." + packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            sPackageVersion = "0.0";
            e.printStackTrace();
        }
        return sPackageVersion;
    }

    /**
     * @return version, ex. Android_5.1.1
     */
    public static String getOS() {
        return "Android_" + android.os.Build.VERSION.RELEASE;
    }

    /**
     * @return version
     */
    public static String getVersion() {
        return getPackageVersion(RVuzovApp.getContext()) + ".a";
    }

    /**
     * @return device name, ex. Nexus 5
     */
    public static String getDeviceName() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    /**
     * @return MD5 Android_Id or null
     */
    public static String getAndroidIdMd5() {
        String md5AndroidId = null;
        String androidId = Settings.Secure.getString(RVuzovApp.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (androidId != null) {
            try {
                md5AndroidId = SecureUtil.md5(androidId, true);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return md5AndroidId;
    }

    /**
     * Получение статуса подключения к сети
     *
     * @return
     */
    public static boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) RVuzovApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = false;
        if (activeNetwork != null)
            isConnected = activeNetwork.isConnected();
        return isConnected;
    }

    public static String getPossibleDeviceEmail(Activity context) {
        String possibleEmail = "";
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches())
                possibleEmail = account.name;
        }
        return possibleEmail;
    }

}
