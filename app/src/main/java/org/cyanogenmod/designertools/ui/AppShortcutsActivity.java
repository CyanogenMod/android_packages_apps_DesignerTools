package org.cyanogenmod.designertools.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.cyanogenmod.designertools.DesignerToolsApplication;
import org.cyanogenmod.designertools.utils.LaunchUtils;

/**
 * Created by clark on 12/19/16.
 */

public class AppShortcutsActivity extends Activity {
    private static final String ACTION_SHOW_GRID_OVERLAY =
            "com.scheffsblend.designertools.action.SHOW_GRID_OVERLAY";
    private static final String ACTION_SHOW_MOCK_OVERLAY =
            "com.scheffsblend.designertools.action.SHOW_MOCK_OVERLAY";
    private static final String ACTION_SHOW_COLOR_PICKER_OVERLAY =
            "com.scheffsblend.designertools.action.SHOW_COLOR_PICKER_OVERLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (ACTION_SHOW_GRID_OVERLAY.equals(action)) {
            toggleGridOverlay();
        } else if (ACTION_SHOW_MOCK_OVERLAY.equals(action)) {
            toggleMockOverlay();
        } else if (ACTION_SHOW_COLOR_PICKER_OVERLAY.equals(action)) {
            toggleColorPickerOverlay();
        }
        finish();
    }

    private DesignerToolsApplication getDesignerToolsApplication() {
        return (DesignerToolsApplication) getApplication();
    }

    private void toggleGridOverlay() {
        if (getDesignerToolsApplication().getGridOverlayOn()) {
            LaunchUtils.cancelGridOverlay(this);
        } else {
            LaunchUtils.launchGridOverlay(this);
        }
    }

    private void toggleMockOverlay() {
        if (getDesignerToolsApplication().getMockOverlayOn()) {
            LaunchUtils.cancelMockOverlay(this);
        } else {
            LaunchUtils.launchMockOverlay(this);
        }
    }

    private void toggleColorPickerOverlay() {
        if (getDesignerToolsApplication().getColorPickerOn()) {
            LaunchUtils.cancelColorPickerOverlay(this);
        } else {
            LaunchUtils.launchColorPickerOverlay(this);
        }
    }
}
