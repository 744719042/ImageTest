package com.example.imagetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void processImage(View view) {
        Intent intent = new Intent(this, ImageProcessActivity.class);
        startActivity(intent);
    }

    public void testImage(View view) {
        Intent intent = new Intent(this, ImageTestActivity.class);
        startActivity(intent);
    }
}
