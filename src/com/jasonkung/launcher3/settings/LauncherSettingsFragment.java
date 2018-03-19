package com.jasonkung.launcher3.settings;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jasonkung.launcher3.BuildConfig;
import com.jasonkung.launcher3.LauncherSettings;
import com.jasonkung.launcher3.R;
import com.jasonkung.launcher3.Utilities;

public class LauncherSettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener{


    private SystemDisplayRotationLockObserver mRotationLockObserver;

    private Preference mShowPredictionsPrefs;

    static class SystemDisplayRotationLockObserver extends ContentObserver {
        private final ContentResolver mResolver;
        private final Preference mRotationPref;

        public SystemDisplayRotationLockObserver(Preference preference, ContentResolver contentResolver) {
            super(new Handler());
            this.mRotationPref = preference;
            this.mResolver = contentResolver;
        }

        public void onChange(boolean z) {
            boolean enable = true;
            if (android.provider.Settings.System.getInt(this.mResolver, "accelerometer_rotation", 1) != 1) {
                enable = false;
            }
            this.mRotationPref.setEnabled(enable);
            this.mRotationPref.setSummary(enable ? R.string.allow_rotation_desc : R.string.allow_rotation_blocked_desc);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Bundle extras = new Bundle();
        extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
        getActivity().getContentResolver().call(
                LauncherSettings.Settings.CONTENT_URI,
                LauncherSettings.Settings.METHOD_SET_BOOLEAN,
                preference.getKey(), extras);
        return true;
    }

    private Preference.OnPreferenceClickListener mOnShowPredictionsChangeListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            mShowPredictionsPrefs = preference;
            if(((SwitchPreference) preference).isChecked() == false) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.title_disable_suggestions_prompt)
                        .content(R.string.msg_disable_suggestions_prompt)
                        .positiveText(R.string.label_turn_off_suggestions)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback(){
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //TODO : close DB
                                LauncherSettingsFragment.this.onPreferenceChange(mShowPredictionsPrefs, false);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback(){
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((SwitchPreference) mShowPredictionsPrefs).setChecked(true);
                            }
                        })
                        .show();
            } else {
                return LauncherSettingsFragment.this.onPreferenceChange(mShowPredictionsPrefs, true);
            }
            return true;
        }
    };

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (!"open_source_licenses".equals(preference.getKey())) {
            return false;
        }
        WebView webView = new WebView(getActivity());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("file:///android_res/raw/license.html");
        new AlertDialog.Builder(getActivity()).setTitle(R.string.pref_open_source_licenses_title).setView(webView).show();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.launcher_preferences);

        SwitchPreference pref = (SwitchPreference) findPreference(
                Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
        pref.setPersistent(false);

        Bundle extras = new Bundle();
        extras.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
        Bundle value = getActivity().getContentResolver().call(
                LauncherSettings.Settings.CONTENT_URI,
                LauncherSettings.Settings.METHOD_GET_BOOLEAN,
                Utilities.ALLOW_ROTATION_PREFERENCE_KEY, extras);
        pref.setChecked(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE));

        pref.setOnPreferenceChangeListener(this);


        ContentResolver contentResolver = getActivity().getContentResolver();
        mRotationLockObserver = new SystemDisplayRotationLockObserver(pref, contentResolver);
        contentResolver.registerContentObserver(android.provider.Settings.System.getUriFor("accelerometer_rotation"), false, mRotationLockObserver);
        this.mRotationLockObserver.onChange(true);
        pref.setDefaultValue(Boolean.valueOf(Utilities.getAllowRotationDefaultValue(getActivity())));

        SwitchPreference pref2 = (SwitchPreference) findPreference(
                Utilities.HIDE_QSB_PREFERENCE_KEY);
        pref2.setOnPreferenceChangeListener(this);

        /*String str = "";
        try {
            str = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (Throwable e) {
            Log.e("SettingsActivity", "Unable to load my own package info", e);
        }*/
        findPreference("about_app_version").setSummary(BuildConfig.VERSION_NAME);
        findPreference("open_source_licenses").setOnPreferenceClickListener(this);

        findPreference(Utilities.SHOW_PREDICTIONS).setOnPreferenceClickListener(mOnShowPredictionsChangeListener);

        SwitchPreference prefHideIcon = (SwitchPreference) findPreference(Utilities.HIDE_APPS_ICON_LABELS);
        prefHideIcon.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        if (this.mRotationLockObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(this.mRotationLockObserver);
            this.mRotationLockObserver = null;
        }
        super.onDestroy();
    }

}
