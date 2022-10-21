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
package org.cyanogenmod.designertools.overlays;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import org.cyanogenmod.designertools.DesignerToolsApplication;
import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.MockupUtils;
import org.cyanogenmod.designertools.utils.NotificationUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils.MockPreferences;
import org.cyanogenmod.designertools.utils.ViewUtils;

public class MockOverlay extends Service {
    private static final int NOTIFICATION_ID = MockOverlay.class.hashCode();
    private static final String CHANNEL_ID = "DesignerTools.MockOverlay";

    private static final String ACTION_HIDE_OVERLAY = "hide_mock_overlay";
    private static final String ACTION_SHOW_OVERLAY = "show_mock_overlay";

    private WindowManager mWindowManager;
    private MockOverlayView mOverlayView;
    private WindowManager.LayoutParams mParams;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setup();
        ((DesignerToolsApplication) getApplicationContext()).setMockOverlayOn(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null) {
            hideOverlay(new Runnable() {
                @Override
                public void run() {
                    removeViewIfAttached(mOverlayView);
                    mOverlayView = null;
                }
            });
        }
        ((DesignerToolsApplication) getApplicationContext()).setMockOverlayOn(false);
    }

    private void setup() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(size);
        mParams = new WindowManager.LayoutParams(
                size.x, size.y,
                ViewUtils.getWindowType(),
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSPARENT);
        mOverlayView = new MockOverlayView(this);
        mOverlayView.setAlpha(0f);
        mWindowManager.addView(mOverlayView, mParams);
        mOverlayView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mOverlayView.animate().alpha(1f);
                mOverlayView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        startForeground(NOTIFICATION_ID, getPersistentNotification(true));
    }

    private void removeViewIfAttached(View v) {
        if (v.isAttachedToWindow()) {
            mWindowManager.removeView(v);
        }
    }

    private void updateNotification(boolean actionIsHide) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.notify(NOTIFICATION_ID, getPersistentNotification(actionIsHide));
    }

    private Notification getPersistentNotification(boolean actionIsHide) {
        int icon = actionIsHide ? R.drawable.ic_qs_overlay_on
                : R.drawable.ic_qs_overlay_off;
        final String contentText = getString(actionIsHide ? R.string.notif_content_hide_mock_overlay
                : R.string.notif_content_show_mock_overlay);
        final PendingIntent pi = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(actionIsHide ? ACTION_HIDE_OVERLAY : ACTION_SHOW_OVERLAY),
                PendingIntent.FLAG_IMMUTABLE);

        return NotificationUtils.createForegroundServiceNotification(
                this,
                CHANNEL_ID,
                icon,
                getString(R.string.mock_qs_tile_label),
                contentText,
                pi);
    }

    private void showOverlay() {
        mWindowManager.addView(mOverlayView, mParams);
        updateNotification(true);
        mOverlayView.animate().alpha(1f);
    }

    private void hideOverlay(final Runnable endAction) {
        mOverlayView.animate().alpha(0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                mOverlayView.setAlpha(0f);
                removeViewIfAttached(mOverlayView);
                if (endAction != null) endAction.run();
            }
        });
    }

    static class MockOverlayView extends AppCompatImageView {
        public MockOverlayView(Context context) {
            super(context);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            SharedPreferences prefs = PreferenceUtils.getShardedPreferences(getContext());
            prefs.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
            setImageBitmap(getBitmapForOrientation(getResources().getConfiguration().orientation));
            setImageAlpha(MockPreferences.getMockOpacity(getContext(), 10));
            invalidate();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            SharedPreferences prefs = PreferenceUtils.getShardedPreferences(getContext());
            prefs.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            setImageBitmap(getBitmapForOrientation(getResources().getConfiguration().orientation));
        }

        private Bitmap getBitmapForOrientation(int orientation) {
            return orientation == Configuration.ORIENTATION_PORTRAIT
                    ? MockupUtils.getPortraitMockup(getContext())
                    : MockupUtils.getLandscapeMockup(getContext());
        }

        private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs,
                                                  String key) {
                if (MockPreferences.KEY_MOCKUP_OVERLAY_PORTRAIT.equals(key) ||
                        MockPreferences.KEY_MOCKUP_OVERLAY_LANDSCAPE.equals(key)) {
                    setImageBitmap(getBitmapForOrientation(
                            getResources().getConfiguration().orientation));
                    invalidate();
                } else if (MockPreferences.KEY_MOCK_OPACITY.equals(key)) {
                    setImageAlpha(MockPreferences.getMockOpacity(getContext(), 10));
                    invalidate();
                }
            }
        };
    }
}
