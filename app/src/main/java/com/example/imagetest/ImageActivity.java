package com.example.imagetest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.imagetest.widget.ScaleImageView;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    private ScaleImageView imageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.image);
        final Rect src = getIntent().getParcelableExtra("info");
        imageView.setSrc(new RectF(src.left, src.top, src.right, src.bottom));
    }
}
