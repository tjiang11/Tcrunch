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

public class TeacherTicketListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemClickListener {

    private SharedPreferences sharedPrefs;

    private RecyclerView mTicketListRecyclerView;
    //private TicketListAdapter mTicketListAdapter;
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
    private ArrayList<Ticket> ticketList;

    private String currentClassName;
    private Classroom currentClass;

    private DrawerLayout mDrawerLayout;
    private NavigationView classListView;
    private ArrayAdapter classListViewAdapter;
    private ArrayList<String> classList;

    private HashMap<String, Classroom> classMap;

    private FloatingActionButton fab;

    TextView noClassText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TeacherTicketListActivity ttla = this;
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String courseCode = Long.toString(System.currentTimeMillis(), 36);
        currentClass = new Classroom("default", "my class", courseCode);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG
//                        .setAction("Action", null).show();
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
        classListViewAdapter = new ArrayAdapter<String>(this, R.layout.class_list_item, classList);
        classListView = (NavigationView) findViewById(R.id.nav_view);
        classListView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fab.setVisibility(View.VISIBLE);
                currentClassName = item.getTitle().toString();
                currentClass = classMap.get(currentClassName);
                mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                //mDatabaseReferenceTickets = mDatabaseReference.child("classes").child(currentClassId).child("tickets");
                mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
                mDrawerLayout.closeDrawers();
                getSupportActionBar().setTitle(currentClassName);
                Log.i("MENU", item.getTitle().toString());

                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        mTicketListRecyclerView = (RecyclerView) findViewById(R.id.ticket_list_recycler_view);
        mTicketListRecyclerView.setHasFixedSize(true);

        mTicketListLayoutManager = new LinearLayoutManager(this);
        mTicketListRecyclerView.setLayoutManager(mTicketListLayoutManager);

        ArrayList<String> empty = new ArrayList<String>();
//        testList[0] = new Ticket("What is an eigenvector? How is an eigenvector useful? Describe how you find an eigenvetor. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues.", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
//        testList[1] = new Ticket("question1", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
//        testList[2] = new Ticket("question2", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
//        testList[3] = new Ticket("question3", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
//        testList[4] = new Ticket("question4", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");

        classMap = new HashMap<String, Classroom>();

        ticketList = new ArrayList<Ticket>();
        launchedTickets = new ArrayList<Ticket>();
        upcomingTickets = new ArrayList<Ticket>();
        //ticketList.add(new Ticket("question1", Ticket.QuestionType.FreeResponse, 0, 0, "class name"));
        mSectionedTicketListAdapter = new SectionedTicketListAdapter();
        upcoming = new TicketSection("UPCOMING", upcomingTickets, this);
        launched = new TicketSection("LAUNCHED", launchedTickets, this);
        upcoming.setVisible(false);
        launched.setVisible(false);

        mSectionedTicketListAdapter.addSection(upcoming);
        mSectionedTicketListAdapter.addSection(launched);

        noClassText = (TextView) findViewById(R.id.no_class_view);
        //mTicketListAdapter = new TicketListAdapter(ticketList);
        mTicketListRecyclerView.setAdapter(mSectionedTicketListAdapter);

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //ticketList.clear();
                launchedTickets.clear();
                upcomingTickets.clear();
                for (DataSnapshot ticketSnapshot: dataSnapshot.getChildren()) {
                    Ticket mTicket = ticketSnapshot.getValue(Ticket.class);
                    if (mTicket.getStartTime() < System.currentTimeMillis()) {
                        launchedTickets.add(mTicket);
                    } else {
                        upcomingTickets.add(mTicket);
                    }
                    //ticketList.add(mTicket);
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
                //Collections.sort(ticketList, Ticket.TicketTimeComparator);
                Collections.sort(upcomingTickets, Ticket.TicketTimeComparator);
                Collections.sort(launchedTickets, Ticket.TicketTimeComparator);
                Collections.reverse(launchedTickets);
                mSectionedTicketListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
        mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);


        mClassesSingleValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot classSnapshot: dataSnapshot.getChildren()) {
                    Classroom cr = classSnapshot.getValue(Classroom.class);
                    //condense
                    currentClass = cr;
                    getSupportActionBar().setTitle(currentClass.getName());
                    mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                    mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
                    noClassText.setVisibility(View.GONE);
                    return;
                }

                Log.i("TAG", "no class");
                noClassText.setVisibility(View.VISIBLE);
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
                    if (!classesExist) {
                        currentClass = cr;
                    }
                    classesExist = true;
                }
                if (classesExist) {
                    noClassText.setVisibility(View.GONE);
                } else {
                    noClassText.setVisibility(View.VISIBLE);
                }
                Log.i("cm", classMap.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "classEvent:onCancelled", databaseError.toException());
            }
        };
        mDatabaseReferenceClasses = mDatabaseReference.child("teachers").child(mAuth.getCurrentUser().getUid());
        mDatabaseReferenceClasses.addListenerForSingleValueEvent(mClassesSingleValueEventListener);
        mDatabaseReferenceClasses.addValueEventListener(mClassesValueEventListener);

        mAuth = FirebaseAuth.getInstance();

        Log.d("TeacherTicketList", mAuth.getCurrentUser().getEmail());

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view, int position, String type) {
        Ticket ticket = null;
        int index = determineIndex(position);
        Log.i("position", Integer.toString(index));
        Log.i("type", type);
        switch (type) {
            case "launched":
                ticket = launchedTickets.get(index);
                break;
            case "upcoming":
                ticket = upcomingTickets.get(index);
                break;
        }
        if (ticket != null) {
            Intent intent = new Intent(this, TeacherTicketDetailActivity.class);
            intent.putExtra("question", ticket.getQuestion());
            intent.putExtra("start_time", ticket.getStartTime());
            intent.putExtra("end_time", ticket.getEndTime());
            intent.putExtra("ticket_id", ticket.getId());
            intent.putExtra("class_id", getCurrentClass().getId());
            intent.putExtra("class_name", getCurrentClass().getName());
            startActivity(intent);
        } else {
            Log.e("ERR", "Could not find ticket type");
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

    public void doNewClassDialogPositiveClick(String className) {
        for (String c : classList) {
            if (className.equals(c)) {
                Toast.makeText(this, "A class with that name already exists.", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        DatabaseReference newClassRef = mDatabaseReference.child("teachers").child(mAuth.getCurrentUser().getUid()).push();
        String newClassId = newClassRef.getKey();
        String courseCode = Long.toString(System.currentTimeMillis(), 36);

        Classroom newClassroom = new Classroom(newClassId, className, courseCode);
        newClassRef.setValue(newClassroom);

        DatabaseReference newClassRefClasses = mDatabaseReference.child("classes").child(newClassId);
        newClassRefClasses.setValue(newClassroom);

        classListView.getMenu().add(className);
        currentClass = newClassroom;
        getSupportActionBar().setTitle(currentClass.getName());
        fab.setVisibility(View.VISIBLE);
        mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
        //mDatabaseReferenceTickets = mDatabaseReference.child("classes").child(currentClassId).child("tickets");
        mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
    }

    public String getCurrentClassName() {
        return this.currentClassName;
    }

    public Classroom getCurrentClass() {
        return this.currentClass;
    }

    public HashMap<String, Classroom> getClassMap() { return this.classMap; }
}
