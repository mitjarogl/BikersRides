package com.moods.bikersrides.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.moods.bikersrides.R;

import java.util.Calendar;



public class DateTimePicker extends DialogFragment
{
    int mDialogType;
    public static final int DATE_DIALOG_ID = 0, TIME_DIALOG_ID = 1;
    private DatePickerDialog.OnDateSetListener mDateListener;
    private TimePickerDialog.OnTimeSetListener mTimeListener;

    public static DateTimePicker newInstance(int dialogType)
    {
        DateTimePicker f = new DateTimePicker();
        Bundle args = new Bundle();
        args.putInt("mDialogType", dialogType);
        f.setArguments(args);
        return f;
    }

    public void setmDateListener(DatePickerDialog.OnDateSetListener mDateListener)
    {
        this.mDateListener = mDateListener;
    }

    public void setmTimeListener(TimePickerDialog.OnTimeSetListener mTimeListener)
    {
        this.mTimeListener = mTimeListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDialogType = getArguments().getInt("mDialogType");
        String title;
        switch (mDialogType)
        {
            case TIME_DIALOG_ID:
                title = getResources().getString(R.string.pick_time);
                return getTimeDialog(getActivity(), title, mTimeListener);
            default:
                title = getResources().getString(R.string.pick_date);
                DatePickerDialog dateDialog = getDateDialog(getActivity(), title, mDateListener);
            //    dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                return dateDialog;
        }
    }

    private DatePickerDialog getDateDialog(Context context, String message, DatePickerDialog.OnDateSetListener listener)
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(context, listener, year, month, day);
        datePicker.setTitle(message);
        return datePicker;
    }

    private TimePickerDialog getTimeDialog(Context context, String message, TimePickerDialog.OnTimeSetListener listener)
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePicker = new TimePickerDialog(context, listener, hour, minute, DateFormat.is24HourFormat(context));
        timePicker.setTitle(message);
        return timePicker;
    }
}
