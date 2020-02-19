/*
 *  Copyright (C) 2015-2018 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/
package com.gzr.funhouse.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.Utils;

import com.gzr.funhouse.preference.CustomSeekBarPreference;
import com.gzr.funhouse.preference.SystemSettingSwitchPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class PulseSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private ColorPickerPreference mPulseLightColor;
    private int mDefaultColor;
    private ListPreference mPulseTimeout;
    private CustomSeekBarPreference mPulseBrightness;
    private CustomSeekBarPreference mDozeBrightness;
    private SwitchPreference mPulseAmbientLight;
    private SwitchPreference mAmbientLightEnabled;
    private SwitchPreference mAmbientLightHide;
    private SwitchPreference mAmbientLightAccent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pulse);

        int defaultDoze = getResources().getInteger(
                com.android.internal.R.integer.config_screenBrightnessDoze);
        int defaultPulse = getResources().getInteger(
                com.android.internal.R.integer.config_screenBrightnessPulse);
        if (defaultPulse == -1) {
            defaultPulse = defaultDoze;
        }

        mPulseBrightness = (CustomSeekBarPreference) findPreference("ambient_pulse_brightness");
        int value = Settings.System.getInt(getContentResolver(),
                Settings.System.OMNI_PULSE_BRIGHTNESS, defaultPulse);
        mPulseBrightness.setValue(value);
        mPulseBrightness.setOnPreferenceChangeListener(this);

        mDozeBrightness = (CustomSeekBarPreference) findPreference("ambient_doze_brightness");
        value = Settings.System.getInt(getContentResolver(),
                Settings.System.OMNI_DOZE_BRIGHTNESS, defaultDoze);
        mDozeBrightness.setValue(value);
        mDozeBrightness.setOnPreferenceChangeListener(this);
		
        mDefaultColor = getResources().getInteger(
                com.android.internal.R.integer.config_ambientNotificationDefaultColor);
        int color = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.OMNI_NOTIFICATION_PULSE_COLOR, mDefaultColor,
                            UserHandle.USER_CURRENT);
        mPulseLightColor = (ColorPickerPreference) findPreference("ambient_notification_light_color");
        mPulseLightColor.setAlphaSliderEnabled(true);
        mPulseLightColor.setNewPreviewColor(color);
        mPulseLightColor.setOnPreferenceChangeListener(this);
		
        mPulseTimeout = (ListPreference) findPreference("ambient_notification_light_timeout");
        mPulseTimeout.setOnPreferenceChangeListener(this);
        int pulseTimeout = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.OMNI_AOD_NOTIFICATION_PULSE_TIMEOUT, 0,
				            UserHandle.USER_CURRENT);
        mPulseTimeout.setValue(String.valueOf(pulseTimeout));
        mPulseTimeout.setSummary(mPulseTimeout.getEntry());
		
        mPulseAmbientLight = (SwitchPreference) findPreference("pulse_ambient_light");
        mPulseAmbientLight.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.OMNI_NOTIFICATION_PULSE, 0) == 1));
        mPulseAmbientLight.setOnPreferenceChangeListener(this);

        mAmbientLightEnabled = (SwitchPreference) findPreference("ambient_notification_light_enabled");
        mAmbientLightEnabled.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.OMNI_AOD_NOTIFICATION_PULSE, 0) == 1));
        mAmbientLightEnabled.setOnPreferenceChangeListener(this);

        mAmbientLightHide = (SwitchPreference) findPreference("ambient_notification_light_hide_aod");
        mAmbientLightHide.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.OMNI_AOD_NOTIFICATION_PULSE_CLEAR, 0) == 1));
        mAmbientLightHide.setOnPreferenceChangeListener(this);
		
        mAmbientLightAccent = (SwitchPreference) findPreference("ambient_notification_light_accent");
        mAmbientLightAccent.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.OMNI_NOTIFICATION_PULSE_ACCENT, 0) == 1));
        mAmbientLightAccent.setOnPreferenceChangeListener(this);
        }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VALIDUS;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPulseBrightness) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.OMNI_PULSE_BRIGHTNESS, value);
            return true;
        } else if (preference == mDozeBrightness) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.OMNI_DOZE_BRIGHTNESS, value);
            return true;
        } else if (preference.equals(mPulseLightColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.OMNI_NOTIFICATION_PULSE_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mPulseTimeout)) {
            int pulseTimeout = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.OMNI_AOD_NOTIFICATION_PULSE_TIMEOUT, pulseTimeout, 
					UserHandle.USER_CURRENT);
            int index = mPulseTimeout.findIndexOfValue((String) newValue);
            mPulseTimeout.setSummary(
                    mPulseTimeout.getEntries()[index]);
            return true;
        } else if (preference == mPulseAmbientLight) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.OMNI_NOTIFICATION_PULSE,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mAmbientLightEnabled) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.OMNI_AOD_NOTIFICATION_PULSE,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mAmbientLightHide) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.OMNI_AOD_NOTIFICATION_PULSE_CLEAR,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mAmbientLightAccent) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.OMNI_NOTIFICATION_PULSE_ACCENT,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }
}
