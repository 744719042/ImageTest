package com.example.imagetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ImageTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);
    }

    public void load(View view) {
        Intent intent = new Intent(this, ImageLoadActivity.class);
        startActivity(intent);
    }

    public void webp(View view ){
        Intent intent = new Intent(this, ImageLoadActivity.class);
        startActivity(intent);
    }

    public void getMetaInfo(View view) {
        Intent intent = new Intent(this, ImageLoadActivity.class);
        startActivity(intent);
    }

    public void view2Image(View view) {
        Intent intent = new Intent(this, View2BitmapActivity.class);
        startActivity(intent);
    }

    public void testGif(View view) {
        Intent intent = new Intent(this, GifTestActivity.class);
        startActivity(intent);
    }
}
