package com.agiliq.timezoneconverter.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "com.agiliq.timezoneconverter.PREF_NAME";
    private static final String KEY_VALUE = "com.agiliq.timezoneconverter.KEY_VALUE";

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener){
        mPref.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener){
        mPref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void setString(String key, String value){
        mPref.edit().putString(key, value).commit();
    }

    public String getString(String key){
        return mPref.getString(key, null);
    }

    public void setBoolean(String key, boolean value){
        mPref.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defValue){
        return mPref.getBoolean(key, defValue);
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}
