package com.example.imagetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.imagetest.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoadActivity extends AppCompatActivity {
    private static final String TAG = "ImageLoadActivity";
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_load);
        imageView = findViewById(R.id.image);
    }

    public void loadHDPI(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hdpi_flower);
        imageView.setImageBitmap(bitmap);
        Log.e(TAG, "hdpi bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
    }

    public void loadXXHDPI(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xxhdpi_flower);
        imageView.setImageBitmap(bitmap);
        Log.e(TAG, "xxhdpi bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
    }

    public void loadBigDrawable(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snow_scienory);
        imageView.setImageBitmap(bitmap);
        Log.e(TAG, "drawable bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
    }

    public void loadDrawable(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
        imageView.setImageBitmap(bitmap);
        Log.e(TAG, "drawable bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
    }

    public void loadAsset(View view) {
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("flower.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            Log.e(TAG, "asset bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }
    }

    public void loadFromFile(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "flower.png");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            imageView.setImageBitmap(bitmap);
            Log.e(TAG, "local file bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
        }
    }

    public void showScreenInfo(View view) {
        DisplayMetrics displayMetrics = getApplication().getResources().getDisplayMetrics();
        Log.e(TAG, "width = " + displayMetrics.widthPixels + ", height = " + displayMetrics.heightPixels);
        Log.e(TAG, "density = " + displayMetrics.density + ", densityDpi = " + displayMetrics.densityDpi);
    }

    public void loadSmallDrawable(View view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.snow_scienory, options);
        Log.e(TAG, "bitmap width = " + options.outWidth + ", height = " + options.outHeight);

        int vwidth = imageView.getWidth(), vheight = imageView.getHeight();
        int dwidth = options.outWidth, dheight = options.outHeight;
        int sampleSize = calcSampleSize(vwidth, vheight, dwidth, dheight);
        Log.e(TAG, "sampleSize = " + sampleSize);
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snow_scienory, options);
        imageView.setImageBitmap(bitmap);
        Log.e(TAG, "drawable bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight() + ", size = " + bitmap.getByteCount());
    }

    private int calcSampleSize(int vwidth, int vheight, int dwidth, int dheight) {
        Log.e(TAG, "vwidth = " + vwidth + ", vheight = " + vheight);
        Log.e(TAG, "dwidth = " + dwidth + ", dheight = " + dheight);
        int sampleSize = 1;
        if (dheight > vheight || dwidth > vwidth) {
            int halfHeight = dheight / 2;
            int halfWidth = dwidth / 2;
            while ((halfHeight / sampleSize) >= vheight && (halfWidth / sampleSize) >= vwidth) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }
}
