package com.example.tjiang11.tcrunch;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tjiang11.tcrunch.models.Classroom;
import com.example.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashSet;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.example.tjiang11.tcrunch.LoginActivity.PREFS_NAME;


public class StudentTicketListActivity extends AppCompatActivity implements ItemClickListener {

    private SharedPreferences sharedPrefs;

    private DrawerLayout mDrawerLayout;

    private RecyclerView mRecyclerView;
    private SectionedTicketListAdapter mSectionedTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

    private Section answered;
    private Section unanswered;

    private DatabaseReference mDatabaseReference;
    private Query mDatabaseReferenceTickets;
    private Query mDatabaseReferenceClasses;
    private Query mDatabaseReferenceUserClasses;
    private Query mDatabaseReferenceStudentAnsweredTickets;
    private ValueEventListener mValueEventListener;
    private FirebaseInstanceId mFirebaseInstanceId;

    private HashSet<String> hasAnswered;

    private ArrayList<Ticket> answeredTickets;
    private ArrayList<Ticket> unansweredTickets;

    private ArrayList<String> classList;

    private StudentTicketListActivity stla = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!sharedPrefs.contains("student_name")) {
            DialogFragment createNameDialog = new StudentCreateNameDialog();
            createNameDialog.show(getFragmentManager(), "create name");
        }
        setContentView(R.layout.activity_student_ticket_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.student_toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.student_ticket_list_recycler_view);
        answeredTickets = new ArrayList<Ticket>();
        unansweredTickets = new ArrayList<Ticket>();
        hasAnswered = new HashSet<String>();
        mSectionedTicketListAdapter = new SectionedTicketListAdapter();
        answered = new TicketSection("ANSWERED", answeredTickets, this);
        unanswered = new TicketSection("NOT ANSWERED", unansweredTickets, this);
        mSectionedTicketListAdapter.addSection(unanswered);
        mSectionedTicketListAdapter.addSection(answered);
        mRecyclerView.setAdapter(mSectionedTicketListAdapter);
        mTicketListLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mTicketListLayoutManager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.student_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        answered.setVisible(false);
        unanswered.setVisible(false);

        mFirebaseInstanceId = FirebaseInstanceId.getInstance();

