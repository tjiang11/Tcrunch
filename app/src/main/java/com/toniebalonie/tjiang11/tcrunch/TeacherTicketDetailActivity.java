package com.toniebalonie.tjiang11.tcrunch;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.toniebalonie.tjiang11.tcrunch.models.Response;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toniebalonie.tjiang11.tcrunch.models.Ticket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TeacherTicketDetailActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private ResponseListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mDatabaseReferenceResponses;
    private DatabaseReference mDatabaseReference;

    private TextView responsesText;

    private ArrayList<Response> responseList;

    private String question;
    private String questionDate;

    private String ticketId;
    private String classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ticket_detail);
        Bundle bundle = getIntent().getExtras();
        ticketId = (String) bundle.get("ticket_id");
        question = (String) bundle.get("question");
        long startTime = (long) bundle.get("start_time");
        classId = (String) bundle.get("class_id");
        className = (String) bundle.get("class_name");

        getSupportActionBar().setTitle(className);

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
        questionDate = formatter.format(date);
        startTimeText.setText(questionDate);
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

        if (id == R.id.email_ticket_data) {
            exportTicketToCSV();
        }

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

    private void exportTicketToCSV() {
        CSVWriter csvWriter;
        verifyStoragePermissions(this);
        try {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "TicketData.csv";
            String filePath = baseDir + File.separator + fileName;
            File ticketDataFile = new File(filePath);
            csvWriter = new CSVWriter(new FileWriter(filePath));
            String[] classInfo = { "Class", className };
            String[] questionInfo = { "Question Text", question };
            String[] launchDate = { "Launch Time", questionDate };
            String[] numResponses = { "# Responses", Integer.toString(responseList.size()) };
            csvWriter.writeNext(classInfo);
            csvWriter.writeNext(questionInfo);
            csvWriter.writeNext(launchDate);
            csvWriter.writeNext(numResponses);
            csvWriter.writeNext(new String[] {});
            String[] headers = {"USER", "RESPONSE"};
            csvWriter.writeNext(headers);
            for (Response r : responseList) {
                String[] data = { r.getAuthor(), r.getResponse() };
                csvWriter.writeNext(data);
            }
            csvWriter.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/csv");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "tjiang11@jhu.edu");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tcrunch: Your ticket data");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Your ticket data is attached.");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ticketDataFile));

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        } catch (IOException e) {
            e.printStackTrace();
            Log.w("TAG", e.toString());
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
