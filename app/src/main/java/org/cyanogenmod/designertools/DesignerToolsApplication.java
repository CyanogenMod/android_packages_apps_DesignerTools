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
package org.cyanogenmod.designertools;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

public class DesignerToolsApplication extends Application {

    private int mResultCode = Activity.RESULT_CANCELED;
    private Intent mResultData;

    private boolean mGridOverlayOn;
    private boolean mMockOverlayOn;
    private boolean mColorPickerOn;
    private boolean mScreenshotOn;

    public void setScreenRecordPermissionData(int resultCode, Intent resultData) {
        mResultCode = resultCode;
        mResultData = resultData;
    }

    public int getScreenRecordResultCode() {
        return mResultCode;
    }

    public Intent getScreenRecordResultData() {
        return mResultData;
    }

    public void setGridOverlayOn(boolean on) {
        mGridOverlayOn = on;
    }

    public boolean getGridOverlayOn() {
        return mGridOverlayOn;
    }

    public void setMockOverlayOn(boolean on) {
        mMockOverlayOn = on;
    }

    public boolean getMockOverlayOn() {
        return mMockOverlayOn;
    }

    public void setColorPickerOn(boolean on) {
        mColorPickerOn = on;
    }

    public boolean getColorPickerOn() {
        return mColorPickerOn;
    }

    public void setScreenshotOn(boolean on) {
        mScreenshotOn = on;
    }

    public boolean getScreenshotOn() {
        return mScreenshotOn;
    }
}
