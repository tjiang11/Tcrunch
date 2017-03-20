package com.toniebalonie.tjiang11.tcrunch;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimePickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private TimePicker tp;

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimePickerDialogFragment.
     */
    public static TimePickerDialogFragment newInstance() {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.MINUTE, -1);
        return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                ((CreateTicketActivity)getActivity()).doTimePickerDialogPositiveClick(hourOfDay, minute);
            }
        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false);
    }
}
