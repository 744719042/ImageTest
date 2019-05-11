package com.example.imagetest;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    private ImageView imageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.image);
        final Rect src = getIntent().getParcelableExtra("info");
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Rect dst = new Rect();
                imageView.getGlobalVisibleRect(dst);
                Log.e(TAG, "src = " + src);
                Log.e(TAG, "dst = " + dst);
                float scaleX = src.width() * 1.0f / dst.width();
                float scaleY = src.height() * 1.0f / dst.height();
                Log.e(TAG, "scaleX = " + scaleX + ", scaleY = " + scaleY);
                float scale = Math.max(scaleX, scaleY);
                Log.e(TAG, "scale = " + scale);
                final Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageMatrix(matrix);
                    }
                });
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }
}
