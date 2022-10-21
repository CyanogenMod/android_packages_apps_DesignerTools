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
package org.cyanogenmod.designertools.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.LaunchUtils;
import org.cyanogenmod.designertools.utils.LayoutRenderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenshotInfoService extends IntentService {
    private static final String TAG = ScreenshotInfoService.class.getSimpleName();
    private static final String FILENAME_PROC_VERSION = "/proc/version";

    public static final String EXTRA_URI = "uri";

    public ScreenshotInfoService() {
        super("ScreenshotInfoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.hasExtra((EXTRA_URI))) {
            Uri uri = intent.getParcelableExtra(EXTRA_URI);
            ContentResolver resolver = getApplicationContext()
                    .getContentResolver();
            try (InputStream stream = resolver.openInputStream(uri)) {
                Bitmap paneBmp = getInfoPane();
                Bitmap screenshotBmp = BitmapFactory.decodeStream(stream);
                saveModifiedScreenshot(screenshotBmp, paneBmp, uri);
            } catch (IOException e) {
                Log.e(TAG, "Failed to store screenshot info", e);
            }
        }
    }

    private Bitmap getInfoPane() {
        Date date = new Date();
        String dateTime = String.format("%s at %s", DateFormat.getDateInstance().format(date),
                DateFormat.getTimeInstance().format(date));
        String device = Build.MODEL;
        String codeName = Build.DEVICE;
        String build = getCmVersionString(this);
        String density = getDensityString();
        String kernelVersion = getFormattedKernelVersion();

        View pane = View.inflate(this, R.layout.screenshot_info, null);
        TextView tv = (TextView) pane.findViewById(R.id.date_time_info);
        tv.setText(dateTime);
        tv = (TextView) pane.findViewById(R.id.device_name);
        tv.setText(device);
        tv = (TextView) pane.findViewById(R.id.code_name);
        tv.setText(codeName);
        tv = (TextView) pane.findViewById(R.id.build);
        tv.setText(build);
        tv = (TextView) pane.findViewById(R.id.density);
        tv.setText(density);
        tv = (TextView) pane.findViewById(R.id.kernel);
        tv.setText(kernelVersion);

        return LayoutRenderUtils.renderViewToBitmap(pane);
    }

    private String getDensityString() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        StringBuilder builder = new StringBuilder();
        switch (dm.densityDpi) {
            case DisplayMetrics.DENSITY_MEDIUM:
                builder.append("mdpi");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                builder.append("hdpi");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                builder.append("xhdpi");
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                builder.append("xxhdpi");
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                builder.append("xxxhdpi");
                break;
            default:
                builder.append(dm.densityDpi + "dpi");
                break;
        }
        builder.append(" (" + dm.density + "x)");
        builder.append(" - ");
        builder.append(size.x);
        builder.append("x");
        builder.append(size.y);
        return  builder.toString();
    }

    /**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (IOException e) {
            Log.e(TAG,
                    "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "";
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
                "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                        "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                        "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                        "(#\\d+) " +              /* group 3: "#1" */
                        "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "";
        } else if (m.groupCount() < 4) {
            Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    private void saveModifiedScreenshot(Bitmap screenshot, Bitmap infoPane, Uri uri)
            throws IOException {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "w");

        WindowManager wm = getSystemService(WindowManager.class);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);
        if (screenshot.getWidth() != size.x || screenshot.getHeight() != size.y) {
            Log.d(TAG, "Not adding info, screenshot too large");
            return;
        }

        Bitmap newBmp = Bitmap.createBitmap(screenshot.getWidth() + infoPane.getWidth(),
                screenshot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBmp);
        canvas.drawColor(getColor(R.color.screenshot_info_background_color));
        canvas.drawBitmap(screenshot, 0, 0, null);
        canvas.drawBitmap(infoPane, screenshot.getWidth(), 0, null);
        screenshot.recycle();
        infoPane.recycle();
        if (pfd != null) {
            newBmp.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(pfd.getFileDescriptor()));
            pfd.close();
        }
        newBmp.recycle();
    }

    private String getCmVersionString(Context context) {
        if (LaunchUtils.isCyanogenMod(context)) {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = null;
            try {
                SystemProperties = cl.loadClass("android.os.SystemProperties");
                //Parameters Types
                Class[] paramTypes = new Class[1];
                paramTypes[0] = String.class;

                Method get = SystemProperties.getMethod("get", paramTypes);

                //Parameters
                Object[] params = new Object[1];
                params[0] = "ro.cm.version";

                return (String) get.invoke(SystemProperties, params);
            } catch (ClassNotFoundException |
                    NoSuchMethodException |
                    IllegalAccessException |
                    InvocationTargetException e) {
            /* don't care, will fallback to Build.ID */
            }
        }

        return Build.ID;
    }
}
