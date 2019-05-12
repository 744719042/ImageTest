package com.example.imagetest.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

public class ScaleImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ScaleImageView";
    private Rect originRect = new Rect();
    private BitmapDrawable bitmapDrawable;
    private Matrix matrix;
    private boolean firstInit = true;
    private Rect src;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmapDrawable = (BitmapDrawable) getDrawable();
        matrix = new Matrix();
        originRect.left = 0;
        originRect.top = 0;
        originRect.right = bitmapDrawable.getIntrinsicWidth();
        originRect.bottom = bitmapDrawable.getIntrinsicHeight();
    }

    public void setSrc(Rect src) {
        this.src = src;
    }

    private void animFrom(final Rect src, final Rect dst) {
        Log.e(TAG, "src = " + src);
        Log.e(TAG, "dst = " + dst);
        float scaleX = src.width() * 1.0f / dst.width();
        float scaleY = src.height() * 1.0f / dst.height();
        Log.e(TAG, "scaleX = " + scaleX + ", scaleY = " + scaleY);
        float scale = Math.max(scaleX, scaleY);
        Log.e(TAG, "scale = " + scale);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(scale, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                matrix.setScale(value, value, src.centerX(), src.centerY());
//                setImageMatrix(matrix);
                invalidate();
            }
        });
        valueAnimator.setDuration(5000);
        valueAnimator.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.concat(matrix);
        bitmapDrawable.draw(canvas);
    }

    @Override
    public void onGlobalLayout() {
        if (firstInit) {
            int originWidth = bitmapDrawable.getIntrinsicWidth();
            int originHeight = bitmapDrawable.getIntrinsicHeight();

            int viewWidth = getWidth();
            int viewHeight = getHeight();

            float scale = 0;
            if (originWidth > viewWidth && originHeight > viewHeight ||
                    originWidth < viewWidth && originHeight < viewHeight) {
                scale = Math.min((float) viewWidth / originWidth, (float) viewHeight / originHeight);
            } else if (originWidth > viewWidth) {
                scale = viewWidth * 1.0f / originWidth;
            } else {
                scale = viewHeight * 1.0f / originHeight;
            }

            matrix.setScale(scale, scale, viewWidth / 2, viewHeight / 2);
            matrix.preTranslate((viewWidth - originWidth) / 2, (viewHeight - originHeight) / 2);
            setImageMatrix(matrix);
            firstInit = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Rect dst = new Rect();
                getGlobalVisibleRect(dst);
                animFrom(src, dst);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
}
