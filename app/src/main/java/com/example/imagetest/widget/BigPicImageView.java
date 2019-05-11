package com.example.imagetest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class BigPicImageView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "BigPicImageView";
    private BitmapRegionDecoder regionDecoder;
    private int left = 0;
    private int top = 0;
    private Rect tmpRect;
    private Rect viewRect;
    private Bitmap bitmap;
    private Paint paint;
    private BitmapFactory.Options options;
    public BigPicImageView(Context context) {
        this(context, null);
    }

    public BigPicImageView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigPicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        tmpRect = new Rect();
        viewRect = new Rect();
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
    }

    public void setRegionDecoder(BitmapRegionDecoder regionDecoder) {
        this.regionDecoder = regionDecoder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            options = new BitmapFactory.Options();
            options.inBitmap = bitmap;
        }
        viewRect.set(0, 0, getWidth(), getHeight());
        tmpRect.set(left, top, left + getWidth(), top + getHeight());
        Bitmap bitmap = regionDecoder.decodeRegion(tmpRect, options);
        Log.e(TAG, "bitmap = " + bitmap.hashCode());
        canvas.drawBitmap(bitmap, null, viewRect, paint);
    }

    private int lastX;
    private int lastY;
    private int touchSlop;
    private boolean isDragging = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (regionDecoder == null) {
            return super.onTouchEvent(event);
        }

        int x = (int) event.getX(), y = (int) event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDragging && isDragging(x, y)) {
                    isDragging = true;
                }

                if (isDragging) {
                    int dx = x - lastX;
                    int dy = y - lastY;
                    int newTop = top - dy;
                    if (newTop < 0) {
                        top = 0;
                    } else if (newTop > regionDecoder.getHeight() - getHeight()) {
                        top = regionDecoder.getHeight() - getHeight();
                    } else {
                        top = newTop;
                    }

                    int newLeft = left - dx;
                    if (newLeft < 0) {
                        left = 0;
                    } else if (newLeft > regionDecoder.getWidth() - getWidth()) {
                        left = regionDecoder.getWidth() - getWidth();
                    } else {
                        left = newLeft;
                    }
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }
        lastX = x;
        lastY = y;
        return true;
    }

    private boolean isDragging(int x, int y) {
        int dxdx = (x - lastX) * (x - lastX);
        int dydy = (y - lastY) * (y - lastY);
        return (dxdx + dydy) > (touchSlop * touchSlop);
    }
}
