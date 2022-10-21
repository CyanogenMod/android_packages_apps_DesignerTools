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
package org.cyanogenmod.designertools.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.cyanogenmod.designertools.DesignerToolsApplication;
import org.cyanogenmod.designertools.overlays.ColorPickerOverlay;
import org.cyanogenmod.designertools.overlays.GridOverlay;
import org.cyanogenmod.designertools.overlays.MockOverlay;
import org.cyanogenmod.designertools.ui.ScreenRecordRequestActivity;
import org.cyanogenmod.designertools.ui.StartOverlayActivity;
import org.cyanogenmod.designertools.utils.PreferenceUtils.ColorPickerPreferences;
import org.cyanogenmod.designertools.utils.PreferenceUtils.GridPreferences;
import org.cyanogenmod.designertools.utils.PreferenceUtils.MockPreferences;

public class LaunchUtils {
    public static void launchGridOverlay(Context context) {
        startOverlayActivity(context, StartOverlayActivity.GRID_OVERLAY);
    }

    public static void cancelGridOverlay(Context context) {
        Intent newIntent = new Intent(context, GridOverlay.class);
        context.stopService(newIntent);
        GridPreferences.setGridOverlayActive(context, false);
        GridPreferences.setGridQsTileEnabled(context, false);
    }

    public static void launchMockOverlay(Context context) {
        startOverlayActivity(context, StartOverlayActivity.MOCK_OVERLAY);
    }

    public static void cancelMockOverlay(Context context) {
        Intent newIntent = new Intent(context, MockOverlay.class);
        context.stopService(newIntent);
        MockPreferences.setMockOverlayActive(context, false);
        MockPreferences.setMockQsTileEnabled(context, false);
    }

    public static void launchColorPickerOverlay(Context context) {
        startOverlayActivity(context, StartOverlayActivity.COLOR_PICKER_OVERLAY);
    }

    public static void cancelColorPickerOverlay(Context context) {
        Intent newIntent = new Intent(context, ColorPickerOverlay.class);
        context.stopService(newIntent);
        ColorPickerPreferences.setColorPickerActive(context, false);
        ColorPickerPreferences.setColorPickerQsTileEnabled(context, false);
    }

    public static void startColorPickerOrRequestPermission(Context context) {
        DesignerToolsApplication app =
                (DesignerToolsApplication) context.getApplicationContext();
        if (app.getScreenRecordResultCode() == Activity.RESULT_OK && app.getScreenRecordResultData() != null) {
            Intent newIntent = new Intent(context, ColorPickerOverlay.class);
            context.startService(newIntent);
            ColorPickerPreferences.setColorPickerActive(context, true);
            ColorPickerPreferences.setColorPickerQsTileEnabled(context, true);
        } else {
            Intent intent = new Intent(context, ScreenRecordRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private static void startOverlayActivity(Context context, int overlayType) {
        Intent intent = new Intent(context, StartOverlayActivity.class);
        intent.putExtra(StartOverlayActivity.EXTRA_OVERLAY_TYPE, overlayType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
