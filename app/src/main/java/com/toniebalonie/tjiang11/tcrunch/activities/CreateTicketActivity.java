package com.toniebalonie.tjiang11.tcrunch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.fragments.DatePickerDialogFragment;
import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.fragments.TimePickerDialogFragment;
import com.toniebalonie.tjiang11.tcrunch.models.Classroom;
import com.toniebalonie.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CreateTicketActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Spinner classSpinner;
    private TextView setDate;
    private TextView setTime;

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

    private long startTime;
    private long endTime;
    private int ticketLength;
    private int choiceNum;

    private String classId;
    private String origClassId;
    private String className;
    private ArrayList<String> classList;
    private HashMap<String, Classroom> classMap;

    private boolean launchDateSet = false;
    private boolean launchTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        if (getIntent().hasExtra("is_editing")) {
            getSupportActionBar().setTitle("Edit Ticket");
        }

        className = getIntent().getStringExtra("className");
        classList = getIntent().getStringArrayListExtra("classes");
        classMap = (HashMap<String, Classroom>) getIntent().getSerializableExtra("classMap");
        classId = classMap.get(className).getId();

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
        if (getIntent().hasExtra("is_editing")) {
            prepopulateData();
        }

        if (getIntent().hasExtra("pre_pop_question")) {
            question.setText(getIntent().getStringExtra("pre_pop_question"));
        }

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

    public void updateTicket() {
        if (!validateTicket()) {
            return;
        }

        Ticket newTicket = generateTicket();

        if (mAuth.getCurrentUser() != null) {
            String ticketId = getIntent().getStringExtra("ticket_id");
            if (!className.equals(classSpinner.getSelectedItem().toString())) {
                DatabaseReference oldTicketRef = mDatabase.child("tickets").child(classId).child(ticketId);
                oldTicketRef.removeValue();
            }
            String theClassId = classMap.get(classSpinner.getSelectedItem().toString()).getId();
            DatabaseReference newTicketRef = mDatabase.child("tickets").child(theClassId).child(ticketId);
            newTicket.setId(ticketId);
            newTicketRef.setValue(newTicket);
            Intent intent = new Intent();
            intent.putExtra("class_name", classSpinner.getSelectedItem().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Log.e("CreateTicketActivity", "User not logged in.");
            Toast.makeText(this, "Error: Could not find current user.", Toast.LENGTH_SHORT).show();
        }
    }

    public void createTicket() {
        if (!validateTicket()) {
            return;
        }

        Ticket newTicket = generateTicket();

        if (mAuth.getCurrentUser() != null) {
            String theClassId = classMap.get(classSpinner.getSelectedItem().toString()).getId();
            DatabaseReference newTicketRef = mDatabase.child("tickets").child(theClassId).push();
            String newTicketId = newTicketRef.getKey();
            newTicket.setId(newTicketId);
            newTicketRef.setValue(newTicket);
            finish();
        } else {
            Log.e("CreateTicketActivity", "User not logged in.");
            Toast.makeText(this, "Error: Could not find current user.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTicket() {
        final CreateTicketActivity parent = this;
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete this ticket?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ticketId = getIntent().getStringExtra("ticket_id");
                        DatabaseReference oldTicketRef = mDatabase.child("tickets").child(classId).child(ticketId);
                        oldTicketRef.removeValue();
                        Toast.makeText(parent, "Ticket deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private Ticket generateTicket() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(startyear, startmonth, startday - 1);
        calendar.set(Calendar.HOUR_OF_DAY, starthour);
        calendar.set(Calendar.MINUTE, startminute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startTime = calendar.getTimeInMillis();
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, "Your ticket has been launched.", Toast.LENGTH_SHORT).show();
        }

        final int msPerHour = 3600000;
        endTime = startTime + ticketLength * msPerHour;
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
        return newTicket;
    }

    private boolean validateTicket() {
        if (!launchDateSet) {
            Toast.makeText(this, "Please select a launch date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!launchTimeSet) {
            Toast.makeText(this, "Please select a launch time.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (question.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ticket cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mcCheckBox.isChecked() &&
                choiceOne.getText().toString().isEmpty() &&
                choiceTwo.getText().toString().isEmpty() &&
                choiceThree.getText().toString().isEmpty() &&
                choiceFour.getText().toString().isEmpty() &&
                choiceFive.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must have at least one answer choice.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.create:
                createTicket();
                break;
            case R.id.update:
                updateTicket();
                break;
            case R.id.delete:
                deleteTicket();
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().hasExtra("is_editing")) {
            getMenuInflater().inflate(R.menu.menu_update_ticket, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_create_ticket, menu);
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
                removeChoiceFive.setVisibility(View.GONE);
                choiceFive.setText("");
                break;
            case 3:
                choiceFour.setVisibility(View.GONE);
                removeChoiceFour.setVisibility(View.GONE);
                choiceFour.setText("");
                break;
            case 2:
                choiceThree.setVisibility(View.GONE);
                removeChoiceThree.setVisibility(View.GONE);
                choiceThree.setText("");
                break;
            case 1:
                choiceTwo.setVisibility(View.GONE);
                removeChoiceTwo.setVisibility(View.GONE);
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

    private void prepopulateData() {
        long time = getIntent().getLongExtra("start_time", 0);
        Date date = new Date(time);
        SimpleDateFormat formatterDate = new SimpleDateFormat("EEEE, M/d/yyyy", Locale.US);
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a", Locale.US);
        String dateFormatted = formatterDate.format(date);
        String timeFormatted = formatterTime.format(date);

        launchDateSet = true;
        launchTimeSet = true;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        startyear = cal.get(Calendar.YEAR);
        startmonth = cal.get(Calendar.MONTH);
        startday = cal.get(Calendar.DAY_OF_MONTH) + 1;
        starthour = cal.get(Calendar.HOUR_OF_DAY);
        startminute = cal.get(Calendar.MINUTE);

        setDate.setText(dateFormatted);
        setTime.setText(timeFormatted);
        question.setText(getIntent().getStringExtra("question"));

        boolean isAnonymous = getIntent().getBooleanExtra("anonymous", false);
        if (isAnonymous) {
            anonymousCheckBox.setChecked(true);
        }

        ArrayList<String> answerChoices = getIntent().getStringArrayListExtra("answer_choices");
        if (!answerChoices.isEmpty()) {
            mcCheckBox.setChecked(true);
            choiceNum = answerChoices.size() - 1;
            switch (answerChoices.size()) {
                case 5:
                    choiceFive.setVisibility(View.VISIBLE);
                    choiceFive.setText(answerChoices.get(4));
                    removeChoiceFive.setVisibility(View.VISIBLE);
                case 4:
                    choiceFour.setVisibility(View.VISIBLE);
                    choiceFour.setText(answerChoices.get(3));
                    removeChoiceFour.setVisibility(View.VISIBLE);
                case 3:
                    choiceThree.setVisibility(View.VISIBLE);
                    choiceThree.setText(answerChoices.get(2));
                    removeChoiceThree.setVisibility(View.VISIBLE);
                case 2:
                    choiceTwo.setVisibility(View.VISIBLE);
                    choiceTwo.setText(answerChoices.get(1));
                    removeChoiceTwo.setVisibility(View.VISIBLE);
                case 1:
                    choiceOne.setVisibility(View.VISIBLE);
                    choiceOne.setText(answerChoices.get(0));
                    removeChoiceOne.setVisibility(View.VISIBLE);
            }

            if (choiceNum <= 3) {
                addChoice.setVisibility(View.VISIBLE);
            }
        }
    }
}
