package com.moods.bikersrides.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moods.bikersrides.R;
import com.moods.bikersrides.adapters.AutoCompleteLoading;
import com.moods.bikersrides.adapters.PlacesAutocompleteAdapter;
import com.moods.bikersrides.common.BaseGlobals;
import com.moods.bikersrides.common.IBaseModel;
import com.moods.bikersrides.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ViaDialog extends DialogFragment implements IBaseModel, View.OnClickListener {
    private AutoCompleteLoading mTxtVia;
    private ImageButton mBtnAddVia;
    private Button mBtnSaveVia;
    private LinearLayout mContainer;
    private List<String> mListVia = new ArrayList<String>();
    private ProgressBar mProgress;


    public ViaDialog() {
    }

    public static ViaDialog newInstance(ArrayList<String> viaEntries) {
        ViaDialog viaDialog = new ViaDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(BaseGlobals.ARG_VIA_ENTRIES, viaEntries);
        viaDialog.setArguments(args);
        return viaDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final Dialog dialog = new Dialog(getActivity());
        View view = inflater.inflate(R.layout.dialog_via, null);
        dialog.setContentView(view);
        mTxtVia = (AutoCompleteLoading) view.findViewById(R.id.autoCompleteTextView_via);
        mProgress = (ProgressBar) view.findViewById(R.id.progress_autocomplete);
        mBtnAddVia = (ImageButton) view.findViewById(R.id.button_add_via);
        mBtnSaveVia = (Button) view.findViewById(R.id.button_save_via);
        mContainer = (LinearLayout) view.findViewById(R.id.container_via);

        ArrayAdapter<String> adapter = new PlacesAutocompleteAdapter(getActivity(), 0);
        mTxtVia.setThreshold(BaseGlobals.THRESHOLD);
        mTxtVia.setAdapter(adapter);
        mTxtVia.setLoadingIndicator(mProgress);

        mBtnAddVia.setOnClickListener(this);
        mBtnSaveVia.setOnClickListener(this);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);

        if (getArguments() != null && getArguments().getStringArrayList(BaseGlobals.ARG_VIA_ENTRIES) != null) {
            Log.i(getClass().toString(), "VIA ENTRIES: " + String.valueOf(getArguments().getStringArrayList(BaseGlobals.ARG_VIA_ENTRIES).size()));
            mListVia = getArguments().getStringArrayList(BaseGlobals.ARG_VIA_ENTRIES);
            for (String place : mListVia) {
                createViaEntries(place);
            }

        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(final View view) {

        if (view.equals(mBtnAddVia)) {
            if (isValidInput()) {
                String viaEntry = mTxtVia.getText().toString();
                createViaEntries(viaEntry);
                mListVia.add(viaEntry);
                mTxtVia.getText().clear();
            }
        }
        if (view.equals(mBtnSaveVia)) {
            Intent intent = new Intent();//put reference back to addride fragment
            intent.putStringArrayListExtra(BaseGlobals.ARG_VIA_ENTRIES, (ArrayList<String>) mListVia);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getDialog().dismiss();
        }
    }

    @Override
    public boolean isValidInput() {
        if (StringUtils.isNullOrEmpty(mTxtVia.getText().toString())) {
            mTxtVia.setError(getString(R.string.enter_via));
            return false;
        }
        return true;
    }

    private void createViaEntries(String viaEntry) {
        LayoutInflater layoutInflater =
                (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row_via, null);
        TextView textOut = (TextView) addView.findViewById(R.id.textView_via);
        ImageButton buttonRemove = (ImageButton) addView.findViewById(R.id.button_remove_via);
        textOut.setText(viaEntry);
        mBtnSaveVia.setVisibility(View.VISIBLE);
        addView.setTag(mListVia.size() - 1);
        buttonRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = (Integer) addView.getTag();

                if (mListVia.size() > position) {
                    mListVia.remove(position);
                    ((LinearLayout) addView.getParent()).removeView(addView);
                }
                Log.i(getClass().toString(), "VIA ENTRIES SIZE: " + String.valueOf(mListVia.size()));
                Log.i(getClass().toString(), "VIA POSITION: " + String.valueOf(position));
            }
        });

        mContainer.addView(addView);
    }


}
