package com.example.tjiang11.tcrunch;

import android.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class CreateTicketActivity extends AppCompatActivity {

    private TextView setDate;
    private TextView setTime;
    private TextView setLength;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        setDate = (TextView) findViewById(R.id.set_date);
        setTime = (TextView) findViewById(R.id.set_time);
        setLength = (TextView) findViewById(R.id.set_length);
        mSeekBar = (SeekBar) findViewById(R.id.seekBarLength);

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        mSeekBar.setMax(23);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(progress + 1) + " hour");
                if (progress != 0) {
                    sb.append("s");
                }
                setLength.setText(sb.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerDialogFragment.newInstance("dummyArg1", "dummyArg2").show(getFragmentManager(), "f");
    }

    private void showTimePickerDialog() {
        TimePickerDialogFragment.newInstance("dummyArg1", "dummyArg2").show(getFragmentManager(), "g");
    }

    public void doDatePickerDialogPositiveClick(int day, int month, int year, String dayOfWeek) {
        String newDate = dayOfWeek + ", " + month + "/" + day + "/" + year;
        setDate.setText(newDate);
    }
    public void doDatePickerDialogNegativeClick() {
        Log.i("CreateTicketActivity", "DatePickerDialogNegativeClick");
    }

    public void doTimePickerDialogPositiveClick(int hour, int minute, String AM_PM) {
        String newTime = "" + hour + ":" + minute + " " + AM_PM;
        setTime.setText(newTime);
    }

    public void doTimePickerDialogNegativeClick() {
        Log.i("CreateTicketActivity", "TimePickerDialogNegativeClick");
    }
}
