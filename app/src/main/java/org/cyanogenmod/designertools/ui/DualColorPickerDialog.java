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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.ColorUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils.GridPreferences;
import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.sliders.LobsterOpacitySlider;
import com.viewpagerindicator.CirclePageIndicator;

public class DualColorPickerDialog extends DialogFragment {
    private ColorPickerViewHolder[] mColorPickerViews;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private CirclePageIndicator mPageIndicator;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = View.inflate(getContext(), R.layout.dialog_color_picker, null);

        initColorPickerViews();

        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mAdapter = new ColorPickerPagerAdapter();
        mViewPager.setAdapter(mAdapter);

        mPageIndicator = (CirclePageIndicator) v.findViewById(R.id.view_pager_indicator);
        mPageIndicator.setViewPager(mViewPager);
        mPageIndicator.setFillColor(getContext().getColor(R.color.colorGridOverlayCardTint));

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),
                R.style.AppDialog));
        builder.setView(v)
                .setTitle(R.string.color_picker_title)
                .setPositiveButton(R.string.color_picker_accept, mClickListener)
                .setNegativeButton(R.string.color_picker_cancel, mClickListener);

        return builder.create();
    }

    private void initColorPickerViews() {
        mColorPickerViews = new ColorPickerViewHolder[2];

        mColorPickerViews[0] = new ColorPickerViewHolder();
        mColorPickerViews[0].container = View.inflate(getContext(), R.layout.lobsterpicker, null);
        mColorPickerViews[0].picker = (LobsterPicker) mColorPickerViews[0].container
                .findViewById(R.id.lobsterpicker);
        mColorPickerViews[0].slider = (LobsterOpacitySlider) mColorPickerViews[0].container
                .findViewById(R.id.opacityslider);
        mColorPickerViews[0].picker.addDecorator(mColorPickerViews[0].slider);
        int color = ColorUtils.getGridLineColor(getContext());
        mColorPickerViews[0].picker.setColor(color);
        mColorPickerViews[0].picker.setHistory(color);
        mColorPickerViews[0].slider.setOnTouchListener(mSliderTouchListener);

        mColorPickerViews[1] = new ColorPickerViewHolder();
        mColorPickerViews[1].container = View.inflate(getContext(), R.layout.lobsterpicker, null);
        mColorPickerViews[1].picker = (LobsterPicker) mColorPickerViews[1].container
                .findViewById(R.id.lobsterpicker);
        mColorPickerViews[1].slider = (LobsterOpacitySlider) mColorPickerViews[1].container
                .findViewById(R.id.opacityslider);
        mColorPickerViews[1].picker.addDecorator(mColorPickerViews[1].slider);
        color = ColorUtils.getKeylineColor(getContext());
        mColorPickerViews[1].picker.setColor(color);
        mColorPickerViews[1].picker.setHistory(color);
        mColorPickerViews[1].slider.setOnTouchListener(mSliderTouchListener);
    }

    private View.OnTouchListener mSliderTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            v.onTouchEvent(event);
            return true;
        }
    };

    private DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    GridPreferences.setGridLineColor(getContext(),
                            mColorPickerViews[0].picker.getColor());
                    GridPreferences.setKeylineColor(getContext(),
                            mColorPickerViews[1].picker.getColor());
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
            }
            dialog.dismiss();
        }
    };

    private class ColorPickerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mColorPickerViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mColorPickerViews[position].container);

            return mColorPickerViews[position].container;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getContext().getString(R.string.color_picker_grid_page_title);
            } else {
                return getContext().getString(R.string.color_picker_keyline_page_title);
            }
        }
    }

    private class ColorPickerViewHolder {
        View container;
        LobsterPicker picker;
        LobsterOpacitySlider slider;
    }
}
