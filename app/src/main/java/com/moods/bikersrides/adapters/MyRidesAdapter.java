package com.moods.bikersrides.adapters;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moods.bikersrides.R;
import com.moods.bikersrides.database.DataBaseHelper;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.fragments.AddRideFragment;
import com.moods.bikersrides.utils.DataTypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyRidesAdapter extends ArrayAdapter<Ride> implements StickyListHeadersAdapter {

    private final LayoutInflater mLayoutInflater;
    private List<Ride> mRides;
    private FragmentManager mFragmentManager;
    private ArrayList<Ride> mFilteredRides;
    private DaoSession mDaoSession;

    public MyRidesAdapter(Context context, FragmentManager fragmentManager, List<Ride> rides) {
        super(context, R.layout.my_rides_item, rides);

        mLayoutInflater = LayoutInflater.from(context);
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
        mDaoSession = dbHelper.getSession();
        mRides = rides;
        mFragmentManager = fragmentManager;
        this.mFilteredRides = new ArrayList<Ride>();
        this.mFilteredRides.addAll(mRides);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Ride ride = mRides.get(position);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.my_rides_item, parent, false);
            holder = new ViewHolder();

            holder.startPoint = (TextView) convertView.findViewById(R.id.textView_start_point);
            holder.endPoint = (TextView) convertView.findViewById(R.id.textView_end_point);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.button_delete_ride);
            holder.editButton = (ImageButton) convertView.findViewById(R.id.button_edit_ride);
            holder.favouriteButton = (CheckBox) convertView.findViewById(R.id.checkBox_favouriteRide);
            holder.editButton.setFocusable(false);
            holder.deleteButton.setFocusable(false);
            holder.favouriteButton.setFocusable(false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.startPoint.setText(ride.getStartPoint());
        holder.endPoint.setText(ride.getEndPoint());
        holder.favouriteButton.setChecked(ride.getIsFavourite());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                v.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        mRides.remove(ride);
//                        notifyDataSetChanged();
//                        v.setAlpha(1);
//                    }
//                });
                Log.d("VALUE", String.valueOf(getItemId(position)));
                mRides.remove(ride);
                mDaoSession.delete(ride);
                notifyDataSetChanged();
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentManager.beginTransaction().replace(R.id.content_frame, AddRideFragment.newInstance(getItemId(position)), "EDIT_RIDE").addToBackStack(null).commit();
            }
        });
        holder.favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    ride.setIsFavourite(true);

                } else {
                    ride.setIsFavourite(false);
                }
                mDaoSession.update(ride);
            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        final HeaderViewHolder holder;
        final Ride ride = mRides.get(position);
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.my_rides_item_header, parent, false);
            holder.dateText = (TextView) convertView.findViewById(R.id.textView_my_rides_date);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.dateText.setText(DataTypeUtils.date2DisplayFormat(ride.getDate()));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mRides.get(position).getId();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mRides.clear();
        if (charText.length() == 0) {
            mRides.addAll(mFilteredRides);
        } else {
            for (Ride ride : mFilteredRides) {
                if (ride.getStartPoint().toLowerCase(Locale.getDefault())
                        .contains(charText) || ride.getEndPoint().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mRides.add(ride);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView startPoint;
        TextView endPoint;
        ImageButton deleteButton;
        ImageButton editButton;
        CheckBox favouriteButton;
    }

    class HeaderViewHolder {
        TextView dateText;
    }

}
