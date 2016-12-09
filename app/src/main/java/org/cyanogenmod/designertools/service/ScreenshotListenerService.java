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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.ui.DesignerToolsActivity;
import org.cyanogenmod.designertools.utils.PreferenceUtils;

import java.io.File;

public class ScreenshotListenerService extends Service
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ScreenShotObserver mScreenshotObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceUtils.getShardedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        startForeground(42, getPersistentNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mScreenshotObserver == null) {
            mScreenshotObserver = new ScreenShotObserver(new Handler());
            getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mScreenshotObserver);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceUtils.getShardedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        if (mScreenshotObserver != null) {
            getContentResolver().unregisterContentObserver(mScreenshotObserver);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtils.KEY_SCREENSHOT_INFO.equals(key)) {
            boolean enabled = PreferenceUtils.getScreenshotInfoEnabled(this, false);
            if (!enabled) {
                stopSelf();
            }
        }
    }

    private Notification getPersistentNotification() {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, DesignerToolsActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(this);
        String text = getString(R.string.notif_content_screenshot_info);
        builder.setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.ic_qs_screenshotinfo_on)
                .setContentTitle(getString(R.string.screenshot_qs_tile_label))
                .setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setContentIntent(pi);
        return builder.build();
    }

    private class ScreenShotObserver extends ContentObserver {
        private final String TAG = ScreenShotObserver.class.getSimpleName();
        private final String EXTERNAL_CONTENT_URI_MATCHER =
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
        private final String[] PROJECTION = new String[] {
                MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };
        private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
        private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;

        private Handler mHandler;

        public ScreenShotObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(uri, PROJECTION, null, null,
                            SORT_ORDER);
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        long dateAdded = cursor.getLong(cursor.getColumnIndex(
                                MediaStore.Images.Media.DATE_ADDED));
                        long currentTime = System.currentTimeMillis() / 1000;
                        Log.d(TAG, "path: " + path + ", dateAdded: " + dateAdded +
                                ", currentTime: " + currentTime);
                        if (path.toLowerCase().contains("screenshot") &&
                                Math.abs(currentTime - dateAdded) <=
                                        DEFAULT_DETECT_WINDOW_SECONDS) {
                            Intent intent =
                                    new Intent(ScreenshotListenerService.this,
                                            ScreenshotInfoService.class);
                            intent.putExtra(ScreenshotInfoService.EXTRA_PATH, path);
                            final File file = new File(path);
                            while (!file.exists()) {
                                Thread.sleep(100);
                            }
                            startService(intent);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "open cursor fail");
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            super.onChange(selfChange, uri);
        }
    }
}
