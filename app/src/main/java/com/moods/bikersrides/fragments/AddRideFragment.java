package com.moods.bikersrides.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.moods.bikersrides.R;
import com.moods.bikersrides.activities.FullScreenImage;
import com.moods.bikersrides.adapters.AutoCompleteLoading;
import com.moods.bikersrides.adapters.ImageGridViewAdapter;
import com.moods.bikersrides.adapters.PlacesAutocompleteAdapter;
import com.moods.bikersrides.async.SaveImageAsync;
import com.moods.bikersrides.common.BaseGlobals;
import com.moods.bikersrides.common.CabFragment;
import com.moods.bikersrides.common.FragmentCallback;
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

/**
 * Class is adding and editing rides, based on bundle the add or edit ride is showed
 */
public class AddRideFragment extends CabFragment implements IBaseModel, View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, TextWatcher {

    private static final int IMAGE_PICKER_SELECT = 1;
    private static final int VIA_DIALOG_SELECT = 2;
    private static final int NUM_OF_ALLOWED_IMAGES = 3;
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
    private List<RideImage> mSelectedRideImages;
    private DialogUtils mDialogUtils;
    private ProgressBar mProgressStart, mProgressEnd;

    private Calendar mCal = Calendar.getInstance();
    private List<String> mListVia;
    private DaoSession mDaoSession;
    ArrayAdapter<String> placesAdapter;

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
        mSelectedRideImages = new ArrayList<RideImage>();
        mDialogUtils = new DialogUtils(getActivity());

        placesAdapter = new PlacesAutocompleteAdapter(getActivity(), 0);
        mTxtStartPoint.setThreshold(BaseGlobals.THRESHOLD);
        mTxtEndPoint.setThreshold(BaseGlobals.THRESHOLD);

        mGridViewImagesAdapter = new ImageGridViewAdapter(getActivity(), mSelectedRideImages);
        mGridViewImages.setAdapter(mGridViewImagesAdapter);

        mBtnPickDate.setOnClickListener(this);
        mBtnAddImage.setOnClickListener(this);
        mBtnAddRide.setOnClickListener(this);
        mBtnAddVia.setOnClickListener(this);
        mGridViewImages.setOnItemLongClickListener(this);
        mGridViewImages.setOnItemClickListener(this);
        mTxtStartPoint.addTextChangedListener(this);
        mTxtEndPoint.addTextChangedListener(this);
        setmBaseAdapter(mGridViewImagesAdapter);

        Bundle args = getArguments();
        if (isEditRideView()) {
            showEditRideView(args.getLong(BaseGlobals.ARG_RIDE_ID));

        } else if (mCurrentId != -1) {
            showEditRideView(mCurrentId);
        }

        return rootView;
    }

//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        Bundle args = getArguments();
//        if (isEditRideView()) {
//            showEditRideView(args.getLong(BaseGlobals.ARG_RIDE_ID));
//
//        } else if (mCurrentId != -1) {
//            showEditRideView(mCurrentId);
//        }
//    }

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
                    Log.e(getClass().toString(), "Error at inserting new ride!");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.e(getClass().toString(), "Error at inserting new ride!");
                    e.printStackTrace();
                }

                Log.i(getClass().toString(), "Inserted new ride, ID: " + rideId);


            }
        }
        if (view.equals(mBtnAddVia)) {
            ViaDialog viaDialog = ViaDialog.newInstance((ArrayList<String>) mListVia);
            viaDialog.setTargetFragment(this, VIA_DIALOG_SELECT);
            viaDialog.show(getActivity().getSupportFragmentManager(), "VIA_DIALOG");
        }
    }

    private void redirectToDetails() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();//skip one fragment
        if (mCurrentId != -1)
            fragmentManager.beginTransaction().replace(R.id.content_frame, RideDetailsFragment.newInstance(mCurrentId), "RIDE_DETAILS").addToBackStack(null).commit();

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
            mSelectedRideImages.add(new RideImage(ImageUtils.getImagePath(data, getActivity())));
            mGridViewImagesAdapter.notifyDataSetChanged();
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
        //FIXME dummy solution, find better no sending through constructor!!!
        if (!ArrayUtils.isNullOrEmpty(mSelectedRideImages))
            new SaveImageAsync(getActivity(), mSelectedRideImages, ride, mDaoSession, new FragmentCallback() {
                @Override
                public void onFinished() {
                    redirectToDetails();
                }
            }).execute();
        else
            redirectToDetails();
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
            mSelectedRideImages.addAll(ride.getImages());
            mGridViewImages.setVisibility(View.VISIBLE);
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

    @Override
    public void onContextualActionBarItemClicked(SparseBooleanArray selected, int position) {
        RideImage selectedRideImage = mGridViewImagesAdapter
                .getItem(selected.keyAt(position));
        mSelectedRideImages.remove(selectedRideImage);
        mGridViewImagesAdapter.remove(selectedRideImage);
        if (mGridViewImagesAdapter.isEmpty())
            mGridViewImages.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

        if (mActionMode == null) {
            Intent fullScreenIntent = new Intent(view.getContext(), FullScreenImage.class);
            fullScreenIntent.putExtra("bitmapUri", mGridViewImagesAdapter.getmRideImages().get(pos).getImgPath());
            startActivity(fullScreenIntent);
        } else onListItemSelect(pos);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
        onListItemSelect(pos);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        //load if text changed
        if (mTxtStartPoint.getText().hashCode() == editable.hashCode()) {
            mTxtStartPoint.setAdapter(placesAdapter);
            mTxtStartPoint.setLoadingIndicator(mProgressStart);
        } else if (mTxtEndPoint.getText().hashCode() == editable.hashCode()) {
            mTxtEndPoint.setAdapter(placesAdapter);
            mTxtEndPoint.setLoadingIndicator(mProgressEnd);
        }
    }
}
