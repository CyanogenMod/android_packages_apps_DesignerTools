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
package org.cyanogenmod.designertools.service.qs;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.cyanogenmod.designertools.DesignerToolsApplication;
import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.LaunchUtils;

public class ColorPickerTileService extends TileService {
    public ColorPickerTileService() {
        super();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_qs_colorpicker_on));
    }

    @Override
    public void onTileRemoved() {
       super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile(((DesignerToolsApplication) getApplication()).getColorPickerOn());
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean isOn = ((DesignerToolsApplication) getApplicationContext()).getColorPickerOn();
        if (isOn) {
            LaunchUtils.cancelColorPickerOverlay(this);
        } else {
            LaunchUtils.launchColorPickerOverlay(this);
        }
        updateTile(!isOn);
    }

    private void updateTile(boolean isOn) {
        final Tile tile = getQsTile();
        tile.setState(isOn ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
