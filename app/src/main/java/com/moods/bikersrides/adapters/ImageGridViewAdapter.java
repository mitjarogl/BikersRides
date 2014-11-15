package com.moods.bikersrides.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moods.bikersrides.R;
import com.moods.bikersrides.database.vao.RideImage;
import com.moods.bikersrides.common.CabAdapter;
import com.moods.bikersrides.utils.ImageUtils;

import java.util.List;


public class ImageGridViewAdapter extends CabAdapter {

    private final LayoutInflater mLayoutInflater;
    private List<RideImage> mRideImages;

    public ImageGridViewAdapter(Context context, List<RideImage> rideImages) {
        super(context, R.layout.row_grid, rideImages);
        mLayoutInflater = LayoutInflater.from(context);
        mRideImages = rideImages;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.row_grid, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        RideImage rideImage = mRideImages.get(position);
        Bitmap bitmap = ImageUtils.getOptimisedBitmap(rideImage.getImgPath());
        holder.image.setImageBitmap(bitmap);
//        bitmap.recycle();
        convertView.setSelected(mSelectedItemsIds.get(position));// selection items
        //   holder.image.setSelected(mSelectedItemsIds.get(position));
        //FIXME better solution
        if (mSelectedItemsIds.get(position)) {
            holder.image.setBackgroundResource(R.color.android_blue_dark);

        } else
            holder.image.setBackgroundColor(Color.TRANSPARENT);
        return convertView;
    }

    public List<RideImage> getmRideImages() {
        return mRideImages;
    }

    @Override
    public RideImage getItem(int i) {
        return mRideImages.get(i);
    }

    static class ViewHolder {
        ImageView image;
    }

}
