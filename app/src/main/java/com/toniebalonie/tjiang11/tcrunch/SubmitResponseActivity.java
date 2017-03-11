package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.models.Response;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class SubmitResponseActivity extends AppCompatActivity {

    private TextView questionText;
    private EditText response;
    private Button submitResponse;
    private DatabaseReference mDatabaseReference;
    private CheckBox anon;
    private RadioGroup submitMultipleChoice;
    private RadioButton choiceOne;
    private RadioButton choiceTwo;
    private RadioButton choiceThree;
    private RadioButton choiceFour;
    private RadioButton choiceFive;

    private String ticketId;

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
        anon = (CheckBox) findViewById(R.id.submit_anon);

        Bundle bundle = getIntent().getExtras();
        String question = bundle.getString("question");
        ArrayList<String> answerChoices = bundle.getStringArrayList("answer_choices");
        ticketId = bundle.getString("ticket_id");

        questionText = (TextView) findViewById(R.id.submit_response_question);
        questionText.setText(question);

        submitResponse = (Button) findViewById(R.id.submit_response_button);
        submitResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResponse();
            }
        });

        submitMultipleChoice = (RadioGroup) findViewById(R.id.submit_multiple_choice);
        choiceOne = (RadioButton) findViewById(R.id.choice_one);
        choiceTwo = (RadioButton) findViewById(R.id.choice_two);
        choiceThree = (RadioButton) findViewById(R.id.choice_three);
        choiceFour = (RadioButton) findViewById(R.id.choice_four);
        choiceFive = (RadioButton) findViewById(R.id.choice_five);

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

    private void submitResponse() {
        if (questionType == QuestionType.MULTIPLE_CHOICE && submitMultipleChoice.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "You must select a choice.", Toast.LENGTH_LONG).show();
            return;
        }
        if (questionType == QuestionType.FREE_RESPONSE && response.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a response before submitting.", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        String author = sharedPreferences.getString("student_name", "Anonymous");
        if (anon.isChecked()) {
            author = "Anonymous";
        }
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
        }
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
