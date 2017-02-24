package com.toniebalonie.tjiang11.tcrunch;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TimePicker;


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
