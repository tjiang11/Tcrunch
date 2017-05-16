package com.toniebalonie.tjiang11.tcrunch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.DatePicker;

import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.activities.CreateTicketActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatePickerDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatePickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatePickerDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    private DatePicker dp;

    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DatePickerDialogFragment.
     */
    public static DatePickerDialogFragment newInstance() {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
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
        return new DatePickerDialog.Builder(getActivity())
                .setTitle("Pick a Date")
                .setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_date_picker_dialog, null))
                .setPositiveButton(R.string.time_picker_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dp = (DatePicker) getDialog().findViewById(R.id.new_ticket_date_picker);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                                Date date = calendar.getTime();
                                String dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(date);
                                ((CreateTicketActivity)getActivity()).doDatePickerDialogPositiveClick(dp.getDayOfMonth(), dp.getMonth(), dp.getYear(), dayOfWeek);
                            }
                        }
                )
                .setNegativeButton(R.string.time_picker_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {}
                        }
                )
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
