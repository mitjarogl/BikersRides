package com.moods.bikersrides.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moods.bikersrides.R;
import com.moods.bikersrides.activities.FullScreenImage;
import com.moods.bikersrides.adapters.ImageGridViewAdapter;
import com.moods.bikersrides.common.BaseGlobals;
import com.moods.bikersrides.database.DataBaseHelper;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.database.vao.Via;
import com.moods.bikersrides.maps.MapsActivity;
import com.moods.bikersrides.utils.ArrayUtils;
import com.moods.bikersrides.utils.DataTypeUtils;
import com.moods.bikersrides.utils.StringUtils;

import java.util.ArrayList;


public class RideDetailsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private long mCurrentId = -1;
    private TextView mTxtStartPoint;
    private TextView mTxtEndPoint;
    private TextView mTxtVia;
    private RatingBar mRatRide;
    private CheckBox mChkFavouriteRide;
    private TextView mTxtComment;
    private TextView mTxtDate;
    private Button mBtnAltInfo;
    private Button mBtnMap;
    private GridView mGridViewImages;
    private ImageGridViewAdapter mGridViewImagesAdapter;
    private DaoSession mDaoSession;
    private ArrayList<String> mAllPlaces = new ArrayList<String>();

    public RideDetailsFragment() {
    }

    public static RideDetailsFragment newInstance(long rideId) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(BaseGlobals.ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_details, container, false);

        mTxtStartPoint = (TextView) rootView.findViewById(R.id.textView_start_point);
        mTxtEndPoint = (TextView) rootView.findViewById(R.id.textView_end_point);
        mTxtVia = (TextView) rootView.findViewById(R.id.textView_via);
        mRatRide = (RatingBar) rootView.findViewById(R.id.rating_ride);
        mChkFavouriteRide = (CheckBox) rootView.findViewById(R.id.checkBox_favouriteRide);
        mTxtComment = (TextView) rootView.findViewById(R.id.textView_comment);
        mTxtDate = (TextView) rootView.findViewById(R.id.textView_my_rides_date);
        mBtnAltInfo = (Button) rootView.findViewById(R.id.button_alt_info);
        mBtnMap = (Button) rootView.findViewById(R.id.button_map);
        mGridViewImages = (GridView) rootView.findViewById(R.id.gridView_images);

        DataBaseHelper dbHelper = DataBaseHelper.getInstance(getActivity());
        mDaoSession = dbHelper.getSession();

        if (savedInstanceState != null) {
            mCurrentId = savedInstanceState.getLong(BaseGlobals.ARG_RIDE_ID);
            showRideDetailsView(mCurrentId);
        }
        Log.d(getClass().toString(),"first");
        //LISTENERS
        mGridViewImages.setOnItemClickListener(this);
        mBtnMap.setOnClickListener(this);
        mChkFavouriteRide.setOnCheckedChangeListener(this);

        return rootView;
    }


    //FIXME refactor loop of images & vias
    private void showRideDetailsView(long rideId) {
        Log.i(getClass().toString(), "RIDE_ID: " + String.valueOf(rideId));
        Ride ride = mDaoSession.load(Ride.class, rideId);
        mTxtStartPoint.setText(ride.getStartPoint());
        mTxtEndPoint.setText(ride.getEndPoint());
        mTxtDate.setText(DataTypeUtils.date2DisplayFormat(ride.getDate()));
        mTxtComment.setText(ride.getComment());
        mRatRide.setRating(Float.valueOf(ride.getRating()));
        mChkFavouriteRide.setChecked(ride.getIsFavourite());
        if (!ArrayUtils.isNullOrEmpty(ride.getImages())) {
//            List<String> imagePaths = new ArrayList<String>();
//            for (RideImage image : ride.getImages())
//                imagePaths.add(image.getImgPath());
            mGridViewImagesAdapter = new ImageGridViewAdapter(getActivity(), ride.getImages());
            mGridViewImages.setAdapter(mGridViewImagesAdapter);
        }

        if (!ArrayUtils.isNullOrEmpty(ride.getVias())) {
            mTxtVia.setText(StringUtils.join(ride.getVias(), "\n"));

        }
//        temp solution
        mAllPlaces.add(ride.getStartPoint());
        mAllPlaces.add(ride.getEndPoint());
        for (Via via : ride.getVias()) {
            mAllPlaces.add(via.getVia());
        }
//        --------------

        mCurrentId = rideId;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(getClass().toString(),"second");
        Bundle args = getArguments();
        if (args != null) {
            showRideDetailsView(args.getLong(BaseGlobals.ARG_RIDE_ID));
        } else if (mCurrentId != -1) {
            showRideDetailsView(mCurrentId);
        }
    }


//    @Override
//    public void onAttach(Activity activity) {
//        mContext = (FragmentActivity) activity;
//        super.onAttach(activity);
//    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BaseGlobals.ARG_RIDE_ID, mCurrentId);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent fullScreenIntent = new Intent(view.getContext(), FullScreenImage.class);
        fullScreenIntent.putExtra("bitmapUri", mGridViewImagesAdapter.getmRideImages().get(position).getImgPath());
        startActivity(fullScreenIntent);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mBtnMap)) {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("VIA", mAllPlaces);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
        if (view.equals(mBtnAltInfo)) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActionBarActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.title_ride_details);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (mCurrentId != -1) {
            Ride ride = mDaoSession.load(Ride.class, mCurrentId);

            if (isChecked) {
                ride.setIsFavourite(true);

            } else {
                ride.setIsFavourite(false);
            }
            mDaoSession.update(ride);
        }
    }
}
