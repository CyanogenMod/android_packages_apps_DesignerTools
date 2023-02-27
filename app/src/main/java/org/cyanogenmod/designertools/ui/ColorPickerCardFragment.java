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

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.LaunchUtils;

public class ColorPickerCardFragment extends DesignerToolCardFragment {
    private static final int REQUEST_OVERLAY_PERMISSION = 0x42;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View base = super.onCreateView(inflater, container, savedInstanceState);
        setTitleText(R.string.header_title_color_picker);
        setTitleSummary(R.string.header_summary_color_picker);
        setIconResource(R.drawable.ic_qs_colorpicker_on);
        base.setBackgroundTintList(ColorStateList.valueOf(
                getActivity().getColor(R.color.colorColorPickerCardTint)));

        return base;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEnabledSwitch.setChecked(getApplicationContext().getColorPickerOn());
    }

    @Override
    protected int getCardStyleResourceId() {
        return R.style.AppTheme_ColorPickerCard;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == getApplicationContext().getColorPickerOn()) return;
        if (isChecked) {
            enableFeature(true);
        } else {
            enableFeature(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(getContext())) {
                mEnabledSwitch.setChecked(true);
            } else {
                mEnabledSwitch.setChecked(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void isEnabled() {
    }

    private void enableFeature(boolean enable) {
        if (enable) {
            LaunchUtils.launchColorPickerOverlay(getContext());
        } else {
            LaunchUtils.cancelColorPickerOverlay(getContext());
        }
    }
}
