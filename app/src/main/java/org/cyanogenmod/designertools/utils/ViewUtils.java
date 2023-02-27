/*
 * Copyright (C) 2019 Scheff's Blend
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

import android.os.Build;
import android.view.WindowManager;

public class ViewUtils {

    public static int getWindowType() {
        return getWindowType(false);
    }

    public static int getWindowType(boolean useSystemAlert) {
        return (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1)
                ? (useSystemAlert
                    ? WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                    : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY)
                : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }
}
