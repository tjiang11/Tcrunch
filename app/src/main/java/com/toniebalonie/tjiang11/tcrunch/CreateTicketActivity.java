package com.toniebalonie.tjiang11.tcrunch;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.models.Classroom;
import com.toniebalonie.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class CreateTicketActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Spinner classSpinner;
    private TextView setDate;
    private TextView setTime;
    private TextView setLength;

    private Button createTicketButton;
    private EditText question;

    private CheckBox anonymousCheckBox;

    private CheckBox mcCheckBox;
    private EditText choiceOne;
    private EditText choiceTwo;
    private EditText choiceThree;
    private EditText choiceFour;
    private EditText choiceFive;
    private TextView addChoice;
    private ImageView removeChoiceOne;
    private ImageView removeChoiceTwo;
    private ImageView removeChoiceThree;
    private ImageView removeChoiceFour;
    private ImageView removeChoiceFive;

    private int startyear; private int startmonth; private int startday;
    private int starthour; private int startminute;
    private int endyear; private int endmonth; private int endday;
    private int endhour; private int endminute;

    private Long startTime;
    private Long endTime;
    private int ticketLength;
    private int choiceNum;

    private String classId;
    private String className;
    private ArrayList<String> classList;
    private HashMap<String, Classroom> classMap;

    private boolean launchDateSet = false;
    private boolean launchTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        classList = getIntent().getStringArrayListExtra("classes");
        classMap = (HashMap<String, Classroom>) getIntent().getSerializableExtra("classMap");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        classSpinner = (Spinner) findViewById(R.id.class_spinner);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, classList);
        classSpinner.setAdapter(spinnerArrayAdapter);

        for (int i = 0; i < classList.size(); i++) {
            if (className.equals(classList.get(i))) {
                classSpinner.setSelection(i);
            }
        }

        setDate = (TextView) findViewById(R.id.set_date);
        setTime = (TextView) findViewById(R.id.set_time);
        question = (EditText) findViewById(R.id.ask_question);
        createTicketButton = (Button) findViewById(R.id.create_ticket_button);
        anonymousCheckBox = (CheckBox) findViewById(R.id.anonResponseCheckbox);
        mcCheckBox = (CheckBox) findViewById(R.id.multipleChoiceCheckbox);
        choiceOne = (EditText) findViewById(R.id.choiceOne);
        choiceTwo = (EditText) findViewById(R.id.choiceTwo);
        choiceThree = (EditText) findViewById(R.id.choiceThree);
        choiceFour = (EditText) findViewById(R.id.choiceFour);
        choiceFive = (EditText) findViewById(R.id.choiceFive);
        addChoice = (TextView) findViewById(R.id.add_choice);
        removeChoiceOne = (ImageView) findViewById(R.id.removeChoiceOne);
        removeChoiceTwo = (ImageView) findViewById(R.id.removeChoiceTwo);
        removeChoiceThree = (ImageView) findViewById(R.id.removeChoiceThree);
        removeChoiceFour = (ImageView) findViewById(R.id.removeChoiceFour);
        removeChoiceFive = (ImageView) findViewById(R.id.removeChoiceFive);
        removeChoiceOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAnswerChoice(1);
            }
        });
        removeChoiceTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAnswerChoice(2);
            }
        });
        removeChoiceThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAnswerChoice(3);
            }
        });
        removeChoiceFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAnswerChoice(4);
            }
        });
        removeChoiceFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAnswerChoice(5);
            }
        });

        choiceNum = 0;
        addChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAnswerChoice();
            }
        });

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

        mcCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i("CreateTicketActivity", "checked!");
                    if (choiceNum < 4) {
                        addChoice.setVisibility(View.VISIBLE);
                    }
                    switch (choiceNum) {
                        case 4:
                            choiceFive.setVisibility(View.VISIBLE);
                            removeChoiceFive.setVisibility(View.VISIBLE);
                        case 3:
                            choiceFour.setVisibility(View.VISIBLE);
                            removeChoiceFour.setVisibility(View.VISIBLE);
                        case 2:
                            choiceThree.setVisibility(View.VISIBLE);
                            removeChoiceThree.setVisibility(View.VISIBLE);
                        case 1:
                            choiceTwo.setVisibility(View.VISIBLE);
                            removeChoiceTwo.setVisibility(View.VISIBLE);
                        case 0:
                            choiceOne.setVisibility(View.VISIBLE);
                            removeChoiceOne.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                else {
                    Log.i("CreateTicketActivity", "unchecked!");
                    addChoice.setVisibility(View.GONE);
                    choiceOne.setVisibility(View.GONE);
                    choiceTwo.setVisibility(View.GONE);
                    choiceThree.setVisibility(View.GONE);
                    choiceFour.setVisibility(View.GONE);
                    choiceFive.setVisibility(View.GONE);
                    removeChoiceOne.setVisibility(View.GONE);
                    removeChoiceTwo.setVisibility(View.GONE);
                    removeChoiceThree.setVisibility(View.GONE);
                    removeChoiceFour.setVisibility(View.GONE);
                    removeChoiceFive.setVisibility(View.GONE);
                }

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
        DatePickerDialogFragment.newInstance().show(getFragmentManager(), "f");
    }

    private void showTimePickerDialog() {
        TimePickerDialogFragment.newInstance().show(getFragmentManager(), "g");
    }

    public void doDatePickerDialogPositiveClick(int day, int month, int year, String dayOfWeek) {
        String newDate = dayOfWeek + ", " + (month + 1) + "/" + day + "/" + year;
        startyear = year; startmonth = month; startday = day + 1;
        setDate.setText(newDate);
        launchDateSet = true;

        if (launchTimeSet) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(startyear, startmonth, startday - 1);
            calendar.set(Calendar.HOUR_OF_DAY, starthour);
            calendar.set(Calendar.MINUTE, startminute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis() - 120000) {
                Toast.makeText(this, "You've selected a launch time in the past. We've set the time to be now.", Toast.LENGTH_LONG).show();
                calendar = Calendar.getInstance();
                startyear = calendar.get(Calendar.YEAR);
                startmonth = calendar.get(Calendar.MONTH);
                startday = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                starthour = calendar.get(Calendar.HOUR_OF_DAY);
                startminute = calendar.get(Calendar.MINUTE);

                int hour = starthour;
                int minute = startminute;
                String zeroPad = "";
                String AM_PM = hour < 12 ? "AM" : "PM";
                if (hour == 0) hour = 12;
                if (hour > 12) hour = hour - 12;
                if (minute < 10) zeroPad = "0";

                String newTime = "" + hour + ":" + zeroPad + minute + " " + AM_PM;
                setTime.setText(newTime);

                newDate = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) + ", " +
                        (calendar.get(Calendar.MONTH) + 1) + "/" +
                        calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                        calendar.get(Calendar.YEAR);
                setDate.setText(newDate);
            }
        }
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

        starthour = tpHour;
        startminute = tpMinute;
        if (launchDateSet) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(startyear, startmonth, startday - 1);
            calendar.set(Calendar.HOUR_OF_DAY, starthour);
            calendar.set(Calendar.MINUTE, startminute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis() - 120000) {
                Toast.makeText(this, "You've selected a launch time in the past. We've set the time to be now.", Toast.LENGTH_LONG).show();
                calendar = Calendar.getInstance();
                tpHour = calendar.get(Calendar.HOUR_OF_DAY);
                tpMinute = calendar.get(Calendar.MINUTE);
                starthour = tpHour;
                startminute = tpMinute;
                AM_PM = tpHour < 12 ? "AM" : "PM";
                if (tpHour == 0) hour = 12;
                if (tpHour > 12) hour = tpHour - 12;
                if (tpMinute < 10) zeroPad = "0";
                newTime = "" + hour + ":" + zeroPad + tpMinute + " " + AM_PM;

                String newDate = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) + ", " +
                        (calendar.get(Calendar.MONTH) + 1) + "/" +
                        calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                        calendar.get(Calendar.YEAR);
                startyear = calendar.get(Calendar.YEAR);
                startmonth = calendar.get(Calendar.MONTH);
                startday = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                setDate.setText(newDate);
            }
        }
        setTime.setText(newTime);
        launchTimeSet = true;

    }

    public void createTicket() {
        if (!launchDateSet) {
            Toast.makeText(this, "Please select a launch date.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!launchTimeSet) {
            Toast.makeText(this, "Please select a launch time.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (question.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ticket cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(startyear, startmonth, startday - 1);
        calendar.set(Calendar.HOUR_OF_DAY, starthour);
        calendar.set(Calendar.MINUTE, startminute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, "Your ticket has been launched.", Toast.LENGTH_SHORT).show();
        }

        final int msPerHour = 3600000;
        long endTime = startTime + ticketLength * msPerHour;
        Ticket newTicket = new Ticket(question.getText().toString(),
                Ticket.QuestionType.FreeResponse, startTime, endTime,
                classSpinner.getSelectedItem().toString(), anonymousCheckBox.isChecked());
        ArrayList<String> answerChoices = new ArrayList<>();
        if (mcCheckBox.isChecked()) {
            if (choiceOne.getVisibility() == View.VISIBLE && !choiceOne.getText().toString().isEmpty()) {
                answerChoices.add(choiceOne.getText().toString());
            }
            if (choiceTwo.getVisibility() == View.VISIBLE && !choiceTwo.getText().toString().isEmpty()) {
                answerChoices.add(choiceTwo.getText().toString());
            }
            if (choiceThree.getVisibility() == View.VISIBLE && !choiceThree.getText().toString().isEmpty()) {
                answerChoices.add(choiceThree.getText().toString());
            }
            if (choiceFour.getVisibility() == View.VISIBLE && !choiceFour.getText().toString().isEmpty()) {
                answerChoices.add(choiceFour.getText().toString());
            }
            if (choiceFive.getVisibility() == View.VISIBLE && !choiceFive.getText().toString().isEmpty()) {
                answerChoices.add(choiceFive.getText().toString());
            }
        }
        newTicket.setAnswerChoices(answerChoices);
        if (mAuth.getCurrentUser() != null) {
            String theClassId = classMap.get(classSpinner.getSelectedItem().toString()).getId();
            DatabaseReference newTicketRef2 = mDatabase.child("tickets").child(theClassId).push();
            String newTicketId = newTicketRef2.getKey();
            newTicket.setId(newTicketId);
            newTicketRef2.setValue(newTicket);
            finish();
        } else {
            Log.w("CreateTicketActivity", "User not logged in.");
            Toast.makeText(this, "Error: Could not find current user.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void removeAnswerChoice(int choiceToRemove) {
        addChoice.setVisibility(View.VISIBLE);

        switch (choiceToRemove) {
            case 1:
                choiceOne.setText(choiceTwo.getText());
            case 2:
                choiceTwo.setText(choiceThree.getText());
            case 3:
                choiceThree.setText(choiceFour.getText());
            case 4:
                choiceFour.setText(choiceFive.getText());
                break;
        }

        switch (choiceNum) {
            case 4:
                choiceFive.setVisibility(View.GONE);
                choiceFive.setText("");
                break;
            case 3:
                choiceFour.setVisibility(View.GONE);
                choiceFour.setText("");
                break;
            case 2:
                choiceThree.setVisibility(View.GONE);
                choiceThree.setText("");
                break;
            case 1:
                choiceTwo.setVisibility(View.GONE);
                choiceTwo.setText("");
                break;
        }
        if (choiceNum > 0) {
            choiceNum--;
        }
    }

    private void addAnswerChoice() {
        switch (choiceNum) {
            case 0:
                choiceTwo.setVisibility(View.VISIBLE);
                removeChoiceTwo.setVisibility(View.VISIBLE);
                choiceTwo.requestFocus();
                break;
            case 1:
                choiceThree.setVisibility(View.VISIBLE);
                removeChoiceThree.setVisibility(View.VISIBLE);
                choiceThree.requestFocus();
                break;
            case 2:
                choiceFour.setVisibility(View.VISIBLE);
                removeChoiceFour.setVisibility(View.VISIBLE);
                choiceFour.requestFocus();
                break;
            case 3:
                choiceFive.setVisibility(View.VISIBLE);
                removeChoiceFive.setVisibility(View.VISIBLE);
                choiceFive.requestFocus();
                addChoice.setVisibility(View.GONE);
                break;
        }
        if (choiceNum <= 3) {
            choiceNum++;
        }
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }

    private void checkTimeInPast() {

    }
}
