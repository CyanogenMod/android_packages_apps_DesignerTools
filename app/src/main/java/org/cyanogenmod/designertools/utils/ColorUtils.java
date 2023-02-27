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

import android.content.Context;

import org.cyanogenmod.designertools.R;

public class ColorUtils {
    public static int getGridLineColor(Context context) {
        return PreferenceUtils.GridPreferences.getGridLineColor(context,
                context.getColor(R.color.dualColorPickerDefaultPrimaryColor));
    }

    public static int getKeylineColor(Context context) {
        return PreferenceUtils.GridPreferences.getKeylineColor(context,
                context.getColor(R.color.dualColorPickerDefaultSecondaryColor));
    }
}