//        mValueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                answeredTickets.clear();
//                unansweredTickets.clear();
//                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
//                    Classroom cr = classSnapshot.getValue(Classroom.class);
//                    Log.i("CLASS", cr.toString());
//                    String classId = cr.getId();
//                    mDatabaseReferenceTickets.orderByKey().equalTo(classId).addListenerForSingleValueEvent(
//                            new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot classSnapshot2 : dataSnapshot.getChildren()) {
//                                        for (DataSnapshot ticketSnapshot : classSnapshot2.getChildren()) {
//                                            Ticket ticket = ticketSnapshot.getValue(Ticket.class);
//                                            Log.i("TICKET", ticket.toString());
//                                            Log.i("INFO", ticketSnapshot.toString());
//                                            if (!hasAnswered.contains(ticket)) {
//                                                unansweredTickets.add(ticket);
//                                            } else {
//                                                answeredTickets.add(ticket);
//                                            }
//                                            if (answeredTickets.size() == 0) {
//                                                answered.setVisible(false);
//                                            } else {
//                                                answered.setVisible(true);
//                                            }
//                                            if (unansweredTickets.size() == 0) {
//                                                unanswered.setVisible(false);
//                                            } else {
//                                                unanswered.setVisible(true);
//                                            }
//                                            mSectionedTicketListAdapter.notifyDataSetChanged();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    Log.w("TAG", "loadTickets:onCancelled");
//                                }
//                            }
//                    );
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("TAG", "loadClasses:onCancelled");
//            }
//        };

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hasAnswered.clear();
                answeredTickets.clear();
                unansweredTickets.clear();
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    Log.i("ASAS", ticketSnapshot.toString());
                    hasAnswered.add(ticketSnapshot.getValue().toString());
                }

                mDatabaseReferenceUserClasses.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                            Classroom cr = classSnapshot.getValue(Classroom.class);
                            Log.i("CLASS", cr.toString());
                            String classId = cr.getId();
                            mDatabaseReferenceTickets.orderByKey().equalTo(classId).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot classSnapshot2 : dataSnapshot.getChildren()) {
                                                for (DataSnapshot ticketSnapshot : classSnapshot2.getChildren()) {
                                                    Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                                                    Log.i("TICKET", ticket.toString());
                                                    Log.i("INFO", ticketSnapshot.toString());
                                                    if (!hasAnswered.contains(ticket.getId())) {
                                                        unansweredTickets.add(ticket);
                                                    } else {
                                                        answeredTickets.add(ticket);
                                                    }
                                                    if (answeredTickets.size() == 0) {
                                                        answered.setVisible(false);
                                                    } else {
                                                        answered.setVisible(true);
                                                    }
                                                    if (unansweredTickets.size() == 0) {
                                                        unanswered.setVisible(false);
                                                    } else {
                                                        unanswered.setVisible(true);
                                                    }
                                                    mSectionedTicketListAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w("TAG", "loadTickets:onCancelled");
                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("WARN", "mValueEventListener:onCancelled");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadClasses:onCancelled");
            }
        };

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferenceTickets = mDatabaseReference.child("tickets");
        mDatabaseReferenceClasses = mDatabaseReference.child("classes");
        mDatabaseReferenceUserClasses = mDatabaseReference.child("students").child(mFirebaseInstanceId.getId());
        //mDatabaseReferenceUserClasses.addValueEventListener(mValueEventListener);

        mDatabaseReferenceStudentAnsweredTickets = mDatabaseReference.child("answered").child(mFirebaseInstanceId.getId());
        mDatabaseReferenceStudentAnsweredTickets.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onClick(View view, int position, String type) {
        Ticket ticket = null;
        int index = determineIndex(position);
        Log.i("position", Integer.toString(index));
        Log.i("type", type);
        switch (type) {
            case "answered":
                ticket = answeredTickets.get(index);
                break;
            case "not answered":
                ticket = unansweredTickets.get(index);
                break;
        }
        if (ticket != null) {
            Intent intent = new Intent(this, SubmitResponseActivity.class);
            intent.putExtra("question", ticket.getQuestion());
            intent.putExtra("start_time", ticket.getStartTime());
            intent.putExtra("end_time", ticket.getEndTime());
            intent.putExtra("ticket_id", ticket.getId());
            startActivity(intent);
        } else {
            Log.e("ERR", "Could not find ticket type");
        }
    }

    public int determineIndex(int position) {
        if (unansweredTickets.size() > 0 && position < unansweredTickets.size() + 1) {
            return position - 1;
        } else if (unansweredTickets.size() > 0 && position >= unansweredTickets.size() + 1) {
            return position - unansweredTickets.size() - 2;
        } else {
            return position - 1;
        }
    }

    @Override
    public void onBackPressed() {
        minimizeApp();
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.log_out) {
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putBoolean("student_logged_in", false);
            sharedPrefsEditor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.add_class) {
            DialogFragment addClassDialog = new StudentAddClassDialog();
            addClassDialog.show(getFragmentManager(), "add class");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doNewClassDialogPositiveClick(String classCode) {
        Query classToAdd = mDatabaseReferenceClasses.orderByChild("courseCode").equalTo(classCode).limitToFirst(1);
        classToAdd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = 0;
                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    size++;
                    Classroom classToAdd = classSnapshot.getValue(Classroom.class);
                    DatabaseReference addClass = mDatabaseReference.child("students")
                            .child(mFirebaseInstanceId.getId()).child(classToAdd.getId());
                    addClass.setValue(classToAdd);
                }
                if (size == 0) {
                    Toast.makeText(stla, "Could not find a matching class.", Toast.LENGTH_SHORT).show();
                }

                //DatabaseReference mDatabaseReferenceStudent = mDatabaseReference.child("students").child(mFirebaseInstanceId.getId());
                //DatabaseReference addClassRef = mDatabaseReferenceStudent.push();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error", "doNewDialogPositiveClick:onCancelled");
            }
        });
    }

    public void doCreateNameDialogClick(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("student_name", name);
        editor.apply();
    }
}
