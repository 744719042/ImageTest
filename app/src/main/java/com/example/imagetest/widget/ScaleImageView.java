package com.example.imagetest.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

public class ScaleImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ScaleImageView";
    private RectF originRect = new RectF();
    private BitmapDrawable bitmapDrawable;
    private Matrix supportMatrix;
    private Matrix baseMatrix;
    private Matrix drawMatrix;
    private RectF finalClipRect;
    private RectF finalRect;
    private RectF clipRect;
    private RectF tmpRect;
    private boolean firstInit = true;
    private RectF src;
    private ValueAnimator valueAnimator;

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
        supportMatrix = new Matrix();
        baseMatrix = new Matrix();
        drawMatrix = new Matrix();
        originRect.left = 0;
        originRect.top = 0;
        originRect.right = bitmapDrawable.getIntrinsicWidth();
        originRect.bottom = bitmapDrawable.getIntrinsicHeight();
        finalClipRect = new RectF();
        finalRect = new RectF();
        clipRect = new RectF();
        tmpRect = new RectF();
    }

    public void setSrc(RectF src) {
        this.src = src;
        Log.e(TAG, "src = " + src);
    }

    private void animFrom(final RectF src) {
        Log.e(TAG, "src = " + src);
        Log.e(TAG, "dst = " + finalClipRect);
        float scale = src.width() * 1.0f / finalClipRect.width();
        Log.e(TAG, "scale = " + scale);
        final float  startX = src.centerX(), startY = src.centerY();
        final float endX = finalRect.centerX(), endY = finalRect.centerY();

        final float startWidth = src.width(), startHeight = src.height();
        final float endWidth = finalClipRect.width(), endHeight = finalClipRect.height();

        valueAnimator = ValueAnimator.ofFloat(scale, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                supportMatrix.setScale(value, value, src.centerX(), src.centerY());
                RectF rectF = getDrawRect();

                float fraction = animation.getAnimatedFraction();
                Log.e(TAG, "fraction = " + fraction);
                float x = (endX - startX) * fraction + startX;
                float y = (endY - startY) * fraction + startY;
                supportMatrix.postTranslate(x - rectF.centerX(), y - rectF.centerY());

                rectF = getDrawRect();
                Log.e(TAG, "rectF = " + rectF);
                float width = (endWidth - startWidth) * fraction + startWidth;
                float height = (endHeight - startHeight) * fraction + startHeight;
                float left = rectF.centerX() - width / 2, top = rectF.centerY() - height / 2;
                clipRect.set(left, top, left + width, top + height);
                Log.e(TAG, "clipRect = " + clipRect);
                invalidate();
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    private Matrix getDrawMatrix() {
        drawMatrix.reset();
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(supportMatrix);
        return drawMatrix;
    }

    private RectF getDrawRect() {
        getDrawMatrix();
        tmpRect.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        drawMatrix.mapRect(tmpRect);
        return tmpRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.clipRect(clipRect);
        canvas.setMatrix(drawMatrix);
        bitmapDrawable.draw(canvas);
    }

    @Override
    public void onGlobalLayout() {
        if (firstInit) {
            int originWidth = bitmapDrawable.getIntrinsicWidth();
            int originHeight = bitmapDrawable.getIntrinsicHeight();

            int viewWidth = getWidth();
            int viewHeight = getHeight();

            float scale = Math.max((float) viewWidth / originWidth, (float) viewHeight / originHeight);
            baseMatrix.setScale(scale, scale, viewWidth / 2, viewHeight / 2);
            baseMatrix.preTranslate((viewWidth - originWidth) / 2, (viewHeight - originHeight) / 2);
            finalClipRect.set(0, 0, getWidth(), getHeight());
            clipRect.set(0, 0, getWidth(), getHeight());
            finalRect.set(0, 0, originWidth, originHeight);
            baseMatrix.mapRect(finalRect);
            Log.e(TAG, "finalRect = " + finalRect);
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
                Point offset = new Point();
                Rect rect = new Rect();
                getGlobalVisibleRect(rect, offset);
                Log.e(TAG, "offset = " + offset);
                src.offset(-offset.x, -offset.y);
                animFrom(src);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
}
