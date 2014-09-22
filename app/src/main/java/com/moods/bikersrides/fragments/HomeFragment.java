package com.moods.bikersrides.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.moods.bikersrides.R;
import com.moods.bikersrides.database.DataBaseHelper;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.utils.DataTypeUtils;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView mTxtCountAllRides;
    private TextView mTxtLastRide;
    //  private ImageButton mBtnAddRide;
    private Button mBtnMyRides;
    private Button mBtnFavourite;
    private DaoSession mDaoSession;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //     mBtnAddRide = (ImageButton) rootView.findViewById(R.id.button_add);
        mBtnMyRides = (Button) rootView.findViewById(R.id.button_my_rides);
        mBtnFavourite = (Button) rootView.findViewById(R.id.button_favourites);
        mTxtCountAllRides = (TextView) rootView.findViewById(R.id.textView_count_all_rides);
        mTxtLastRide = (TextView) rootView.findViewById(R.id.textView_last_ride);
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(getActivity());
        mDaoSession = dbHelper.getSession();

        long countAllRides = mDaoSession.getRideDao().count();

        Ride lastRide = mDaoSession.getRideDao().load(countAllRides);
        mTxtCountAllRides.setText(getString(R.string.you_have) + String.valueOf(countAllRides) + getString(R.string.rides));
        mTxtLastRide.setText(getString(R.string.last_ride) + DataTypeUtils.date2DisplayWithDayOfWeek(lastRide.getDate()));
        //      this.mBtnAddRide.setOnClickListener(this);
        this.mBtnMyRides.setOnClickListener(this);
        this.mBtnFavourite.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View view) {
//        if (view.equals(mBtnAddRide)) {
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, AddRideFragment.newInstance(-1), "ADD_RIDE").addToBackStack(null).commit();
//        }
        if (view.equals(mBtnMyRides)) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, MyRidesFragment.newInstance(false), "MY_RIDES").addToBackStack(null).commit();
        }
        if (view.equals(mBtnFavourite)) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, MyRidesFragment.newInstance(true), "MY_RIDES").addToBackStack(null).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActionBarActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.app_name);
    }
}
