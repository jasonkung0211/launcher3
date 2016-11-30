/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jasonkung.launcher3;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.System;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {

    class SystemDisplayRotationLockObserver extends ContentObserver {
        private final ContentResolver mResolver;
        private final Preference mRotationPref;

        public SystemDisplayRotationLockObserver(Preference preference, ContentResolver contentResolver) {
            super(new Handler());
            this.mRotationPref = preference;
            this.mResolver = contentResolver;
        }

        public void onChange(boolean z) {
            boolean enable = true;
            if (System.getInt(this.mResolver, "accelerometer_rotation", 1) != 1) {
                enable = false;
            }
            this.mRotationPref.setEnabled(enable);
            this.mRotationPref.setSummary(enable ? R.string.allow_rotation_desc : R.string.allow_rotation_blocked_desc);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public class LauncherSettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {
            private SystemDisplayRotationLockObserver mRotationLockObserver;
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
            contentResolver.registerContentObserver(System.getUriFor("accelerometer_rotation"), false, mRotationLockObserver);
            this.mRotationLockObserver.onChange(true);
            pref.setDefaultValue(Boolean.valueOf(Utilities.getAllowRotationDefaultValue(getActivity())));


        }

        public void onDestroy() {
            if (this.mRotationLockObserver != null) {
                getActivity().getContentResolver().unregisterContentObserver(this.mRotationLockObserver);
                this.mRotationLockObserver = null;
            }
            super.onDestroy();
        }

        //TODO: no use ..
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
    }
}
