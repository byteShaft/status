package com.byteshaft.status;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AppGlobals extends Application {

    private static final String IS_DATA_SAVED = "isDataSaved";
    private static Context sContext;
    public static final String KEY_SECONDS = "seconds";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ID = "id";

    public static String GET_URL;
    public static String POST_URL;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        if (isDataSaved()) {
            GET_URL = String.format("https://sourceway.de/admin/online.php?id=%s&amp;pw=%s",
                    getStringFromSharedPreferences(KEY_ID),
                    getStringFromSharedPreferences(KEY_PASSWORD));
            POST_URL = String.format("https://sourceway.de/admin/online.php?id=%s&amp;pw=%s&amp;status=",
                    getStringFromSharedPreferences(KEY_ID),
                    getStringFromSharedPreferences(KEY_PASSWORD));
        }
    }


    public static void saveData(boolean state) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(IS_DATA_SAVED, state).apply();
    }

    public static boolean isDataSaved() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(IS_DATA_SAVED, false);
    }


    public static SharedPreferences getPreferenceManager() {
        return getContext().getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public static Context getContext() {
        return sContext;
    }

    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }
}

