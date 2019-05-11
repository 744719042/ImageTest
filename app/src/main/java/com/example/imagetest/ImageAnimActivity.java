package com.example.imagetest;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ImageAnimActivity extends AppCompatActivity {
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_anim);
        image1 = findViewById(R.id.image1);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect rect = new Rect();
                image1.getGlobalVisibleRect(rect);
                Intent intent = new Intent(ImageAnimActivity.this, ImageActivity.class);
                intent.putExtra("info", rect);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        image2 = findViewById(R.id.image2);
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect rect = new Rect();
                image2.getGlobalVisibleRect(rect);
                Intent intent = new Intent(ImageAnimActivity.this, ImageActivity.class);
                intent.putExtra("info", rect);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        image3 = findViewById(R.id.image3);
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect rect = new Rect();
                image3.getGlobalVisibleRect(rect);
                Intent intent = new Intent(ImageAnimActivity.this, ImageActivity.class);
                intent.putExtra("info", rect);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
