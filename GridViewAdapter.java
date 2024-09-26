package com.smartcity.cgs;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    // 這裡要再設定 gridview

    private Context context;
    private ArrayList<Bitmap> bitmaps;

    private int itemWidth;
    private int itemHeight;


    public GridViewAdapter (Context context, ArrayList<Bitmap> bitmaps , int itemWidth , int itemHeight) {

        this.context = context;
        this.bitmaps = bitmaps;
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;

    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ImageView imageView;

        if (convertView == null) {

            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(330, 200)); // 設置圖片大小
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(bitmaps.get(position));

        // 設置 Grid 每一項的寬高

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemHeight;
        imageView.setLayoutParams(layoutParams);

        return imageView;



    }


}

