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
import org.cyanogenmod.designertools.utils.NotificationUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils.ScreenshotPreferences;

public class ScreenshotListenerService extends Service
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String CHANNEL_ID = "DesignerTools.ScreenshotListenerService";

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
        if (ScreenshotPreferences.KEY_SCREENSHOT_INFO.equals(key)) {
            boolean enabled = ScreenshotPreferences.getScreenshotInfoEnabled(this, false);
            if (!enabled) {
                stopSelf();
            }
        }
    }

    private Notification getPersistentNotification() {
        final PendingIntent pi = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, DesignerToolsActivity.class),
                PendingIntent.FLAG_IMMUTABLE);
        final String contentText = getString(R.string.notif_content_screenshot_info);

        return NotificationUtils.createForegroundServiceNotification(
                this,
                CHANNEL_ID,
                R.drawable.ic_qs_screenshotinfo_on,
                getString(R.string.screenshot_qs_tile_label),
                contentText,
                pi);
    }

    private class ScreenShotObserver extends ContentObserver {
        private final String TAG = ScreenShotObserver.class.getSimpleName();
        private final String EXTERNAL_CONTENT_URI_MATCHER =
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
        private final String[] PROJECTION = new String[] {
                MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN
        };
        private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
        private static final long ADD_INFO_DELAY_MS = 1000;

        private String mLastProcessedImage = null;
        private Handler mHandler;

        ScreenShotObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
                try (Cursor cursor = getContentResolver().query(uri, PROJECTION, null, null,
                        SORT_ORDER)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        if (path.substring(path.lastIndexOf("/") + 1).toLowerCase().startsWith("screenshot") &&
                                !path.equals(mLastProcessedImage)) {
                            mLastProcessedImage = path;
                            final Intent intent =
                                    new Intent(ScreenshotListenerService.this,
                                            ScreenshotInfoService.class);
                            intent.putExtra(ScreenshotInfoService.EXTRA_URI, uri);
                            // give time for screenshot to be fully written to storage
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startService(intent);
                                }
                            }, ADD_INFO_DELAY_MS);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "open cursor fail", e);
                }
            }
            super.onChange(selfChange, uri);
        }
    }
}
