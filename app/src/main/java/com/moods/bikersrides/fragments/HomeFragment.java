package com.moods.bikersrides.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.moods.bikersrides.R;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private ImageButton mBtnAddRide;
    private Button mBtnMyRides;
    private Button mBtnFavourite;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mBtnAddRide = (ImageButton) rootView.findViewById(R.id.button_add);
        mBtnMyRides = (Button) rootView.findViewById(R.id.button_my_rides);
        mBtnFavourite = (Button) rootView.findViewById(R.id.button_favourites);

        this.mBtnAddRide.setOnClickListener(this);
        this.mBtnMyRides.setOnClickListener(this);
        this.mBtnFavourite.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View view) {
        if (view.equals(mBtnAddRide)) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, AddRideFragment.newInstance(-1), "ADD_RIDE").addToBackStack(null).commit();
        }
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
