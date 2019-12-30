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

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import org.cyanogenmod.designertools.R;

import org.cyanogenmod.designertools.qs.OnOffTileState;
import org.cyanogenmod.designertools.utils.ColorUtils;
import org.cyanogenmod.designertools.utils.LaunchUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils;
import org.cyanogenmod.designertools.utils.PreferenceUtils.GridPreferences;
import org.cyanogenmod.designertools.widget.DualColorPicker;
import org.cyanogenmod.designertools.widget.GridPreview;

public class GridOverlayCardFragment extends DesignerToolCardFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private CheckBox mIncludeKeylines;
    private CheckBox mIncudeCustomGrid;
    private SeekBar mColumnSizer;
    private SeekBar mRowSizer;
    private GridPreview mGridPreview;
    private DualColorPicker mDualColorPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View base = super.onCreateView(inflater, container, savedInstanceState);

        final Context context = getContext();
        setTitleText(R.string.header_title_grid_overlay);
        setTitleSummary(R.string.header_summary_grid_overlay);
        setIconResource(R.drawable.ic_qs_grid_on);
        base.setBackgroundTintList(ColorStateList.valueOf(
                context.getColor(R.color.colorGridOverlayCardTint)));

        View v = inflater.inflate(R.layout.grid_overlay_content, mCardContent, true);
        mIncludeKeylines = v.findViewById(R.id.include_keylines);
        mIncudeCustomGrid = v.findViewById(R.id.include_custom_grid_size);
        mColumnSizer = v.findViewById(R.id.column_sizer);
        mColumnSizer.setProgress((GridPreferences.getGridColumnSize(getContext(), 8) - 4) / 2);
        mRowSizer = v.findViewById(R.id.row_sizer);
        mRowSizer.setProgress((GridPreferences.getGridRowSize(getContext(), 8) - 4) / 2);
        mGridPreview = v.findViewById(R.id.grid_preview);
        mGridPreview.setColumnSize(GridPreferences.getGridColumnSize(getContext(), 8));
        mGridPreview.setRowSize(GridPreferences.getGridRowSize(getContext(), 8));

        mColumnSizer.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mRowSizer.setOnSeekBarChangeListener(mSeekBarChangeListener);

        mIncludeKeylines.setChecked(GridPreferences.getShowKeylines(context, false));
        mIncludeKeylines.setOnCheckedChangeListener(mCheckChangedListener);

        setIncludeCustomGridLines(GridPreferences.getUseCustomGridSize(context, false));
        mIncudeCustomGrid.setOnCheckedChangeListener(mCheckChangedListener);

        mRowSizer.setOnTouchListener(new View.OnTouchListener() {
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
        });

        mDualColorPicker = v.findViewById(R.id.color_picker);
        mDualColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DualColorPickerDialog dualColorPickerDialog = new DualColorPickerDialog();
                dualColorPickerDialog.show(fm, "color_picker_dialog");
            }
        });

        return base;
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceUtils.getShardedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
        mEnabledSwitch.setChecked(getApplicationContext().getGridOverlayOn());
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceUtils.getShardedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected int getCardStyleResourceId() {
        return R.style.AppTheme_GridOverlayCard;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            LaunchUtils.lauchGridOverlayOrPublishTile(getContext(),
                    GridPreferences.getGridOverlayActive(getContext(), false)
                            ? OnOffTileState.STATE_ON
                            : OnOffTileState.STATE_OFF);
        } else {
            LaunchUtils.cancelGridOverlayOrUnpublishTile(getContext());
        }
    }

    private void setIncludeCustomGridLines(boolean include) {
        mIncudeCustomGrid.setChecked(include);
        mColumnSizer.setEnabled(include);
        mRowSizer.setEnabled(include);
    }

    private CompoundButton.OnCheckedChangeListener mCheckChangedListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == mIncludeKeylines) {
                GridPreferences.setShowKeylines(getContext(), isChecked);
            } else if (buttonView == mIncudeCustomGrid){
                GridPreferences.setUseCustomGridSize(getContext(), isChecked);
                if (isChecked) {
                    GridPreferences.setGridColumnSize(getContext(), mGridPreview.getColumnSize());
                    GridPreferences.setGridRowSize(getContext(), mGridPreview.getRowSize());
                }
                mColumnSizer.setEnabled(isChecked);
                mRowSizer.setEnabled(isChecked);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int size = 4 + progress * 2;
            if (seekBar == mColumnSizer) {
                mGridPreview.setColumnSize(size);
                GridPreferences.setGridColumnSize(getContext(), size);
            } else if (seekBar == mRowSizer) {
                mGridPreview.setRowSize(size);
                GridPreferences.setGridRowSize(getContext(), size);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (GridPreferences.KEY_GRID_LINE_COLOR.equals(key)) {
            mDualColorPicker.setPrimaryColor(ColorUtils.getGridLineColor(getContext()));
        } else if(GridPreferences.KEY_KEYLINE_COLOR.equals(key)) {
            mDualColorPicker.setSecondaryColor(ColorUtils.getKeylineColor(getContext()));
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {}
    };
}
