/*
 * Copyright (C) 2016 Cyanogen, Inc.
 */
package org.cyanogenmod.designertools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import org.cyanogenmod.designertools.R;

public class GridPreview extends View {
    // default line width in dp
    private static final float DEFAULT_LINE_WIDTH = 1f;
    // default column size in dp
    private static final int DEFAULT_COLUMN_SIZE = 8;
    // default row size in dp
    private static final int DEFAULT_ROW_SIZE = 8;
    private static final int BACKGROUND_COLOR = 0x1f000000;

    private float mGridLineWidth;
    private float mColumnSize;
    private float mRowSize;
    private float mDensity;
    private int mColumnSizeDp;
    private int mRowSizeDp;

    private Paint mGridLinePaint;
    private Paint mGridSizeTextPaint;

    public GridPreview(Context context) {
        this(context, null);
    }

    public GridPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GridPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mDensity = getResources().getDisplayMetrics().density;

        mGridLineWidth = DEFAULT_LINE_WIDTH * mDensity;
        mColumnSizeDp = DEFAULT_COLUMN_SIZE;
        mColumnSize = mColumnSizeDp * mDensity;
        mRowSizeDp = DEFAULT_ROW_SIZE;
        mRowSize = mRowSizeDp * mDensity;

        mGridLinePaint = new Paint();
        mGridLinePaint.setColor(context.getColor(R.color.colorGridOverlayCardTint));
        mGridLinePaint.setStrokeWidth(mGridLineWidth);

        mGridSizeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mGridSizeTextPaint.setTextSize(
                getResources().getDimensionPixelSize(R.dimen.grid_preview_text_size));
        mGridSizeTextPaint.setColor(BACKGROUND_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();

        canvas.drawColor(BACKGROUND_COLOR);
        for (float x = mColumnSize; x < width; x += mColumnSize) {
            canvas.drawLine(x, 0, x, height, mGridLinePaint);
        }
        for (float y = mRowSize; y < height; y += mRowSize) {
            canvas.drawLine(0, y, width, y, mGridLinePaint);
        }

        String text = String.format("%d x %d", mColumnSizeDp, mRowSizeDp);
        Rect bounds = new Rect();
        mGridSizeTextPaint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (width - bounds.width()) / 2f, (height + bounds.height()) / 2f, mGridSizeTextPaint);
    }

    public void setColumnSize(int columnSize) {
        mColumnSizeDp = columnSize;
        mColumnSize = mColumnSizeDp * mDensity;
        invalidate();
    }

    public int getColumnSize() {
        return mColumnSizeDp;
    }

    public void setRowSize(int rowSize) {
        mRowSizeDp = rowSize;
        mRowSize = mRowSizeDp * mDensity;
        invalidate();
    }

    public int getRowSize() {
        return mRowSizeDp;
    }
}
