package com.example.imagetest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

public class BlurImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint paint;
    private int[] colors = new int[] { 0x19979CA5, 0x19979CA5, 0xffffffff };
    private float[] positions = new float[] { 0, 0.1f, 1f };
    private int rShadowLength = 15;
    private RectF lShadowRect;
    private LinearGradient lShadowGradient;

    private int bShadowLength = 25;
    private RectF rShadowRect;
    private LinearGradient rShadowGradient;

    private int lShadowLength = 15;
    private RectF bShadowRect;
    private LinearGradient bShadowGradient;

    private RectF rbCornerRect;
    private RadialGradient rbGradient;

    private RectF lbCornerRect;
    private RadialGradient lbGradient;

    public BlurImageView(Context context) {
        this(context, null);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint = new Paint();
        lShadowRect = new RectF();
        rShadowRect = new RectF();
        bShadowRect = new RectF();
        rbCornerRect = new RectF();
        lbCornerRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lShadowRect.set(0, 0, lShadowLength, getHeight() - bShadowLength);
        lShadowGradient = new LinearGradient(lShadowLength, 0, 0, 0, colors, positions, Shader.TileMode.CLAMP);

        rShadowRect.set(getWidth() - rShadowLength, 0, getWidth(), getHeight() - rShadowLength);
        rShadowGradient = new LinearGradient(getWidth() - rShadowLength, 0, getWidth(), 0, colors, positions, Shader.TileMode.CLAMP);

        bShadowRect.set(lShadowLength, getHeight() - bShadowLength, getWidth() - rShadowLength, getHeight());
        bShadowGradient = new LinearGradient(0, getHeight() - bShadowLength, 0, getHeight(), colors, positions, Shader.TileMode.CLAMP);

        rbCornerRect.set(getWidth() - 2 * rShadowLength, getHeight() - 2 * rShadowLength, getWidth(), getHeight());
        rbGradient = new RadialGradient(getWidth() - rShadowLength, getHeight() - rShadowLength, rShadowLength, colors, positions, Shader.TileMode.CLAMP);

        lbCornerRect.set(0, getHeight() - 2 * lShadowLength, lShadowLength * 2, getHeight());
        lbGradient = new RadialGradient(lShadowLength, getHeight() - lShadowLength, lShadowLength, colors, positions, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        paint.setShader(lShadowGradient);
        canvas.drawRect(lShadowRect, paint);

        paint.setShader(rShadowGradient);
        canvas.drawRect(rShadowRect, paint);

        paint.setShader(bShadowGradient);
        canvas.drawRect(bShadowRect, paint);

        paint.setShader(rbGradient);
        canvas.drawArc(rbCornerRect, 0, 90, true, paint);

        paint.setShader(lbGradient);
        canvas.drawArc(lbCornerRect, 180, 90, true, paint);
        canvas.restore();
    }
}
