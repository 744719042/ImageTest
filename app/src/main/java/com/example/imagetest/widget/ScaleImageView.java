package com.example.imagetest.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
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
    private RectF srcRect;
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
        setScaleType(ScaleType.MATRIX);
    }

    public void setSrc(RectF srcRect) {
        this.srcRect = srcRect;
        Log.e(TAG, "src = " + srcRect);
    }

    private void animFrom(final RectF srcRect) {
        Log.e(TAG, "src = " + srcRect);
        Log.e(TAG, "dst = " + finalRect);
        float scale = srcRect.width() * 1.0f / finalRect.width();
        Log.e(TAG, "scale = " + scale);
        final float  startX = srcRect.centerX(), startY = srcRect.centerY();
        final float endX = finalRect.centerX(), endY = finalRect.centerY();

        final float startWidth = srcRect.width(), startHeight = srcRect.height();
        final float endWidth = finalRect.width(), endHeight = finalRect.height();

        valueAnimator = ValueAnimator.ofFloat(scale, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                supportMatrix.setScale(value, value, srcRect.centerX(), srcRect.centerY());
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

    public void playExit(final Activity activity) {
        final RectF startRect = finalRect;
        final RectF dstRect = srcRect;
        Log.e(TAG, "src = " + startRect);
        Log.e(TAG, "dst = " + dstRect);
        float scale = startRect.width() * 1.0f / dstRect.width();
        Log.e(TAG, "scale = " + scale);
        final float  startX = startRect.centerX(), startY = startRect.centerY();
        final float endX = dstRect.centerX(), endY = dstRect.centerY();

        final float startWidth = startRect.width(), startHeight = startRect.height();
        final float endWidth = dstRect.width(), endHeight = dstRect.height();

        valueAnimator = ValueAnimator.ofFloat(scale, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                supportMatrix.setScale(value, value, startRect.centerX(), startRect.centerY());
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
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activity.overridePendingTransition(0, 0);
                activity.finish();
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
                srcRect.offset(-offset.x, -offset.y);
                animFrom(srcRect);
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
