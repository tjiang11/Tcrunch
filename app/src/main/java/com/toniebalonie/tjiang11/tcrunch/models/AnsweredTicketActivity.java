package com.toniebalonie.tjiang11.tcrunch.models;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.toniebalonie.tjiang11.tcrunch.R;

import org.w3c.dom.Text;

public class AnsweredTicketActivity extends AppCompatActivity {

    private static String TAG = AnsweredTicketActivity.class.getName();
    private AnsweredTicketActivity ata = this;

    private DatabaseReference mDatabaseReference;
    private FirebaseInstanceId mFirebaseInstanceId;

    private TextView questionText;
    private TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answered_ticket);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseInstanceId = FirebaseInstanceId.getInstance();

        questionText = (TextView) findViewById(R.id.answered_ticket_question);
        responseText = (TextView) findViewById(R.id.answered_ticket_response);

        Bundle bundle = getIntent().getExtras();
        String question = bundle.getString("question");
        String ticketId = bundle.getString("ticket_id");
        DatabaseReference responseRef = mDatabaseReference.child("responses").child(ticketId).child(mFirebaseInstanceId.getId());
        responseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Response response = dataSnapshot.getValue(Response.class);
                if (response == null) {
                    responseText.setText("Response unavailable");
                    responseText.setTextColor(Color.RED);
                    Toast.makeText(ata, "Unable to view response. This response my have been submitted before the last app update that allows students to view past responses.", Toast.LENGTH_LONG).show();
                } else {
                    responseText.setText(response.getResponse());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.getMessage());
            }
        });
        questionText.setText(question);
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
}
