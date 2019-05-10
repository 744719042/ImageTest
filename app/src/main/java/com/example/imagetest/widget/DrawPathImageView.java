package com.example.imagetest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.imagetest.utils.CommonUtils;

/**
 * Created by Administrator on 2018/1/30.
 */
public class DrawPathImageView extends AppCompatImageView {
    private Paint paint;
    private Path path;
    private int radius = CommonUtils.dp2px(15);

    public DrawPathImageView(Context context) {
        this(context, null);
    }

    public DrawPathImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawPathImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        drawTopLeft(canvas);
        drawTopRight(canvas);
        drawBottomLeft(canvas);
        drawBottomRight(canvas);
        canvas.restoreToCount(count);
    }

    private void drawTopLeft(Canvas canvas) {
        if (radius > 0) {
            path.reset();
            path.moveTo(0, radius);
            path.lineTo(0, 0);
            path.lineTo(radius, 0);
            path.arcTo(new RectF(0, 0, radius * 2, radius * 2),
                    -90, -90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawTopRight(Canvas canvas) {
        if (radius > 0) {
            int width = getWidth();
            path.reset();
            path.moveTo(width - radius, 0);
            path.lineTo(width, 0);
            path.lineTo(width, radius);
            path.arcTo(new RectF(width - 2 * radius, 0, width,
                    radius * 2), 0, -90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawBottomLeft(Canvas canvas) {
        if (radius > 0) {
            int height = getHeight();
            path.reset();
            path.moveTo(0, height - radius);
            path.lineTo(0, height);
            path.lineTo(radius, height);
            path.arcTo(new RectF(0, height - 2 * radius,
                    radius * 2, height), 90, 90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawBottomRight(Canvas canvas) {
        if (radius > 0) {
            int height = getHeight();
            int width = getWidth();
            path.reset();
            path.moveTo(width - radius, height);
            path.lineTo(width, height);
            path.lineTo(width, height - radius);
            path.arcTo(new RectF(width - 2 * radius, height - 2
                    * radius, width, height), 0, 90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }
}
