package com.example.imagetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MemoryTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_test);
    }

    public void testMemory(View view) {
        int[][] array = new int[1024 * 1024][];
        for (int i = 0; i < 1024; i++) {
            array[i] = new int[1024 * 1024];
        }
    }
}
