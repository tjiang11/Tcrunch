package com.example.tjiang11.tcrunch;

import android.app.DialogFragment;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Response;
import com.example.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateTicketActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private Spinner classSpinner;
    private TextView setDate;
    private TextView setTime;
    private TextView setLength;
    private SeekBar mSeekBar;
    private Button createTicketButton;
    private EditText question;

    private int startyear; private int startmonth; private int startday;
    private int starthour; private int startminute;
    private int endyear; private int endmonth; private int endday;
    private int endhour; private int endminute;

    private Long startTime;
    private Long endTime;
    private int ticketLength;

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
        question = (EditText) findViewById(R.id.ask_question);
        createTicketButton = (Button) findViewById(R.id.create_ticket_button);

        ticketLength = 1;

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
                ticketLength = progress + 1;
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
        String newDate = dayOfWeek + ", " + (month + 1) + "/" + day + "/" + year;
        startyear = year; startmonth = month; startday = day;

        setDate.setText(newDate);
    }
    public void doDatePickerDialogNegativeClick() {
        Log.i("CreateTicketActivity", "DatePickerDialogNegativeClick");
    }

    public void doTimePickerDialogPositiveClick(int tpHour, int tpMinute) {
        int hour = tpHour;
        String AM_PM;
        AM_PM = tpHour < 12 ? "AM" : "PM";
        if (tpHour == 0) hour = 12;
        if (tpHour > 12) hour = tpHour - 12;
        String zeroPad = "";
        if (tpMinute < 10) zeroPad = "0";
        String newTime = "" + hour + ":" + zeroPad + tpMinute + " " + AM_PM;

        setTime.setText(newTime);
    }

    public void doTimePickerDialogNegativeClick() {
        Log.i("CreateTicketActivity", "TimePickerDialogNegativeClick");
    }

    public void createTicket() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startyear);
        calendar.set(Calendar.MONTH, startmonth);
        calendar.set(Calendar.DAY_OF_MONTH, startday);
        calendar.set(Calendar.HOUR, starthour);
        calendar.set(Calendar.MINUTE, startminute);
        long startTime = calendar.getTimeInMillis();
        int msPerHour = 3600000;
        long endTime = startTime + ticketLength * msPerHour;
        Ticket dummyTicket = new Ticket(question.getText().toString(),
                Ticket.QuestionType.FreeResponse, startTime, endTime, classSpinner.getSelectedItem().toString());
        ArrayList<String> answerChoices = new ArrayList<>(Arrays.asList("Choice A", "Choice B", "Choice C"));
        dummyTicket.setAnswerChoices(answerChoices);
        List<Response> responses = dummyTicket.getResponses();
        responses.add(new Response("tony", "i don't know the answer sorry prof"));
        responses.add(new Response("caty", "four"));
        DatabaseReference newTicketRef = mDatabase.child("tickets").push();
        String newTicketId = newTicketRef.getKey();
        dummyTicket.setId(newTicketId);
        newTicketRef.setValue(dummyTicket);
        finish();
    }
}
