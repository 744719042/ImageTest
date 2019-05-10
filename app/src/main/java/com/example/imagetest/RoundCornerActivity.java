package com.example.imagetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.imagetest.widget.ClipPathImageView;
import com.example.imagetest.widget.DrawPathImageView;
import com.example.imagetest.widget.RoundCornerImageView;
import com.example.imagetest.widget.XFermodeImageView;

public class RoundCornerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_corner);
        FrameLayout frameLayout = findViewById(R.id.root_view);
        String type = getIntent().getStringExtra("type");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
        ImageView imageView = null;
        switch (type) {
            case "clipPath":
                imageView = new ClipPathImageView(this);
                break;
            case "roundCorner":
                imageView = new RoundCornerImageView(this);
                break;
            case "drawPath":
                imageView = new DrawPathImageView(this);
                break;
            case "xfermode":
                imageView = new XFermodeImageView(this);
                break;
        }

        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
            frameLayout.addView(imageView, new FrameLayout.LayoutParams(400, 400, Gravity.CENTER));
        }
    }
}
