package com.raspisaniyevuzov.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class PrefUtil {

    private static final String PREF_IS_LOGGED = "is_logged";
    private static final String PREF_IS_NO_ADS = "is_no_ads";
    private static final String PREF_BANNERS = "banners";
    private static final String PREF_SCHEDULE_ACTIVATED= "schedule_activated";
    private static final String PREF_OLD_DATA_HAS_ALREADY_IMPORTED = "old_data_has_already_imported";

    public static boolean isLogged(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_IS_LOGGED, false);
    }

    public static void setLogged(Context context, final boolean isLogged) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_IS_LOGGED, isLogged).apply();
    }

    public static boolean isNoAds(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_IS_NO_ADS, false);
    }

    public static void setBanners(Context context, String banners) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_BANNERS, banners).apply();
    }

    public static String getBanners(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_BANNERS, null);
    }

    public static void setNoAds(Context context, final boolean isNoAds) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_IS_NO_ADS, isNoAds).apply();
    }

    public static boolean isScheduleActivated(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SCHEDULE_ACTIVATED, false);
    }

    public static void setScheduleActivated(Context context, final boolean isActivated) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SCHEDULE_ACTIVATED, isActivated).apply();
    }

    public static String getStringProperty(Context context, String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(name, null);
    }

    public static int getIntProperty(Context context, String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(name, 0);
    }

    public static void addStringProperty(Context context, String name, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(name, value).apply();
    }

    public static void addIntProperty(Context context, String name, Integer value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(name, value).apply();
    }

    public static boolean dataHasAlreadyImported(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_OLD_DATA_HAS_ALREADY_IMPORTED, false);
    }

    public static void setDataImported(Context context, boolean imported) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_OLD_DATA_HAS_ALREADY_IMPORTED, imported).apply();
    }

}
