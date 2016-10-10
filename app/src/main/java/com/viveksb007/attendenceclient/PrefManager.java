package com.viveksb007.attendenceclient;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String pref_name = "AttendanceClient";
    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    private static final String USERNAME = "username";
    private static final String DEVICE_ID = "defaultID";


    public PrefManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setFirstLaunch(boolean is_first_time) {
        editor.putBoolean(IS_FIRST_LAUNCH, is_first_time);
        editor.commit();
    }

    public boolean isFirstTime() {
        return preferences.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void setUserNameAndDeviceID(String userName, String deviceID) {
        editor.putString(USERNAME, userName);
        editor.putString(DEVICE_ID, deviceID);
        editor.commit();
    }

    public String getDeviceId(){
        return preferences.getString(DEVICE_ID,"default_ID");
    }

    public String getUsername(){
        return preferences.getString(USERNAME,"default_user");
    }

    public String getRollNo(){
        return preferences.getString("rollNo","default_roll_no");
    }

    public void initialSelection(String year, String branch, String section, String rollNo) {
        editor.putString("year", year);
        editor.putString("branch", branch);
        editor.putString("section", section);
        editor.putString("rollNo",rollNo);
        editor.commit();
    }
}
