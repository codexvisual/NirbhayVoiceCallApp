package com.orbit.call.acitivites;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.orbit.call.R;
import com.orbit.call.utils.Log;


public class AccessNOPrefActivity extends PreferenceActivity
{

    private SharedPreferences mpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.access_no_preference);
        mpreferences = getSharedPreferences(
                String.format("%s_preferences", this.getPackageName()),
                Context.MODE_PRIVATE);
        String[] access_no_list = mpreferences.getString(SplashActivity.IVR, "").split(",");
        for (int i = 0; i < access_no_list.length; i++) {
            Log.e("split array ", access_no_list[i]);
        }
        ListPreference access_no_pref = (ListPreference) findPreference("access_no");
        access_no_pref.setEntries(access_no_list);
        access_no_pref.setEntryValues(access_no_list);
        if (access_no_pref.getValue() == null) {
            access_no_pref.setValueIndex(0);
        }
    }
}