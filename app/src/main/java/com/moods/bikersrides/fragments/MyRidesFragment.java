package com.moods.bikersrides.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.moods.bikersrides.R;
import com.moods.bikersrides.adapters.MyRidesAdapter;
import com.moods.bikersrides.common.BaseGlobals;
import com.moods.bikersrides.database.DataBaseHelper;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.dao.RideDao;
import com.moods.bikersrides.database.vao.Ride;

import java.util.List;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class MyRidesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TextWatcher {

    private EditText mTxtSearch;
    private StickyListHeadersListView mStickyList;
    private MyRidesAdapter mMyRidesAdapter;
    private DaoSession mDaoSession;
    private ActionMode mActionMode;

    public MyRidesFragment() {
    }

    public static MyRidesFragment newInstance(boolean isFavourite) {
        MyRidesFragment fragment = new MyRidesFragment();
        Bundle args = new Bundle();
        args.putBoolean(BaseGlobals.ARG_IS_FAVOURITE, isFavourite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_rides, container, false);
        mTxtSearch = (EditText) rootView.findViewById(R.id.textView_search);
        mStickyList = (StickyListHeadersListView) rootView.findViewById(R.id.listView_my_rides);
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(getActivity());
        mStickyList.setEmptyView(rootView.findViewById(R.id.empty));

        mDaoSession = dbHelper.getSession();

        List<Ride> rides;
        if (isSelectedFavouriteRides()) {
            rides = mDaoSession.queryBuilder(Ride.class).where(RideDao.Properties.IsFavourite.eq(true)).list();
        } else
            rides = mDaoSession.loadAll(Ride.class);

        mMyRidesAdapter = new MyRidesAdapter(getActivity(), getActivity().getSupportFragmentManager(), rides);
        mStickyList.setAdapter(mMyRidesAdapter);
        mStickyList.setOnItemClickListener(this);
        mStickyList.setOnItemLongClickListener(this);
        mTxtSearch.addTextChangedListener(this);
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rideId) {
        if (mActionMode == null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, RideDetailsFragment.newInstance(rideId), "RIDE_DETAILS").addToBackStack("MY_RIDES").commit();
        } else onListItemSelect(pos);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

        onListItemSelect(pos);
        return true;
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


    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (isSelectedFavouriteRides())
            actionBar.setTitle(R.string.title_my_favourite_rides);
        else
            actionBar.setTitle(R.string.title_my_rides);
    }

    private boolean isSelectedFavouriteRides() {
        Bundle bundle = getArguments();
        return bundle != null && bundle.getBoolean(BaseGlobals.ARG_IS_FAVOURITE);
    }

    private void onListItemSelect(int position) {
        mMyRidesAdapter.toggleSelection(position);
        boolean hasCheckedItems = mMyRidesAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mMyRidesAdapter
                    .getSelectedCount()) + " selected");
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_main, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:
                    // retrieve selected items and delete them out
                    SparseBooleanArray selected = mMyRidesAdapter
                            .getSelectedIds();
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Ride selectedItem = mMyRidesAdapter
                                    .getItem(selected.keyAt(i));
                            mMyRidesAdapter.remove(selectedItem);

                            mDaoSession.delete(selectedItem);
                        }
                    }
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            mMyRidesAdapter.removeSelection();
            mActionMode = null;
        }
    }
}

