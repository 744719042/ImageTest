package com.example.imagetest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
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
public class XFermodeImageView extends AppCompatImageView {
    private Paint paint;
    private Bitmap bitmap;
    private RectF mTmpRect;

    public XFermodeImageView(Context context) {
        this(context, null);
    }

    public XFermodeImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XFermodeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        mTmpRect = new RectF();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }

    @Override
    public void setImageResource(int resId) {
        bitmap = BitmapFactory.decodeResource(getResources(), resId);
        setImageBitmap(bitmap);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (bmp != null) {
                bitmap = bmp;
                setImageBitmap(bmp);
            }
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null && bm != bitmap) {
            bitmap = bm;
            bmpSrc = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            srcCanvas = new Canvas(bmpSrc);
            invalidate();
        }
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        throw new UnsupportedOperationException("Unsupport setImageURI");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (bitmap == null) {
            setMeasuredDimension(0, 0);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthSpec = widthMeasureSpec;
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSpec = MeasureSpec.makeMeasureSpec(bitmap.getWidth(), MeasureSpec.EXACTLY);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthSpec = MeasureSpec.makeMeasureSpec(Math.min(bitmap.getWidth(), width), MeasureSpec.EXACTLY);
        }

        int heightSpec = heightMeasureSpec;
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSpec = MeasureSpec.makeMeasureSpec(bitmap.getHeight(), MeasureSpec.EXACTLY);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSpec = MeasureSpec.makeMeasureSpec(Math.min(bitmap.getHeight(), height), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthSpec, heightSpec);
    }

    private Bitmap bmpSrc;
    private Canvas srcCanvas;
    private PorterDuffXfermode xfermode;

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) return;

        mTmpRect.set(0, 0, getWidth(), getHeight());
        int count = canvas.saveLayer(mTmpRect, paint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(bitmap, null, mTmpRect, paint);
        paint.setXfermode(null);
        mTmpRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        srcCanvas.drawRoundRect(mTmpRect, CommonUtils.dp2px(15), CommonUtils.dp2px(15), paint);
        paint.setXfermode(xfermode);
        mTmpRect.set(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(bmpSrc, null, mTmpRect, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(count);
    }

    private Bitmap roundCorner(Bitmap bmpDst) {
        mTmpRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Bitmap bmpSrc = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bmpResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setXfermode(null);
        Canvas srcCanvas = new Canvas(bmpSrc);
        srcCanvas.drawRoundRect(mTmpRect, CommonUtils.dp2px(15), CommonUtils.dp2px(15), paint);

        Canvas resultCanvas = new Canvas(bmpResult);
        resultCanvas.drawBitmap(bmpDst, null, mTmpRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        resultCanvas.drawBitmap(bmpSrc, null, mTmpRect, paint);
        return bmpResult;
    }
}
