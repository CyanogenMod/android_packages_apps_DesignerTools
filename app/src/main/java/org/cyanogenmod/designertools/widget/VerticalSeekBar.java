/*
 * Copyright (C) 2016 Cyanogen, Inc.
 */
package org.cyanogenmod.designertools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(270);
        c.translate(-getHeight(), 0);
        super.onDraw(c);

        // Work around for known bug with Marshmallow where the enabled thumb is not drawn
        if (isEnabled() && Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            drawThumb(c);
        }
    }

    void drawThumb(Canvas canvas) {
        Drawable thumb = getThumb();
        if (thumb != null) {
            Rect thumbBounds = thumb.getBounds();
            canvas.save();
            canvas.rotate(270, thumbBounds.exactCenterX(), thumbBounds.exactCenterY());
            canvas.translate(0, thumbBounds.height() / 3f);
            thumb.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}