package com.toniebalonie.tjiang11.tcrunch.activities;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.toniebalonie.tjiang11.tcrunch.interfaces.ItemClickListener;
import com.toniebalonie.tjiang11.tcrunch.services.PollingService;
import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.adapters.SectionedTicketListAdapter;
import com.toniebalonie.tjiang11.tcrunch.fragments.StudentAddClassDialog;
import com.toniebalonie.tjiang11.tcrunch.fragments.StudentCreateNameDialog;
import com.toniebalonie.tjiang11.tcrunch.fragments.StudentInfoDialog;
import com.toniebalonie.tjiang11.tcrunch.adapters.TicketSection;
import com.toniebalonie.tjiang11.tcrunch.models.AnsweredTicketActivity;
import com.toniebalonie.tjiang11.tcrunch.models.Classroom;
import com.toniebalonie.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.toniebalonie.tjiang11.tcrunch.activities.LoginActivity.PREFS_NAME;


public class StudentTicketListActivity extends AppCompatActivity implements ItemClickListener {

    private static final String TAG = StudentTicketListActivity.class.getName();

    private SharedPreferences sharedPrefs;

    /** Recycler View containing tickets displayed. */
    private RecyclerView mRecyclerView;
    private SectionedTicketListAdapter mSectionedTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

    /** Sections representing answered tickets and unanswered tickets
     * in the sectioned recycler view. */
    private Section answered;
    private Section unanswered;

    /** The student's diplay name, visible in the navigation drawer header. */
    private TextView userDisplayName;
    /** NavigationView containing all classes the student is enrolled in. */
    private NavigationView classListView;
    /** DrawerLayout containing side navigation window */
    private DrawerLayout mDrawerLayout;

    /** TextView that is visible when no tickets are available. */
    private TextView noTicketText;

    /** Spinning circle indicating that content is loading. */
    private RelativeLayout loadingIndicator;

    /** Reference to Firebase */
    private DatabaseReference mDatabaseReference;
    /** Reference to Tickets table in Firebase, organized by class ID*/
    private DatabaseReference mDatabaseReferenceTickets;
    /** Reference to Classes table in Firebase containing class information */
    private DatabaseReference mDatabaseReferenceClasses;
    /** Reference to Students --> Student ID in Firebase (this student's classes) */
    private DatabaseReference mDatabaseReferenceUserClasses;
    /** Reference to student's answered tickets in Firebase */
    private DatabaseReference mDatabaseReferenceStudentAnsweredTickets;

    private ValueEventListener mValueEventListener;
    private ValueEventListener mTicketChangeListener;

    /** Firebase Instance ID used to identify Student */
    private FirebaseInstanceId mFirebaseInstanceId;

    /** Set of all tickets answered by the user */
    private HashSet<String> hasAnswered;

    /** List of displayed, answered tickets */
    private ArrayList<Ticket> answeredTickets;
    /** List of displayed, unanswered tickets */
    private ArrayList<Ticket> unansweredTickets;

    /** Map from class name to class object */
    private HashMap<String, Classroom> classMap;

    /** Currently selected class, a value of null means all classes are shown */
    private Classroom currentClass = null;
    boolean showingAll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Task for polling notifications
        Task task = new PeriodicTask.Builder()
                .setService(PollingService.class)
                .setPeriod(30)
                .setFlex(10)
                .setTag("PollingService")
                .setPersisted(true)
                .build();

        GcmNetworkManager.getInstance(this).schedule(task);

