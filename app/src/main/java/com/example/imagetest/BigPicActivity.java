package com.example.imagetest;

import android.graphics.BitmapRegionDecoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.imagetest.utils.IOUtils;
import com.example.imagetest.widget.BigPicImageView;

import java.io.InputStream;

public class BigPicActivity extends AppCompatActivity {
    private BitmapRegionDecoder regionDecoder;
    private BigPicImageView bigPicImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pic);
        bigPicImageView = findViewById(R.id.big_image_view);
        initRegionDecode();
        if (regionDecoder != null) {
            bigPicImageView.setRegionDecoder(regionDecoder);
        }
    }

    public void initRegionDecode() {
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("big.jpg"); // 首先获得大图片的输入流
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (regionDecoder != null) {
            regionDecoder.recycle();
        }
    }
}
