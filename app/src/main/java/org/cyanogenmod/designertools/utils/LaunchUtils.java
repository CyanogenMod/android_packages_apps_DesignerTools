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
import android.os.Build;

import org.cyanogenmod.designertools.DesignerToolsApplication;
import org.cyanogenmod.designertools.overlays.ColorPickerOverlay;
import org.cyanogenmod.designertools.overlays.GridOverlay;
import org.cyanogenmod.designertools.overlays.MockOverlay;
import org.cyanogenmod.designertools.qs.ColorPickerQuickSettingsTile;
import org.cyanogenmod.designertools.qs.GridQuickSettingsTile;
import org.cyanogenmod.designertools.qs.MockQuickSettingsTile;
import org.cyanogenmod.designertools.ui.ScreenRecordRequestActivity;
import org.cyanogenmod.designertools.ui.StartOverlayActivity;

public class LaunchUtils {
    public static boolean isCyanogenMod(Context context) {
        return context.getPackageManager().hasSystemFeature("org.cyanogenmod.theme");
    }

    public static void lauchGridOverlayOrPublishTile(Context context, int state) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            GridQuickSettingsTile.publishGridTile(context, state);
        } else {
            startOverlayActivity(context, StartOverlayActivity.GRID_OVERLAY);
        }
    }

    public static void cancelGridOverlayOrUnpublishTile(Context context) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            GridQuickSettingsTile.unpublishGridTile(context);
        } else {
            Intent newIntent = new Intent(context, GridOverlay.class);
            context.stopService(newIntent);
            PreferenceUtils.setGridOverlayActive(context, false);
            PreferenceUtils.setGridQsTileEnabled(context, false);
        }
    }

    public static void lauchMockPverlayOrPublishTile(Context context, int state) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            MockQuickSettingsTile.publishMockTile(context, state);
        } else {
            startOverlayActivity(context, StartOverlayActivity.MOCK_OVERLAY);
        }
    }

    public static void cancelMockOverlayOrUnpublishTile(Context context) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            MockQuickSettingsTile.unpublishMockTile(context);
        } else {
            Intent newIntent = new Intent(context, MockOverlay.class);
            context.stopService(newIntent);
            PreferenceUtils.setMockOverlayActive(context, false);
            PreferenceUtils.setMockQsTileEnabled(context, false);
        }
    }

    public static void lauchColorPickerOrPublishTile(Context context, int state) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            ColorPickerQuickSettingsTile.publishColorPickerTile(context, state);
        } else {
            startOverlayActivity(context, StartOverlayActivity.COLOR_PICKER_OVERLAY);
        }
    }

    public static void cancelColorPickerOrUnpublishTile(Context context) {
        if (isCyanogenMod(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            ColorPickerQuickSettingsTile.unpublishColorPickerTile(context);
        } else {
            Intent newIntent = new Intent(context, ColorPickerOverlay.class);
            context.stopService(newIntent);
            PreferenceUtils.setColorPickerActive(context, false);
            PreferenceUtils.setColorPickerQsTileEnabled(context, false);
        }
    }

    public static void startColorPickerOrRequestPermission(Context context) {
        DesignerToolsApplication app =
                (DesignerToolsApplication) context.getApplicationContext();
        if (app.getScreenRecordResultCode() == Activity.RESULT_OK && app.getScreenRecordResultData() != null) {
            Intent newIntent = new Intent(context, ColorPickerOverlay.class);
            context.startService(newIntent);
            PreferenceUtils.setColorPickerActive(context, true);
            PreferenceUtils.setColorPickerQsTileEnabled(context, true);
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
