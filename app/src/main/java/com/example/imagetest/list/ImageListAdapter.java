package com.example.imagetest.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.imagetest.R;

import java.util.List;

public class ImageListAdapter extends BaseAdapter {
    private List<Bitmap> mData;
    private Context mContext;

    public ImageListAdapter(Context context, List<Bitmap> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pic, parent, false);
        ImageView imageView = convertView.findViewById(R.id.image);
        Bitmap bitmap = getItem(position);
        imageView.getLayoutParams().width = bitmap.getWidth();
        imageView.getLayoutParams().height = bitmap.getHeight();
        imageView.setImageBitmap(bitmap);
        return convertView;
    }
}
