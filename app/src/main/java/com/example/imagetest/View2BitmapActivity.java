package com.example.imagetest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class View2BitmapActivity extends AppCompatActivity {
    private LinearLayout gallery;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view2_bitmap);
        gallery = findViewById(R.id.gallery_layout);
        imageView = findViewById(R.id.image);
    }

    public void getViewBitmap(View view) {
        gallery.buildDrawingCache();
        Bitmap bitmap = gallery.getDrawingCache();
        imageView.setImageBitmap(bitmap);
    }

    public void getViewByCanvas(View view) {
        Bitmap bitmap = Bitmap.createBitmap(gallery.getWidth(), gallery.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        gallery.draw(canvas);
        imageView.setImageBitmap(bitmap);
    }
}
