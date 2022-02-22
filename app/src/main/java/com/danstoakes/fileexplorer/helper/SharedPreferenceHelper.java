package com.danstoakes.fileexplorer.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceHelper
{
    public static final String sortKey = "sortType";
    public static final String orderKey = "orderType";

    public static final int PERMISSION_ACCESS_STORAGE = 1;
    public static final int DEFAULT_VALUE = -1;

    public static void setInteger (Context context, String key, int value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInteger (Context context, String key)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, DEFAULT_VALUE);
    }
}