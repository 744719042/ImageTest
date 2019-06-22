package com.example.imagetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MemoryTestActivity extends AppCompatActivity {
    private static final String TAG = "MemoryTestActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_test);

        Log.e(TAG, "Total Memory = " + Runtime.getRuntime().totalMemory());
        Log.e(TAG, "Free Memory = " + Runtime.getRuntime().freeMemory());
        Log.e(TAG, "Max Memory = " + Runtime.getRuntime().maxMemory());
    }

    public void testMemory(View view) {
        int[][] array = new int[1024 * 1024][];
        for (int i = 0; i < 1024; i++) {
            array[i] = new int[1024 * 1024];
        }
    }
}
