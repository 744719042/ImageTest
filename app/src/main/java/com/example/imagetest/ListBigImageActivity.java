package com.example.imagetest;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.imagetest.list.ImageListAdapter;
import com.example.imagetest.utils.CommonUtils;
import com.example.imagetest.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListBigImageActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_big_image);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(new ImageListAdapter(this, getData()));
    }

    public List<Bitmap> getData() {
        List<Bitmap> bitmapList = new ArrayList<>();
        int height = CommonUtils.dp2px(100);
        BitmapRegionDecoder regionDecoder = null;
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("ad.jpg"); // 首先获得大图片的输入流
            regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
            int originHeight = regionDecoder.getHeight(); // 获得大图片的高度
            int count = originHeight % height == 0 ? originHeight / height : originHeight / height + 1; // 获得剪切后的小图片数量
            Rect rect = new Rect();
            rect.left = 0;
            rect.right = regionDecoder.getWidth();
            int lastHeight = height;
            for (int i = 0; i < count; i++) { // 遍历生成小图片
                rect.top = i * height; //确定小图片的top位置
                lastHeight = (i + 1) * height;
                rect.bottom = lastHeight > originHeight ? originHeight : lastHeight; // 确定小图片的底部位置
                Bitmap bitmap = regionDecoder.decodeRegion(rect, null);
                bitmapList.add(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
            if (regionDecoder != null) {
                regionDecoder.recycle();
            }
        }
        return bitmapList;
    }
}
