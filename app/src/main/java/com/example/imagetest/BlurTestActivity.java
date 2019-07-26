package com.example.imagetest;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.imagetest.utils.CommonUtils;

public class BlurTestActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageView imageViewBlur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_test);
        imageView = findViewById(R.id.image);
        imageView.setBackgroundResource(R.drawable.common_third_shadow_bg);
        imageViewBlur = findViewById(R.id.imageBlur);
        imageViewBlur.setImageResource(R.drawable.single_line);
        imageViewBlur.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(CommonUtils.dp2px(150), CommonUtils.dp2px(150), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                imageViewBlur.draw(canvas);
                bitmap = blur(bitmap, 10);
                imageViewBlur.setImageBitmap(bitmap);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blur(Bitmap bmp, @IntRange(from = 1, to = 25) int radius) {
        RenderScript rs = RenderScript.create(this);
        Allocation allocFromBmp = Allocation.createFromBitmap(rs, bmp);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allocFromBmp.getElement());
        blur.setInput(allocFromBmp);
        blur.setRadius(radius);
        blur.forEach(allocFromBmp);
        allocFromBmp.copyTo(bmp);
        rs.destroy();

        return bmp;
    }
}
