package com.example.leon.taitou;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import com.orhanobut.logger.Logger;
import android.widget.Toast;
import android.content.Context;

/**
 * Created by leon on 2017/10/2.
 */

public class SettingFragment extends PreferenceFragment {


    SharedPreferences.OnSharedPreferenceChangeListener mChangeListener;
    Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference tip_lp=(ListPreference) findPreference("alert_tip_list");
        if(tip_lp.getEntry()!=null) {
            tip_lp.setSummary(tip_lp.getEntry());
        }


        mChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
               if("alert_tip_list".equals(key))
                {
                    //findPreference("alert_tip_list").setSummary(sharedPreferences.getString(key,"tip"));
                    ListPreference tip_lp=(ListPreference) findPreference("alert_tip_list");
                    tip_lp.setSummary(tip_lp.getEntry());
                }
            }
        };





    }
    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(mChangeListener);
    }


    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mChangeListener);
    }

}
