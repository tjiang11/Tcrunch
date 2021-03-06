package com.toniebalonie.tjiang11.tcrunch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.activities.LoginActivity;
import com.toniebalonie.tjiang11.tcrunch.models.Response;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class SubmitResponseActivity extends AppCompatActivity {

    private TextView questionText;
    private EditText response;
    private DatabaseReference mDatabaseReference;
    private RadioGroup submitMultipleChoice;
    private RadioButton choiceOne;
    private RadioButton choiceTwo;
    private RadioButton choiceThree;
    private RadioButton choiceFour;
    private RadioButton choiceFive;
    private TextView anonIndicator;

    private String ticketId;
    private boolean anonymous;

    private FirebaseInstanceId mFirebaseInstanceId;

    private enum QuestionType {
        FREE_RESPONSE,
        MULTIPLE_CHOICE
    }

    private QuestionType questionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_response);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseInstanceId = FirebaseInstanceId.getInstance();

        response = (EditText) findViewById(R.id.submit_response_edittext);

        Bundle bundle = getIntent().getExtras();
        String question = bundle.getString("question");
        ArrayList<String> answerChoices = bundle.getStringArrayList("answer_choices");
        ticketId = bundle.getString("ticket_id");
        anonymous = bundle.getBoolean("anonymous");

        questionText = (TextView) findViewById(R.id.submit_response_question);
        questionText.setText(question);

        submitMultipleChoice = (RadioGroup) findViewById(R.id.submit_multiple_choice);
        choiceOne = (RadioButton) findViewById(R.id.choice_one);
        choiceTwo = (RadioButton) findViewById(R.id.choice_two);
        choiceThree = (RadioButton) findViewById(R.id.choice_three);
        choiceFour = (RadioButton) findViewById(R.id.choice_four);
        choiceFive = (RadioButton) findViewById(R.id.choice_five);
        anonIndicator = (TextView) findViewById(R.id.anonymous_indicator);
        if (!anonymous) {
            anonIndicator.setVisibility(View.GONE);
        }
        if (answerChoices != null && !answerChoices.isEmpty()) {
            questionType = QuestionType.MULTIPLE_CHOICE;
            submitMultipleChoice.setVisibility(View.VISIBLE);
            switch (answerChoices.size()) {
                case 5:
                    choiceFive.setVisibility(View.VISIBLE);
                    choiceFive.setText(answerChoices.get(4));
                case 4:
                    choiceFour.setVisibility(View.VISIBLE);
                    choiceFour.setText(answerChoices.get(3));
                case 3:
                    choiceThree.setVisibility(View.VISIBLE);
                    choiceThree.setText(answerChoices.get(2));
                case 2:
                    choiceTwo.setVisibility(View.VISIBLE);
                    choiceTwo.setText(answerChoices.get(1));
                case 1:
                    choiceOne.setVisibility(View.VISIBLE);
                    choiceOne.setText(answerChoices.get(0));
            }
        } else {
            questionType = QuestionType.FREE_RESPONSE;
            response.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Perform checks on response and open confirmation dialog
     */
    private void submitResponse() {
        if (questionType == QuestionType.MULTIPLE_CHOICE && submitMultipleChoice.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "You must select a choice.", Toast.LENGTH_LONG).show();
            return;
        }
        if (questionType == QuestionType.FREE_RESPONSE && response.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a response before submitting.", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to submit? You may not edit your response.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResponse();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Save response to Firebase.
     */
    private void sendResponse() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        String author = sharedPreferences.getString("student_name", "Unidentified");

        DatabaseReference responsesRef = mDatabaseReference.child("responses").child(ticketId).child(mFirebaseInstanceId.getId());

        if (questionType == QuestionType.FREE_RESPONSE) {
            responsesRef.setValue(new Response(author, response.getText().toString(), System.currentTimeMillis()));
        } else {
            int choiceId = submitMultipleChoice.getCheckedRadioButtonId();
            RadioButton selected = (RadioButton) submitMultipleChoice.findViewById(choiceId);
            responsesRef.setValue(new Response(author, selected.getText().toString(), System.currentTimeMillis()));
        }
        DatabaseReference answeredRef = mDatabaseReference.child("answered").child(mFirebaseInstanceId.getId());
        DatabaseReference newAnswered = answeredRef.push();
        newAnswered.setValue(ticketId);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.submit:
                submitResponse();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit_response, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!response.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit? Your response will not be saved.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }
}
