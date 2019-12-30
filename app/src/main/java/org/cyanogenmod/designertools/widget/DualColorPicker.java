/*
 * Copyright (C) 2016 Cyanogen, Inc.
 */
package org.cyanogenmod.designertools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.PreferenceUtils.GridPreferences;

public class DualColorPicker extends View {
    private static final float STROKE_WIDTH = 5f;
    private static final float COLOR_DARKEN_FACTOR = 0.8f;

    private Paint mPrimaryFillPaint;
    private Paint mSecondaryFillPaint;
    private Paint mPrimaryStrokePaint;
    private Paint mSecondaryStrokePaint;

    public DualColorPicker(Context context) {
        this(context, null);
    }

    public DualColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DualColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DualColorPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DualColorPicker, 0, 0);
        int primaryColor = ta.getColor(R.styleable.DualColorPicker_primaryColor,
                GridPreferences.getGridLineColor(context, getResources()
                .getColor(R.color.dualColorPickerDefaultPrimaryColor)));
        int secondaryColor = ta.getColor(R.styleable.DualColorPicker_primaryColor,
                GridPreferences.getKeylineColor(context, getResources()
                .getColor(R.color.dualColorPickerDefaultSecondaryColor)));

        mPrimaryFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPrimaryFillPaint.setStyle(Paint.Style.FILL);
        mPrimaryFillPaint.setColor(primaryColor);
        mPrimaryStrokePaint = new Paint(mPrimaryFillPaint);
        mPrimaryStrokePaint.setStyle(Paint.Style.STROKE);
        mPrimaryStrokePaint.setStrokeWidth(STROKE_WIDTH);
        mPrimaryStrokePaint.setColor(getDarkenedColor(primaryColor));

        mSecondaryFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSecondaryFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mSecondaryFillPaint.setColor(secondaryColor);
        mSecondaryStrokePaint = new Paint(mSecondaryFillPaint);
        mSecondaryStrokePaint.setStyle(Paint.Style.STROKE);
        mSecondaryStrokePaint.setStrokeWidth(STROKE_WIDTH);
        mSecondaryStrokePaint.setColor(getDarkenedColor(secondaryColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float width = getWidth();
        final float height = getHeight();
        final float widthDiv2 = width / 2f;
        final float heightDiv2 = height / 2f;
        final float radius = Math.min(widthDiv2, heightDiv2) * 0.9f;

        // erase everything
        canvas.drawColor(0);

        // draw the left half
        canvas.save();
        canvas.clipRect(0, 0, widthDiv2, height);
        canvas.drawCircle(widthDiv2, heightDiv2, radius, mPrimaryFillPaint);
        canvas.drawCircle(widthDiv2, heightDiv2, radius, mPrimaryStrokePaint);
        canvas.drawLine(widthDiv2 - STROKE_WIDTH / 2f, heightDiv2 - radius,
                widthDiv2 - STROKE_WIDTH / 2f, heightDiv2 + radius, mPrimaryStrokePaint);
        canvas.restore();

        /// draw the right half
        canvas.save();
        canvas.clipRect(widthDiv2, 0, width, height);
        canvas.drawCircle(widthDiv2, heightDiv2, radius, mSecondaryFillPaint);
        canvas.drawCircle(widthDiv2, heightDiv2, radius, mSecondaryStrokePaint);
        canvas.drawLine(widthDiv2 + STROKE_WIDTH / 2f, heightDiv2 - radius,
                widthDiv2 + STROKE_WIDTH / 2f, heightDiv2 + radius, mSecondaryStrokePaint);
        canvas.restore();
    }

    private int getDarkenedColor(int color) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * COLOR_DARKEN_FACTOR);
        int g = (int) (Color.green(color) * COLOR_DARKEN_FACTOR);
        int b = (int) (Color.blue(color) * COLOR_DARKEN_FACTOR);

        return Color.argb(a, r, g, b);
    }

    public void setPrimaryColor(int color) {
        mPrimaryFillPaint.setColor(color);
        mPrimaryStrokePaint.setColor(getDarkenedColor(color));
        invalidate();
    }

    public int getPrimaryColor() {
        return mPrimaryFillPaint.getColor();
    }

    public void setSecondaryColor(int color) {
        mSecondaryFillPaint.setColor(color);
        mSecondaryStrokePaint.setColor(getDarkenedColor(color));
        invalidate();
    }

    public int getSecondaryColor() {
        return mSecondaryFillPaint.getColor();
    }
}
