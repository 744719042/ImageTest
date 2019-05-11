package com.example.imagetest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.imagetest.R;

public class AspectImageView extends android.support.v7.widget.AppCompatImageView {
    private Matrix matrix;
    public static final int NONE = 0;
    public static final int LEFT_TOP = 1;
    public static final int CENTER_TOP = 2;
    public static final int RIGHT_TOP = 3;
    public static final int LEFT_CENTER = 4;
    public static final int RIGHT_CENTER = 5;
    public static final int LEFT_BOTTOM = 6;
    public static final int CENTER_BOTTOM = 7;
    public static final int RIGHT_BOTTOM= 8;
    private int aspectAlign = NONE; // none

    public AspectImageView(Context context) {
        this(context, null);
    }

    public AspectImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AspectImageView);
            aspectAlign = array.getInt(R.styleable.AspectImageView_alignType, 0);
            array.recycle();
        }
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        initAspectMatrix(r - l, b - t);
        return super.setFrame(l, t, r, b);
    }

    private void initAspectMatrix(int vWidth, int vHeight) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();

        float scale = 1.0f;
        if (dWidth > vWidth && dHeight > vHeight) {
            float scaleX = vWidth * 1.0f / dWidth;
            float scaleY = vHeight * 1.0f / dHeight;
            scale = Math.max(scaleX, scaleY);
        } else if (dWidth > vWidth && dHeight < vHeight) {
            scale = vHeight * 1.0f / dHeight;
        } else if (dWidth < vWidth && dHeight > vHeight) {
            scale = vWidth * 1.0f / dWidth;
        } else { // 图片宽高都比ImageView宽高小
            float scaleX = vWidth * 1.0f / dWidth;
            float scaleY = vHeight * 1.0f / dHeight;
            scale = Math.max(scaleX, scaleY);
        }

        matrix.setScale(scale, scale);
        int sWidth = (int) (scale * dWidth);
        int sHeight = (int) (scale * dHeight);
        int dx = 0, dy = 0;
        switch (aspectAlign) {
            case LEFT_TOP:
                dx = 0;
                dy = 0;
                break;
            case CENTER_TOP:
                dx = -(sWidth - vWidth) / 2;
                dy = 0;
                break;
            case RIGHT_TOP:
                dx = -(sWidth - vWidth);
                dy = 0;
                break;



            case LEFT_CENTER:
                dx = 0;
                dy = -(sHeight - vHeight) / 2;
                break;
            case RIGHT_CENTER:
                dx = -(sWidth - vWidth);
                dy = -(sHeight - vHeight) / 2;
                break;


            case LEFT_BOTTOM:
                dx = 0;
                dy = -(sHeight - vHeight);
                break;
            case CENTER_BOTTOM:
                dx = -(sWidth - vWidth) / 2;
                dy = -(sHeight - vHeight);
                break;
            case RIGHT_BOTTOM:
                dx = -(sWidth - vWidth);
                dy = -(sHeight - vHeight);
                break;
        }
        matrix.postTranslate(dx, dy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null || aspectAlign == 0) {
            super.onDraw(canvas);
        } else {
            canvas.save();
            Drawable drawable = getDrawable();
            canvas.concat(matrix);
            drawable.draw(canvas);
            canvas.restore();
        }
    }
}
