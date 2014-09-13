package com.moods.bikersrides.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.moods.bikersrides.R;
import com.moods.bikersrides.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;


public class ImageGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<String> data = new ArrayList<String>();

    public ImageGridViewAdapter(Context context, int layoutResourceId,
                                List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.imageView_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        String itemPath = data.get(position);
        Bitmap bitmap = ImageUtils.getOptimisedBitmap(itemPath);
        holder.image.setImageBitmap(bitmap);
//        bitmap.recycle();
        return row;
    }
    public List<String> getData() {
        return data;
    }

    static class ViewHolder {
        ImageView image;
    }

}
