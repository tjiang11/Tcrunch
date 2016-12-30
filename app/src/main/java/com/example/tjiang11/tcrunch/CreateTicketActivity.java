package com.example.tjiang11.tcrunch;

import android.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateTicketActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private Spinner classSpinner;
    private TextView setDate;
    private TextView setTime;
    private TextView setLength;
    private SeekBar mSeekBar;
    private Button createTicketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        classSpinner = (Spinner) findViewById(R.id.class_spinner);
        setDate = (TextView) findViewById(R.id.set_date);
        setTime = (TextView) findViewById(R.id.set_time);
        setLength = (TextView) findViewById(R.id.set_length);
        mSeekBar = (SeekBar) findViewById(R.id.seekBarLength);
        createTicketButton = (Button) findViewById(R.id.create_ticket_button);

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
        createTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTicket();
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

    public void createTicket() {
        Ticket dummyTicket = new Ticket("What's your favorite color", Ticket.QuestionType.FreeResponse, "6:00PM", "8:00PM", classSpinner.getSelectedItem().toString());
        DatabaseReference newTicketRef = mDatabase.child("tickets").push();
        String newTicketId = newTicketRef.getKey();
        dummyTicket.setId(newTicketId);
        newTicketRef.setValue(dummyTicket);
    }
}
