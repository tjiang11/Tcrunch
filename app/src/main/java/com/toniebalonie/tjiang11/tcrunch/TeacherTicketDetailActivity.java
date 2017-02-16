package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.models.Response;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TeacherTicketDetailActivity extends AppCompatActivity {

    private ResponseListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mDatabaseReferenceResponses;
    private DatabaseReference mDatabaseReference;

    private TextView responsesText;

    private ArrayList<Response> responseList;

    private String ticketId;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ticket_detail);
        Bundle bundle = getIntent().getExtras();
        ticketId = (String) bundle.get("ticket_id");
        String question = (String) bundle.get("question");
        long startTime = (long) bundle.get("start_time");
        classId = (String) bundle.get("class_id");

        responseList = new ArrayList<Response>();

        responsesText = (TextView) findViewById(R.id.responses_text);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferenceResponses = FirebaseDatabase.getInstance().getReference().child("responses").child(ticketId);
        mDatabaseReferenceResponses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                responseList.clear();
                for (DataSnapshot responseSnapshot : dataSnapshot.getChildren()) {
                    responseList.add(responseSnapshot.getValue(Response.class));
                }
                String resp = responseList.size() + " " + getResources().getString(R.string.responses);
                responsesText.setText(resp);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error", "response reference cancelled");
            }
        });

        //ArrayList<Response> responseList = getIntent().getParcelableArrayListExtra("responses");

        TextView questionText = (TextView) findViewById(R.id.ticket_detail_question);
        TextView startTimeText = (TextView) findViewById(R.id.ticket_detail_start_time);
        RecyclerView responsesText = (RecyclerView) findViewById(R.id.ticket_detail_responses_recycler_view);
        responsesText.setFocusable(false);

        mAdapter = new ResponseListAdapter(responseList);
        responsesText.setAdapter(mAdapter);

        mLinearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        responsesText.setLayoutManager(mLinearLayoutManager);

        questionText.setText(question);

        Date date = new Date(startTime);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, h:mm a", Locale.US);
        String dateFormatted = formatter.format(date);
        startTimeText.setText(dateFormatted);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_ticket_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete_ticket) {
            final TeacherTicketDetailActivity parent = this;
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to delete this ticket?")
                    .setMessage("This action cannot be undone.")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabaseReference.child("tickets").child(classId).child(ticketId).removeValue();
                            mDatabaseReference.child("responses").child(ticketId).removeValue();
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

        return super.onOptionsItemSelected(item);
    }
}
