/*
 * Copyright (C) 2019 Validus
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

package com.gzr.funhouse.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Vibrator;
import androidx.preference.PreferenceCategory;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.gzr.funhouse.preference.SystemSettingSeekBarPreference;
import com.gzr.funhouse.preference.CustomSeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class QSPanel extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_QS_PANEL_ALPHA = "qs_panel_alpha";
    private static final String QS_PANEL_COLOR = "qs_panel_color";
    private static final String QS_BLUR_ALPHA = "qs_blur_alpha";
    private static final String QS_BLUR_INTENSITY = "qs_blur_intensity";
    static final int DEFAULT_QS_PANEL_COLOR = 0xffffffff;

    private ColorPickerPreference mQsPanelColor;
    private SystemSettingSeekBarPreference mQsPanelAlpha;
    private CustomSeekBarPreference mQSBlurAlpha;
    private CustomSeekBarPreference mQSBlurIntensity;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.qspanel);

        mQsPanelAlpha = (SystemSettingSeekBarPreference) findPreference(KEY_QS_PANEL_ALPHA);
        int qsPanelAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_PANEL_BG_ALPHA, 221);
        mQsPanelAlpha.setValue((int)(((double) qsPanelAlpha / 255) * 100));
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        mQsPanelColor = (ColorPickerPreference) findPreference(QS_PANEL_COLOR);
        mQsPanelColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.QS_PANEL_BG_COLOR, DEFAULT_QS_PANEL_COLOR, UserHandle.USER_CURRENT);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQsPanelColor.setSummary(hexColor);
        mQsPanelColor.setNewPreviewColor(intColor);

        mQSBlurAlpha = (CustomSeekBarPreference) findPreference(QS_BLUR_ALPHA);
        int qsBlurAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_BLUR_ALPHA, 100);
        mQSBlurAlpha.setValue(qsBlurAlpha);
        mQSBlurAlpha.setOnPreferenceChangeListener(this);

        mQSBlurIntensity = (CustomSeekBarPreference) findPreference(QS_BLUR_INTENSITY);
        int qsBlurIntensity = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_BLUR_INTENSITY, 30);
        mQSBlurIntensity.setValue(qsBlurIntensity);
        mQSBlurIntensity.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VALIDUS;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQsPanelAlpha) {
            int bgAlpha = (Integer) newValue;
            int trueValue = (int) (((double) bgAlpha / 100) * 255);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_PANEL_BG_ALPHA, trueValue);
            return true;
        } else if (preference == mQsPanelColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_PANEL_BG_COLOR, intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQSBlurAlpha) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_BLUR_ALPHA, value);
            return true;
        } else if (preference == mQSBlurIntensity) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_BLUR_INTENSITY, value);
            return true;
        }
        return true;
    }

}
