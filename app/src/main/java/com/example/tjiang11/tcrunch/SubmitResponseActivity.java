package com.example.tjiang11.tcrunch;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Response;
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
        newResponse.setValue(new Response(author, response.getText().toString()));

        DatabaseReference answeredRef = mDatabaseReference.child("answered").child(mFirebaseInstanceId.getId());
        DatabaseReference newAnswered = answeredRef.push();
        newAnswered.setValue(ticketId);
        finish();
    }
}
