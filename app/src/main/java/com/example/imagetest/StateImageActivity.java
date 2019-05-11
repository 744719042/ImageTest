package com.example.imagetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.imagetest.widget.LikeImageView;

public class StateImageActivity extends AppCompatActivity {
    private LikeImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_image);
        imageView = findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setLiked(!imageView.isLiked());
            }
        });
    }
}
