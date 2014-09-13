package com.moods.bikersrides.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.moods.bikersrides.R;
import com.moods.bikersrides.adapters.MyRidesAdapter;
import com.moods.bikersrides.database.DataBaseHelper;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.database.dao.RideDao;

import java.util.List;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class MyRidesFragment extends Fragment implements AdapterView.OnItemClickListener, TextWatcher {

    private EditText mTxtSearch;
    private StickyListHeadersListView mStickyList;
    private MyRidesAdapter mMyRidesAdapter;
    private DaoSession mDaoSession;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_rides, container, false);
        mTxtSearch = (EditText) rootView.findViewById(R.id.textView_search);
        mStickyList = (StickyListHeadersListView ) rootView.findViewById(R.id.listView_my_rides);
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(getActivity());
        mDaoSession = dbHelper.getSession();

        List<Ride> rides;
        if (isSelectedFavouriteRides()) {
            rides = mDaoSession.queryBuilder(Ride.class).where(RideDao.Properties.IsFavourite.eq(true)).list();
        } else
            rides = mDaoSession.loadAll(Ride.class);

        mMyRidesAdapter = new MyRidesAdapter(getActivity(), rides);
        mStickyList.setAdapter(mMyRidesAdapter);
        mStickyList.setOnItemClickListener(this);
        mTxtSearch.addTextChangedListener(this);
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

        Bundle args = new Bundle();
        args.putLong(RideDetailsFragment.RIDE_ID, id);
        Fragment fragment = new RideDetailsFragment();
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "RIDE_DETAILS").addToBackStack(null).commit();
    }

    @Override
    public void beforeTextChanged(CharSequence cs, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence cs, int i, int i2, int i3) {
        //  mMyRidesAdapter.getFilter().filter(cs.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = mTxtSearch.getText().toString().toLowerCase(Locale.getDefault());
        mMyRidesAdapter.filter(text);
    }

    private boolean isSelectedFavouriteRides() {
        Bundle bundle = getArguments();
        return bundle != null && bundle.containsKey("isFavourite") ? true : false;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar supportActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (isSelectedFavouriteRides())
            supportActionBar.setTitle(R.string.title_my_favourite_rides);
        else
            supportActionBar.setTitle(R.string.title_my_rides);
    }

}

