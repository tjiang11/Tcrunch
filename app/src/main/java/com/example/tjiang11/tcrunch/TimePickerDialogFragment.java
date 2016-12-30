package com.example.tjiang11.tcrunch;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimePickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimePickerDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TimePicker tp;

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimePickerDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimePickerDialogFragment newInstance(String param1, String param2) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog.Builder(getActivity())
                .setTitle("Pick a Date")
                .setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_time_picker_dialog, null))
                .setPositiveButton(R.string.time_picker_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                tp = (TimePicker) getDialog().findViewById(R.id.new_ticket_time_picker);
                                int hour; int minute;
                                if (Build.VERSION.SDK_INT >= 23) {
                                    hour = tp.getHour();
                                    minute = tp.getMinute();
                                } else {
                                    hour = tp.getCurrentHour();
                                    minute = tp.getCurrentMinute();
                                }
                                ((CreateTicketActivity)getActivity()).doTimePickerDialogPositiveClick(hour, minute);
                            }
                        }
                )
                .setNegativeButton(R.string.time_picker_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((CreateTicketActivity)getActivity()).doTimePickerDialogNegativeClick();
                            }
                        }
                )
                .create();
    }

}
