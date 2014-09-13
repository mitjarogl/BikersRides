package com.moods.bikersrides.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.moods.bikersrides.R;
import com.moods.bikersrides.activities.MainActivity;
import com.moods.bikersrides.adapters.AutoCompleteLoading;
import com.moods.bikersrides.adapters.ImageGridViewAdapter;
import com.moods.bikersrides.adapters.PlacesAutocompleteAdapter;
import com.moods.bikersrides.async.SaveImageAsync;
import com.moods.bikersrides.common.BaseGlobals;
import com.moods.bikersrides.common.IBaseModel;
import com.moods.bikersrides.database.DataBaseHelper;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.database.vao.RideImage;
import com.moods.bikersrides.database.vao.Via;
import com.moods.bikersrides.utils.ArrayUtils;
import com.moods.bikersrides.utils.DataTypeUtils;
import com.moods.bikersrides.utils.DateTimePicker;
import com.moods.bikersrides.utils.DialogUtils;
import com.moods.bikersrides.utils.ImageUtils;
import com.moods.bikersrides.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class AddRideFragment extends Fragment implements IBaseModel, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int IMAGE_PICKER_SELECT = 1;
    private static final int VIA_DIALOG_SELECT = 2;
    private static final int NUM_OF_ALLOWED_IMAGES = 2;
    private long mCurrentId = -1;
    private AutoCompleteLoading mTxtStartPoint;
    private AutoCompleteLoading mTxtEndPoint;
    private RatingBar mRatRide;
    private EditText mTxtComment;
    private Button mBtnPickDate;
    private Button mBtnAddImage;
    private Button mBtnAddRide;
    private Button mBtnAddVia;
    private GridView mGridViewImages;
    private ImageGridViewAdapter mGridViewImagesAdapter;
    private List<String> mSelectedImagesPaths;
    private List<Bitmap> mSelectedImages;
    private DialogUtils mDialogUtils;
    private ProgressBar mProgressStart, mProgressEnd;

    private Calendar mCal = Calendar.getInstance();
    private List<String> mListVia;// = new ArrayList<String>();
    private DaoSession mDaoSession;

    public AddRideFragment() {
    }

    public static AddRideFragment newInstance(long rideId) {
        AddRideFragment fragment = new AddRideFragment();
        Bundle args = new Bundle();
        args.putLong(BaseGlobals.ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_ride, container, false);
        mBtnPickDate = (Button) rootView.findViewById(R.id.button_pick_date);
        mBtnAddImage = (Button) rootView.findViewById(R.id.button_add_photo);
        mBtnAddRide = (Button) rootView.findViewById(R.id.button_add_ride);
        mBtnAddVia = (Button) rootView.findViewById(R.id.button_via);
        mTxtStartPoint = (AutoCompleteLoading) rootView.findViewById(R.id.autoCompleteTextView_start_point);
        mTxtEndPoint = (AutoCompleteLoading) rootView.findViewById(R.id.autoCompleteTextView_end_point);
        mRatRide = (RatingBar) rootView.findViewById(R.id.rating_ride);
        mTxtComment = (EditText) rootView.findViewById(R.id.editText_comment);
        mGridViewImages = (GridView) rootView.findViewById(R.id.gridView_images);
        mProgressStart = (ProgressBar) rootView.findViewById(R.id.progress_autocomplete);
        mProgressEnd = (ProgressBar) rootView.findViewById(R.id.progress_autocomplete2);

        mBtnPickDate.setText(DataTypeUtils.date2DisplayFormat(mCal.getTime()));

        DataBaseHelper dbHelper = DataBaseHelper.getInstance(getActivity());
        mDaoSession = dbHelper.getSession();
        mSelectedImagesPaths = new ArrayList<String>();
        mSelectedImages = new ArrayList<Bitmap>();
        mDialogUtils = new DialogUtils(getActivity());

        ArrayAdapter<String> adapter = new PlacesAutocompleteAdapter(getActivity(), 0);
        mTxtStartPoint.setThreshold(BaseGlobals.THRESHOLD);
        mTxtStartPoint.setAdapter(adapter);
        mTxtStartPoint.setLoadingIndicator(mProgressStart);
        mTxtEndPoint.setThreshold(BaseGlobals.THRESHOLD);
        mTxtEndPoint.setAdapter(adapter);
        mTxtEndPoint.setLoadingIndicator(mProgressEnd);

        this.mBtnPickDate.setOnClickListener(this);
        this.mBtnAddImage.setOnClickListener(this);
        this.mBtnAddRide.setOnClickListener(this);
        this.mBtnAddVia.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (isEditRideView()) {
            showEditRideView(args.getLong(BaseGlobals.ARG_RIDE_ID));

        } else if (mCurrentId != -1) {
            showEditRideView(mCurrentId);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mBtnPickDate)) {

            DateTimePicker dateTimePicker = DateTimePicker.newInstance(DateTimePicker.DATE_DIALOG_ID);
            dateTimePicker.setmDateListener(this);
            dateTimePicker.show(getActivity().getSupportFragmentManager(), "DATE_TIME_PICKER");
        }
        if (view.equals(mBtnAddImage)) {
            if (mGridViewImages.getChildCount() >= NUM_OF_ALLOWED_IMAGES) {
                mDialogUtils.showCancelInfoDialogWithButton(getString(R.string.title_info), getString(R.string.too_many_images));
            } else {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);
            }
        }
        if (view.equals(mBtnAddRide)) {
            if (isValidInput()) {

                Long rideId = null;
                try {
                    rideId = addRide();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.i(getClass().toString(), "Inserted new ride, ID: " + rideId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (rideId != null)
                    fragmentManager.beginTransaction().replace(R.id.content_frame, RideDetailsFragment.newInstance(rideId), "RIDE_DETAILS").addToBackStack(null).commit();
            }
        }
        if (view.equals(mBtnAddVia)) {
            ViaDialog viaDialog = ViaDialog.newInstance((ArrayList<String>) mListVia);
            viaDialog.setTargetFragment(this, VIA_DIALOG_SELECT);
            viaDialog.show(getActivity().getSupportFragmentManager(), "VIA_DIALOG");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mCal.set(year, month, day);
        mBtnPickDate.setText(DataTypeUtils.date2DisplayFormat(mCal.getTime()));
    }

    /**
     * Photo Selection result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            MainActivity activity = (MainActivity) getActivity();
            Bitmap bitmap = ImageUtils.getBitmapFromCameraData(data, activity);
            mSelectedImages.add(bitmap);
            mSelectedImagesPaths.add(ImageUtils.getImagePath(data, getActivity()));
            mGridViewImagesAdapter = new ImageGridViewAdapter(getActivity(), R.layout.row_grid, mSelectedImagesPaths);
            mGridViewImages.setAdapter(mGridViewImagesAdapter);
            mGridViewImages.setVisibility(View.VISIBLE);
        }
        if (requestCode == VIA_DIALOG_SELECT && resultCode == Activity.RESULT_OK) {

            Log.i("DATE SELECTED", String.valueOf(data.getStringArrayListExtra("ViaEntries")));
            mListVia = data.getStringArrayListExtra("ViaEntries");
        }
    }

    //FIXME refactor image name
    private Long addRide() throws ExecutionException, InterruptedException {
        Ride ride = new Ride();
        ride.setStartPoint(mTxtStartPoint.getText().toString());
        ride.setEndPoint(mTxtEndPoint.getText().toString());
        ride.setRating((int) mRatRide.getRating());
        ride.setIsFavourite(false);
        ride.setComment(mTxtComment.getText().toString());
        ride.setDate(DataTypeUtils.displayDate2Date(mBtnPickDate.getText().toString()));

        if (isEditRideView()) {
            ride.setId(mCurrentId);
            mDaoSession.update(ride);
        } else {
            mDaoSession.insert(ride);
        }
        saveViaEntries(ride, mListVia);
        if (!ArrayUtils.isNullOrEmpty(mSelectedImages))
            new SaveImageAsync(getActivity(), mSelectedImages, ride, mDaoSession).execute();
        return ride.getId();

    }

    //FIXME refactor
    private void saveViaEntries(Ride ride, List<String> viaEntries) {
        Log.i(getClass().toString(), "SAVE VIA ENTRIES");
        if (!ArrayUtils.isNullOrEmpty(viaEntries)) {
            if (isEditRideView()) {
                for (Via via : ride.getVias()) {
                    mDaoSession.delete(via);
                }
            }
            for (String viaEntry : viaEntries) {
                Via via = new Via();
                via.setVia(viaEntry);
                via.setRideId(ride.getId());
                mDaoSession.insert(via);
            }

        }
    }

    @Override
    public boolean isValidInput() {
        if (StringUtils.isNullOrEmpty(mTxtStartPoint.getText().toString())) {
            mTxtStartPoint.setError(getString(R.string.enter_start_point));
            return false;
        }
        if (StringUtils.isNullOrEmpty(mTxtEndPoint.getText().toString())) {
            mTxtEndPoint.setError(getString(R.string.enter_end_point));
            return false;
        }
        return true;
    }

    private void showEditRideView(long rideId) {
        Log.i(getClass().toString(), "RIDE_ID: " + String.valueOf(rideId));
        Ride ride = mDaoSession.load(Ride.class, rideId);
        mTxtStartPoint.setText(ride.getStartPoint());
        mTxtEndPoint.setText(ride.getEndPoint());
        mRatRide.setRating(ride.getRating());
        mBtnPickDate.setText(DataTypeUtils.date2DisplayFormat(ride.getDate()));
        mTxtComment.setText(ride.getComment());
        mListVia = new ArrayList<String>();
        if (!ArrayUtils.isNullOrEmpty(ride.getVias()))
            for (Via via : ride.getVias()) {
                mListVia.add(via.getVia());
            }
        if (!ArrayUtils.isNullOrEmpty(ride.getImages())) {
            for (RideImage rideImage : ride.getImages())
                mSelectedImagesPaths.add(rideImage.getImgPath());
        }
        mCurrentId = rideId;
    }

    private boolean isEditRideView() {
        Bundle bundle = getArguments();
        return bundle != null && bundle.getLong(BaseGlobals.ARG_RIDE_ID) != -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (isEditRideView())
            actionBar.setTitle(R.string.title_edit_ride);
        else
            actionBar.setTitle(R.string.title_add_ride);
    }

}
