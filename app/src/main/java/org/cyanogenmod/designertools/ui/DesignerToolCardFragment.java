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

import android.app.Fragment;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.cyanogenmod.designertools.DesignerToolsApplication;
import org.cyanogenmod.designertools.R;

public class DesignerToolCardFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    protected View mParentLayout;
    protected ImageView mIcon;
    protected TextView mHeaderTitle;
    protected TextView mHeaderSummary;
    protected Switch mEnabledSwitch;
    protected FrameLayout mCardContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ContextWrapper ctx = new ContextWrapper(inflater.getContext());
        ctx.setTheme(getCardStyleResourceId());
        inflater.cloneInContext(ctx);
        View v = inflater.inflate(R.layout.card_layout, container, true);
        mParentLayout = v.findViewById(R.id.parent_layout);
        mIcon = v.findViewById(R.id.header_icon);
        mHeaderTitle = v.findViewById(R.id.header_title);
        mHeaderSummary = v.findViewById(R.id.header_summary);
        mEnabledSwitch = v.findViewById(R.id.enable_switch);
        mCardContent = v.findViewById(R.id.card_content);

        mEnabledSwitch.setOnCheckedChangeListener(this);

        return v;
    }

    protected void setIconResource(int resId) {
        if (mIcon != null) mIcon.setImageResource(resId);
    }

    protected void setTitleText(CharSequence text) {
        if (mHeaderTitle != null) mHeaderTitle.setText(text);
    }

    protected void setTitleText(int resId) {
        if (mHeaderTitle != null) mHeaderTitle.setText(resId);
    }

    protected void setTitleSummary(CharSequence text) {
        if (mHeaderSummary != null) mHeaderSummary.setText(text);
    }

    protected void setTitleSummary(int resId) {
        if (mHeaderSummary != null) mHeaderSummary.setText(resId);
    }

    protected void setBackgroundTintList(ColorStateList tint) {
        if (mParentLayout != null) mParentLayout.setBackgroundTintList(tint);
    }

    protected int getCardStyleResourceId() {
        return R.style.AppTheme;
    }

    protected DesignerToolsApplication getApplicationContext() {
        return (DesignerToolsApplication) getActivity().getApplicationContext();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }
}
