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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import org.cyanogenmod.designertools.overlays.GridOverlay;
import org.cyanogenmod.designertools.overlays.MockOverlay;
import org.cyanogenmod.designertools.utils.LaunchUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils.GridPreferences;
import org.cyanogenmod.designertools.utils.PreferenceUtils.MockPreferences;

public class StartOverlayActivity extends Activity {
    private static final int REQUEST_OVERLAY_PERMSSISION = 42;

    public static final String EXTRA_OVERLAY_TYPE = "overlayType";
    public static final int GRID_OVERLAY = 0;
    public static final int MOCK_OVERLAY = 1;
    public static final int COLOR_PICKER_OVERLAY = 2;

    private int mOverlayType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_OVERLAY_TYPE)) {
            mOverlayType = intent.getIntExtra(EXTRA_OVERLAY_TYPE, -1);
            if (Settings.canDrawOverlays(this)) {
                startOverlayService(mOverlayType);
                finish();
            } else {
                Intent newIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(newIntent, REQUEST_OVERLAY_PERMSSISION);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMSSISION) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayService(mOverlayType);
            }
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startOverlayService(int overlayType) {
        switch (overlayType) {
            case GRID_OVERLAY:
                Intent newIntent = new Intent(this, GridOverlay.class);
                this.startService(newIntent);
                GridPreferences.setGridOverlayActive(this, true);
                GridPreferences.setGridQsTileEnabled(this, true);
                break;
            case MOCK_OVERLAY:
                newIntent = new Intent(this, MockOverlay.class);
                this.startService(newIntent);
                MockPreferences.setMockOverlayActive(this, true);
                MockPreferences.setMockQsTileEnabled(this, true);
                break;
            case COLOR_PICKER_OVERLAY:
                LaunchUtils.startColorPickerOrRequestPermission(this);
                break;
        }
    }
}
