package com.example.imagetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ImageProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
    }

    public void clipPath(View view) {
        Intent intent = new Intent(this, RoundCornerActivity.class);
        intent.putExtra("type", "clipPath");
        startActivity(intent);
    }

    public void drawRoundRect(View view) {
        Intent intent = new Intent(this, RoundCornerActivity.class);
        intent.putExtra("type", "roundCorner");
        startActivity(intent);
    }

    public void xfermode(View view) {
        Intent intent = new Intent(this, RoundCornerActivity.class);
        intent.putExtra("type", "xfermode");
        startActivity(intent);
    }

    public void drawPath(View View) {
        Intent intent = new Intent(this, RoundCornerActivity.class);
        intent.putExtra("type", "drawPath");
        startActivity(intent);
    }

    public void bigPicListView(View View) {
        Intent intent = new Intent(this, ListBigImageActivity.class);
        startActivity(intent);
    }

    public void bigPicRegion(View View) {
        Intent intent = new Intent(this, BigPicActivity.class);
        startActivity(intent);
    }

    public void customAlign(View view) {

    }

    public void fullScreenAnimation(View View) {

    }
}
