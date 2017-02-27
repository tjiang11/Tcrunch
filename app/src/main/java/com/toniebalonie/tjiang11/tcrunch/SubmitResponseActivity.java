package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.toniebalonie.tjiang11.tcrunch.models.Response;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SubmitResponseActivity extends AppCompatActivity {

    private TextView questionText;
    private EditText response;
    private Button submitResponse;
    private DatabaseReference mDatabaseReference;
    private CheckBox anon;

    private String ticketId;

    private FirebaseInstanceId mFirebaseInstanceId;

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
    }

    private void submitResponse() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        String author = sharedPreferences.getString("student_name", "Anonymous");
        if (anon.isChecked()) {
            author = "Anonymous";
        }
        DatabaseReference responsesRef = mDatabaseReference.child("responses").child(ticketId);
        DatabaseReference newResponse = responsesRef.push();
        newResponse.setValue(new Response(author, response.getText().toString(), System.currentTimeMillis()));

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