        setTitle("All classes");
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //Only launch tutorial on first time
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirstStart = sharedPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {

                    // Launch app intro
                    Intent i = new Intent(StudentTicketListActivity.this, StudentIntroActivity.class);
                    startActivity(i);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                }
            }
        });

        t.start();

        //Make user set name on first login
        if (!sharedPrefs.contains("student_name")) {
            DialogFragment createNameDialog = new StudentCreateNameDialog();
            createNameDialog.setCancelable(false);
            createNameDialog.show(getFragmentManager(), "create name");
        }
        setContentView(R.layout.activity_student_ticket_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.student_toolbar);
        setSupportActionBar(toolbar);
        setTimeUpdater();

        noTicketText = (TextView) findViewById(R.id.no_ticket_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.student_ticket_list_recycler_view);
        loadingIndicator = (RelativeLayout) findViewById(R.id.loadingPanel);
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


        classMap = new HashMap<String, Classroom>();
        classListView = (NavigationView) findViewById(R.id.nav_view);
        View header = classListView.getHeaderView(0);
        userDisplayName = (TextView) header.findViewById(R.id.user_info);
        userDisplayName.setText(sharedPrefs.getString("student_name", "No name specified"));

        // When class is seleceted in the navigation menu, show all tickets for that class.
        classListView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                answered.setVisible(false);
                unanswered.setVisible(false);
                loadingIndicator.setVisibility(View.VISIBLE);
                if (item.toString().equals("Show All")) {
                    // Set currentClass to null to represent no specific class is seleceted,
                    // and show all tickets for all classes.
                    currentClass = null;
                    getSupportActionBar().setTitle("All classes");
                    showingAll = true;
                } else {
                    showingAll = false;
                    currentClass = classMap.get(item.toString());
                    getSupportActionBar().setTitle(currentClass.getName());
                }
                mDatabaseReferenceStudentAnsweredTickets.addListenerForSingleValueEvent(mValueEventListener);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.student_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        answered.setVisible(false);
        unanswered.setVisible(false);

        mFirebaseInstanceId = FirebaseInstanceId.getInstance();

        // Listener attached to a single class that when activated, loads
        // and displays tickets for that class.
        mTicketChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!showingAll) {
                    answeredTickets.clear();
                    unansweredTickets.clear();
                }
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                    if (ticket.getStartTime() < System.currentTimeMillis()) {
                        if (!hasAnswered.contains(ticket.getId())) {
                            unansweredTickets.add(ticket);
                        } else {
                            answeredTickets.add(ticket);
                        }
                    }
                    mSectionedTicketListAdapter.notifyDataSetChanged();
                }

                if (answeredTickets.size() == 0) {
                    answered.setVisible(false);
                } else {
                    answered.setVisible(true);
                    noTicketText.setVisibility(View.GONE);
                }
                if (unansweredTickets.size() == 0) {
                    unanswered.setVisible(false);
                } else {
                    unanswered.setVisible(true);
                    noTicketText.setVisibility(View.GONE);
                }
                loadingIndicator.setVisibility(View.GONE);
                if (answeredTickets.size() == 0 && unansweredTickets.size() == 0) {
                    noTicketText.setVisibility(View.VISIBLE);
                }
                Collections.sort(unansweredTickets, Ticket.TicketTimeComparator);
                Collections.sort(answeredTickets, Ticket.TicketTimeComparator);
                Collections.reverse(answeredTickets);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadTickets:onCancelled");
            }
        };

        // Listener that when activated loads, filters, and displays tickets
        // based on class selected. Should be attached to answered tickets column for user.
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reset map containing what tickets the user has already answered.
                hasAnswered.clear();

                // Record every ticket that the user has answered.
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    hasAnswered.add(ticketSnapshot.getValue().toString());
                }

                mDatabaseReferenceUserClasses.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        classMap.clear();
                        answeredTickets.clear();
                        unansweredTickets.clear();
                        classListView.getMenu().clear();
                        if (dataSnapshot.getValue() == null) {
                            noTicketText.setVisibility(View.VISIBLE);
                            loadingIndicator.setVisibility(View.GONE);
                        }

                        // For every class that the student is enrolled in, add to the navigation menu
                        // and if the class is selected (or showing all) then display its tickets.
                        for (final DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                            Classroom newClass = classSnapshot.getValue(Classroom.class);
                            classListView.getMenu().add(newClass.getName());
                            classMap.put(newClass.getName(), newClass);
                            String classId = newClass.getId();

                            if (currentClass == null || classId.equals(currentClass.getId())) {
                                mDatabaseReferenceTickets.child(classId)
                                        .addValueEventListener(mTicketChangeListener);
                            }
                        }

                        classListView.getMenu().add("Show All");
                        mSectionedTicketListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "mValueEventListener:onCancelled");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadClasses:onCancelled");
            }
        };

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferenceTickets = mDatabaseReference.child("tickets");
        mDatabaseReferenceClasses = mDatabaseReference.child("classes");
        mDatabaseReferenceUserClasses = mDatabaseReference.child("students").child(mFirebaseInstanceId.getId());
        mDatabaseReferenceStudentAnsweredTickets = mDatabaseReference.child("answered").child(mFirebaseInstanceId.getId());
        mDatabaseReferenceStudentAnsweredTickets.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onClick(View view, int position, String type) {
        Ticket ticket = null;
        int index = determineIndex(position);
        switch (type) {
            case "answered":
                ticket = answeredTickets.get(index);
                break;
            case "not answered":
                ticket = unansweredTickets.get(index);
                break;
        }
        if (ticket != null && type.equals("not answered")) {
            // On clicking an unanswered ticket, go to submit response activity.
            Intent intent = new Intent(this, SubmitResponseActivity.class);
            intent.putExtra("question", ticket.getQuestion());
            intent.putExtra("start_time", ticket.getStartTime());
            intent.putExtra("end_time", ticket.getEndTime());
            intent.putExtra("ticket_id", ticket.getId());
            intent.putExtra("anonymous", ticket.isAnonymous());
            intent.putStringArrayListExtra("answer_choices", new ArrayList<>(ticket.getAnswerChoices()));
            startActivity(intent);
        } else if (ticket != null && type.equals("answered")) {
            // On clicking an answered ticket, view the student's response.
            Intent intent = new Intent(this, AnsweredTicketActivity.class);
            intent.putExtra("question", ticket.getQuestion());
            intent.putExtra("ticket_id", ticket.getId());
            startActivity(intent);
        }
    }

    /**
     * Determine the local index of a ticket given its position in the entire sectioned recycler view.
     * @param position position of item within sectioned recycler view
     * @return local index within respective array list.
     */
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
        getMenuInflater().inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.log_out) {
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putBoolean("student_logged_in", false);
            sharedPrefsEditor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            GcmNetworkManager.getInstance(this).cancelAllTasks(PollingService.class);
            finish();
            return true;
        }

        if (id == R.id.add_class) {
            DialogFragment addClassDialog = new StudentAddClassDialog();
            addClassDialog.show(getFragmentManager(), "add class");
            return true;
        }

        if (id == R.id.delete_class) {
            if (currentClass == null) {
                Toast.makeText(this, "You must select a class first to perform this action.", Toast.LENGTH_LONG).show();
            } else {
                final StudentTicketListActivity parent = this;
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure you want to leave " + this.currentClass.getName() + "?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabaseReference.child("students").child(mFirebaseInstanceId.getId())
                                        .child(currentClass.getId()).removeValue();
                                answered.setVisible(false);
                                unanswered.setVisible(false);
                                loadingIndicator.setVisibility(View.VISIBLE);
                                Toast.makeText(parent, "You left " + currentClass.getName(), Toast.LENGTH_SHORT).show();
                                currentClass = null;
                                showingAll = true;
                                mDatabaseReferenceStudentAnsweredTickets.addListenerForSingleValueEvent(mValueEventListener);
                                getSupportActionBar().setTitle("All classes");
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        }

        if (id == R.id.FAQ) {
            DialogFragment infoDialog = new StudentInfoDialog();
            infoDialog.show(getFragmentManager(), "student info");
            return true;
        }

        if (id == R.id.edit_display_name) {
            DialogFragment createNameDialog = new StudentCreateNameDialog();
            createNameDialog.show(getFragmentManager(), "edit name");
        }

        return super.onOptionsItemSelected(item);
    }

    public void doNewClassDialogPositiveClick(String classCode, final AlertDialog d) {
        final boolean answeredVisible = answered.isVisible();
        final boolean unansweredVisible = unanswered.isVisible();
        answered.setVisible(false);
        unanswered.setVisible(false);
        noTicketText.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        Query classToAdd = mDatabaseReferenceClasses.orderByChild("courseCode").equalTo(classCode).limitToFirst(1);
        classToAdd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean newClassExists = false;
                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    newClassExists = true;

                    // Update class to student enrolled classes in Firebase
                    Classroom classToAdd = classSnapshot.getValue(Classroom.class);
                    DatabaseReference addClass = mDatabaseReference.child("students")
                            .child(mFirebaseInstanceId.getId()).child(classToAdd.getId());
                    addClass.setValue(classToAdd);

                    // Front-end changes
                    getSupportActionBar().setTitle(currentClass.getName());

                    // Reload tickets
                    currentClass = classToAdd;

                    DatabaseReference answeredRef = mDatabaseReference.child("answered").child(mFirebaseInstanceId.getId());
                    answeredRef.addListenerForSingleValueEvent(mValueEventListener);

                    // Display message saying what class the user joined by what teacher
                    String teacherString = "";
                    if (classToAdd.getTeacher() != null) {
                        teacherString = " by " + classToAdd.getTeacher();
                    }
                    Toast.makeText(StudentTicketListActivity.this, "Joined class " + classToAdd.getName() + teacherString, Toast.LENGTH_LONG).show();

                    // Dismiss the join class dialog
                    d.dismiss();
                }
                if (!newClassExists) {
                    if (answeredVisible) { answered.setVisible(true); }
                    if (unansweredVisible) { unanswered.setVisible(true); }
                    Toast.makeText(StudentTicketListActivity.this, "Could not find a matching class.", Toast.LENGTH_SHORT).show();
                    loadingIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error", "doNewDialogPositiveClick:onCancelled");
            }
        });
    }

    /**
     * Used to update release times of tickets approximately once a minute.
     */
    private void setTimeUpdater() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            mSectionedTicketListAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 60*1000);  // interval of one minute

    }

    /**
     * Handle editing of user display name.
     * @param name New name for student
     */
    public void doCreateNameDialogClick(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("student_name", name);
        editor.apply();
        userDisplayName.setText(name);
        Toast.makeText(this, "Your name has been set to " + name, Toast.LENGTH_SHORT).show();
    }
}
