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
package org.cyanogenmod.designertools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.cyanogenmod.designertools.qs.ColorPickerQuickSettingsTile;
import org.cyanogenmod.designertools.qs.GridQuickSettingsTile;
import org.cyanogenmod.designertools.qs.MockQuickSettingsTile;
import org.cyanogenmod.designertools.service.ScreenshotListenerService;
import org.cyanogenmod.designertools.utils.LaunchUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final boolean isCm = LaunchUtils.isCyanogenMod(context);
        if (PreferenceUtils.getGridQsTileEnabled(context, false)) {
            PreferenceUtils.setGridOverlayActive(context, false);
            if (isCm) GridQuickSettingsTile.publishGridTile(context);
        }
        if (PreferenceUtils.getMockQsTileEnabled(context, false)) {
            PreferenceUtils.setMockOverlayActive(context, false);
            if (isCm) MockQuickSettingsTile.publishMockTile(context);
        }
        if (PreferenceUtils.getColorPickerQsTileEnabled(context, false)) {
            PreferenceUtils.setColorPickerActive(context, false);
            if (isCm) ColorPickerQuickSettingsTile.publishColorPickerTile(context);
        }
        if (PreferenceUtils.getScreenshotInfoEnabled(context, false)) {
            Intent newIntent = new Intent(context, ScreenshotListenerService.class);
            context.startService(newIntent);
        }
    }
}
