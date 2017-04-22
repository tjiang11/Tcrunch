package com.toniebalonie.tjiang11.tcrunch;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.models.Classroom;
import com.toniebalonie.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.toniebalonie.tjiang11.tcrunch.LoginActivity.PREFS_NAME;

public class TeacherTicketListActivity extends AppCompatActivity implements
        ItemClickListener {

    private static final String TAG = TeacherTicketListActivity.class.getName();

    private SharedPreferences sharedPrefs;

    private RecyclerView mTicketListRecyclerView;
    private SectionedTicketListAdapter mSectionedTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

    private Section upcoming;
    private Section launched;

    private DatabaseReference mDatabaseReference;
    private Query mDatabaseReferenceTickets;
    private ValueEventListener mValueEventListener;

    private Query mDatabaseReferenceClasses;
    private ValueEventListener mClassesValueEventListener;
    private ValueEventListener mClassesSingleValueEventListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<Ticket> upcomingTickets;
    private ArrayList<Ticket> launchedTickets;

    private String currentClassName;
    private Classroom currentClass;

    private DrawerLayout mDrawerLayout;
    private NavigationView classListView;
    private ArrayList<String> classList;

    private HashMap<String, Classroom> classMap;

    private FloatingActionButton fab;

    private RelativeLayout loadingIndicator;

    TextView noClassText;
    TextView noTicketText;
    TextView classDeletedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TeacherTicketListActivity ttla = this;
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, ttla.getCurrentClass().getName());
                Log.i(TAG, ttla.getCurrentClass().toString());
                Intent intent = new Intent(view.getContext(), CreateTicketActivity.class);
                intent.putExtra("classId", ttla.getCurrentClass().getId());
                intent.putExtra("className", ttla.getCurrentClass().getName());
                intent.putExtra("classes", classList);
                intent.putExtra("classMap", classMap);
                startActivity(intent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        classList = new ArrayList<String>();
        classListView = (NavigationView) findViewById(R.id.nav_view);
        View header = classListView.getHeaderView(0);
        TextView userEmail = (TextView) header.findViewById(R.id.user_info);
        userEmail.setText(sharedPrefs.getString("email", "No email specified"));
        classListView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.no_class_item) {
                    return false;
                }
                Log.i(TAG, "Navigation item selected");
                classDeletedText.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                currentClassName = item.getTitle().toString();
                currentClass = classMap.get(currentClassName);
                mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
                mDrawerLayout.closeDrawers();
                getSupportActionBar().setTitle(currentClassName);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        mTicketListRecyclerView = (RecyclerView) findViewById(R.id.ticket_list_recycler_view);
        mTicketListRecyclerView.setHasFixedSize(true);

        loadingIndicator = (RelativeLayout) findViewById(R.id.loadingPanelTeacher);

        mTicketListLayoutManager = new LinearLayoutManager(this);
        mTicketListRecyclerView.setLayoutManager(mTicketListLayoutManager);

        classMap = new HashMap<String, Classroom>();

        launchedTickets = new ArrayList<Ticket>();
        upcomingTickets = new ArrayList<Ticket>();
        mSectionedTicketListAdapter = new SectionedTicketListAdapter();
        upcoming = new TicketSection("UPCOMING", upcomingTickets, this);
        launched = new TicketSection("LAUNCHED", launchedTickets, this);
        upcoming.setVisible(false);
        launched.setVisible(false);

        mSectionedTicketListAdapter.addSection(upcoming);
        mSectionedTicketListAdapter.addSection(launched);

        noClassText = (TextView) findViewById(R.id.no_class_view);
        noTicketText = (TextView) findViewById(R.id.no_ticket_view);
        classDeletedText = (TextView) findViewById(R.id.class_deleted_view);

        mTicketListRecyclerView.setAdapter(mSectionedTicketListAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                launchedTickets.clear();
                upcomingTickets.clear();
                for (DataSnapshot ticketSnapshot: dataSnapshot.getChildren()) {
                    Ticket mTicket = ticketSnapshot.getValue(Ticket.class);
                    if (mTicket.getStartTime() <= System.currentTimeMillis()) {
                        launchedTickets.add(mTicket);
                    } else {
                        upcomingTickets.add(mTicket);
                    }
                }
                if (launchedTickets.size() == 0) {
                    launched.setVisible(false);
                } else {
                    launched.setVisible(true);
                }
                if (upcomingTickets.size() == 0) {
                    upcoming.setVisible(false);
                } else {
                    upcoming.setVisible(true);
                }
                if (launchedTickets.size() == 0 && upcomingTickets.size() == 0 && classList.size() != 0) {
                    noTicketText.setVisibility(View.VISIBLE);
                    noClassText.setVisibility(View.GONE);
                } else {
                    noTicketText.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
                Collections.sort(upcomingTickets, Ticket.TicketTimeComparator);
                Collections.sort(launchedTickets, Ticket.TicketTimeComparator);
                Collections.reverse(launchedTickets);
                loadingIndicator.setVisibility(View.GONE);
                mSectionedTicketListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mClassesSingleValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "monkey");
                for (DataSnapshot classSnapshot: dataSnapshot.getChildren()) {
                    Log.i(TAG, "single value event");
                    Classroom cr = classSnapshot.getValue(Classroom.class);
                    //condense
                    currentClass = cr;
                    getSupportActionBar().setTitle(currentClass.getName());
                    mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                    mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
                    noClassText.setVisibility(View.GONE);
                    return;
                }
                noClassText.setVisibility(View.VISIBLE);
                noTicketText.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                currentClass = null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "classEventSingle:onCancelled", databaseError.toException());
            }
        };

        mClassesValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean classesExist = false;
                classMap.clear();
                classList.clear();
                classListView.getMenu().clear();
                for (DataSnapshot classSnapshot: dataSnapshot.getChildren()) {
                    Classroom cr = classSnapshot.getValue(Classroom.class);
                    classListView.getMenu().add(cr.getName());
                    classList.add(cr.getName());
                    classMap.put(cr.getName(), cr);
                    classesExist = true;
                }
                if (classesExist) {
                    noClassText.setVisibility(View.GONE);
                } else {
                    noClassText.setVisibility(View.VISIBLE);
                    classListView.getMenu().add(0, R.id.no_class_item, 0, "No classes yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "classEvent:onCancelled", databaseError.toException());
            }
        };
        mDatabaseReferenceClasses = mDatabaseReference.child("teachers").child(mAuth.getCurrentUser().getUid());
        mDatabaseReferenceClasses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mDatabaseReferenceClasses.addListenerForSingleValueEvent(mClassesSingleValueEventListener);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                    noClassText.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
                mDatabaseReferenceClasses.addValueEventListener(mClassesValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            minimizeApp();
        }
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
        int id = item.getItemId();

        if (id == R.id.log_out) {
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putBoolean("teacher_logged_in", false);
            sharedPrefsEditor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.add_class) {
            DialogFragment addClassDialog = new AddClassDialog();
            addClassDialog.show(getFragmentManager(), "add class");
            return true;
        }

        if (id == R.id.delete_class) {
            if (currentClass == null) {
                Toast.makeText(this, "You must select a class first to perform this action.", Toast.LENGTH_LONG).show();
            } else {
                final TeacherTicketListActivity parent = this;
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure you want to delete " + this.currentClass.getName() + "?")
                        .setMessage("This action cannot be undone. All tickets and responses of this class will be deleted.")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabaseReferenceClasses.getRef().child(currentClass.getId()).removeValue();
                                mDatabaseReference.child("classes").child(currentClass.getId()).removeValue();
                                mDatabaseReference.child("tickets").child(currentClass.getId()).removeValue();
                                getSupportActionBar().setTitle("Tcrunch");
                                currentClass = null;
                                fab.setVisibility(View.GONE);
                                noTicketText.setVisibility(View.GONE);
                                if (classList.size() > 1)
                                classDeletedText.setVisibility(View.VISIBLE);
                                Toast.makeText(parent, "Class deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        }

        if (id == R.id.info) {
            DialogFragment infoDialog = new TeacherInfoDialog();
            infoDialog.show(getFragmentManager(), "teacher info");
            return true;
        }

        if (id == R.id.class_code) {
            if (currentClass == null) {
                Toast.makeText(this, "You must select a class first to perform this action.", Toast.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Class Code for " + this.currentClass.getName())
                        .setMessage(this.currentClass.getCourseCode())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position, String type) {
        Ticket ticket = null;
        int index = determineIndex(position);
        Intent intent;
        switch (type) {
            case "launched":
                ticket = launchedTickets.get(index);
                intent = new Intent(this, TeacherTicketDetailActivity.class);
                intent.putExtra("question", ticket.getQuestion());
                intent.putExtra("start_time", ticket.getStartTime());
                intent.putExtra("end_time", ticket.getEndTime());
                intent.putExtra("ticket_id", ticket.getId());
                intent.putExtra("anonymous", ticket.isAnonymous());
                intent.putStringArrayListExtra("answer_choices", new ArrayList<>(ticket.getAnswerChoices()));
                intent.putExtra("class_id", getCurrentClass().getId());
                intent.putExtra("class_name", getCurrentClass().getName());
                startActivity(intent);
                break;
            case "upcoming":
                ticket = upcomingTickets.get(index);
                intent = new Intent(this, CreateTicketActivity.class);
                intent.putExtra("question", ticket.getQuestion());
                intent.putExtra("start_time", ticket.getStartTime());
                intent.putExtra("end_time", ticket.getEndTime());
                intent.putExtra("ticket_id", ticket.getId());
                intent.putExtra("anonymous", ticket.isAnonymous());
                intent.putStringArrayListExtra("answer_choices", new ArrayList<>(ticket.getAnswerChoices()));
                intent.putExtra("class_id", getCurrentClass().getId());
                intent.putExtra("className", getCurrentClass().getName());
                intent.putExtra("classes", classList);
                intent.putExtra("classMap", classMap);
                intent.putExtra("is_editing", true);
                startActivityForResult(intent, 0);
                break;
        }
        if (ticket == null) {
            Log.e("ERR", "Could not find ticket type");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String updatedClass = data.getStringExtra("class_name");
                currentClass = classMap.get(updatedClass);
                getSupportActionBar().setTitle(updatedClass);
            }
        }
    }

    public int determineIndex(int position) {
        if (upcomingTickets.size() > 0 && position < upcomingTickets.size() + 1) {
            return position - 1;
        } else if (upcomingTickets.size() > 0 && position >= upcomingTickets.size() + 1) {
            return position - upcomingTickets.size() - 2;
        } else {
            return position - 1;
        }
    }

    public void doNewClassDialogPositiveClick(final String className, final String classCode) {
        for (String c : classList) {
            if (className.equals(c)) {
                Toast.makeText(this, "You already have a class with that name.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        final TeacherTicketListActivity ttla = this;
        Query classRefByCode = mDatabaseReference.child("classes").orderByChild("courseCode").equalTo(classCode);
        final Query classRef = mDatabaseReference.child("classes").orderByChild("name").equalTo(className);
        classRefByCode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Toast.makeText(ttla, "That class code is taken.", Toast.LENGTH_SHORT).show();
                } else {
                    classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Toast.makeText(ttla, "That class name is taken.", Toast.LENGTH_SHORT).show();
                            } else {
                                DatabaseReference newClassRef = mDatabaseReference.child("teachers").child(mAuth.getCurrentUser().getUid()).push();
                                String newClassId = newClassRef.getKey();
                                String courseCode = classCode;

                                Classroom newClassroom = new Classroom(newClassId, className, courseCode);
                                newClassRef.setValue(newClassroom);

                                DatabaseReference newClassRefClasses = mDatabaseReference.child("classes").child(newClassId);
                                newClassRefClasses.setValue(newClassroom);

                                classListView.getMenu().add(className);
                                currentClass = newClassroom;
                                Log.i(TAG, currentClass.toString());
                                getSupportActionBar().setTitle(currentClass.getName());
                                fab.setVisibility(View.VISIBLE);
                                mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                                mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Error creating class: " + databaseError.toString());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Error creating class: " + databaseError.toString());
            }
        });
    }

    public Classroom getCurrentClass() {
        return this.currentClass;
    }
}
