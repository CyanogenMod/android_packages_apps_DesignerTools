/*
 * Copyright (C) 2016 The CyanogenMod Project
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
package org.cyanogenmod.designertools.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.service.ScreenshotListenerService;
import org.cyanogenmod.designertools.utils.PreferenceUtils.ScreenshotPreferences;

public class ScreenshotCardFragment extends DesignerToolCardFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View base = super.onCreateView(inflater, container, savedInstanceState);
        setTitleText(R.string.header_title_screenshot);
        setTitleSummary(R.string.header_summary_screenshot);
        setIconResource(R.drawable.ic_qs_screenshotinfo_on);
        base.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.colorScreenshotCardTint)));

        mEnabledSwitch.setChecked(ScreenshotPreferences.getScreenshotInfoEnabled(getContext(),
                false));

        return base;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScreenshotPreferences.setScreenshotInfoEnabled(getContext(), true);
            Intent newIntent = new Intent(getContext(), ScreenshotListenerService.class);
            getContext().startService(newIntent);
            mEnabledSwitch.setChecked(true);
        } else {
            mEnabledSwitch.setChecked(false);
        }
    }

    @Override
    protected int getCardStyleResourceId() {
        return R.style.AppTheme_ScreenshotCard;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (getActivity().checkSelfPermission(getReadPermission())
                    == PackageManager.PERMISSION_GRANTED) {
                ScreenshotPreferences.setScreenshotInfoEnabled(getContext(), true);
                Intent newIntent = new Intent(getContext(), ScreenshotListenerService.class);
                getContext().startService(newIntent);
            } else {
                mEnabledSwitch.setChecked(false);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
                } else {
                    requestPermissions(new String[]{getReadPermission()}, 42);
                }
            }
        } else {
            ScreenshotPreferences.setScreenshotInfoEnabled(getContext(), false);
        }
    }

    private static String getReadPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
    }
}
