package com.example.tjiang11.tcrunch;

import android.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CreateTicketActivity extends AppCompatActivity {

    private TextView setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        setDate = (TextView) findViewById(R.id.set_date);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerDialogFragment.newInstance("dummyArg1", "dummyArg2").show(getFragmentManager(), "f");
    }

    public void doPositiveClick(int day, int month, int year, String dayOfWeek) {
        Log.i("CreateTicketActivity", "Positive");
        Log.i("CreateTicketActivity", "Day: " + day + "M: " + month + "Y: " + year);
        String newDate = dayOfWeek + ", " + month + "/" + day + "/" + year;
        setDate.setText(newDate);
    }
    public void doNegativeClick() {
        Log.i("CreateTicketActivity", "Negative");
    }
}
