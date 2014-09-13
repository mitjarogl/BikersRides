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
            Fragment fragment = new AddRideFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "ADD_RIDE").addToBackStack(null).commit();
        }
        if (view.equals(mBtnMyRides)) {
            Fragment fragment = new MyRidesFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "MY_RIDES").addToBackStack(null).commit();
        }
        if (view.equals(mBtnFavourite)) {

            Fragment fragment = new MyRidesFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFavourite", true);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "MY_RIDES").addToBackStack(null).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActionBarActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.app_name);
    }
}
