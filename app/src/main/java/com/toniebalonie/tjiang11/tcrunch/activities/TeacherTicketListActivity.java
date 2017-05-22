package com.toniebalonie.tjiang11.tcrunch.activities;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.interfaces.ItemClickListener;
import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.adapters.SectionedTicketListAdapter;
import com.toniebalonie.tjiang11.tcrunch.fragments.TeacherAddClassDialog;
import com.toniebalonie.tjiang11.tcrunch.fragments.TeacherCreateNameDialog;
import com.toniebalonie.tjiang11.tcrunch.fragments.TeacherInfoDialog;
import com.toniebalonie.tjiang11.tcrunch.adapters.TicketSection;
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
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.toniebalonie.tjiang11.tcrunch.activities.LoginActivity.PREFS_NAME;

public class TeacherTicketListActivity extends AppCompatActivity implements
        ItemClickListener {

    private static final String TAG = TeacherTicketListActivity.class.getName();

    private SharedPreferences sharedPrefs;

    /** Recycler view containing tickets displayed */
    private RecyclerView mTicketListRecyclerView;
    private SectionedTicketListAdapter mSectionedTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

    /** Sections representing upcoming tickets and launched ticket
     * in the sectioned recycler view. */
    private Section upcoming;
    private Section launched;

    /** Reference to Firebase */
    private DatabaseReference mDatabaseReference;
    /** Reference to Tickets table in Firebase */
    private Query mDatabaseReferenceTickets;
    /** Database listener detecting changes to tickets table.
     *  Update the list of tickets displayed on change detected. */
    private ValueEventListener mTicketChangeListener;

    /** Reference to Classes table in Firebase */
    private Query mDatabaseReferenceClasses;

    /** Firebase Authentication. */
    private FirebaseAuth mAuth;

    /** Displayed upcoming tickets. */
    private ArrayList<Ticket> upcomingTickets;
    /** Displayed launched tickets. */
    private ArrayList<Ticket> launchedTickets;

    /** Name of selected class */
    private String currentClassName;
    /** Selected class */
    private Classroom currentClass;

    /** List of all classes created by the teacher. */
    private ArrayList<String> classList;

    /** Map from class names to the corresponding class objects. */
    private HashMap<String, Classroom> classMap;

    /** Drawer view. */
    private DrawerLayout mDrawerLayout;

    /** Side navigation window containing all the classes. */
    private NavigationView classListView;

    /** Button in the bottom right for creating new ticket. */
    private FloatingActionButton fab;

    /** Spinning circle indicating that content is loading. */
    private RelativeLayout loadingIndicator;

    /** Teacher's name displayed in the side navigation header. */
    private TextView userDisplayName;

    /** Text informing user that they have no classes. */
    private TextView noClassText;
    /** Text informing user that they have no tickets for the selected class. */
    private TextView noTicketText;
    /** Text informing user that they have just deleted the class. */
    private TextView classDeletedText;

    /** Request code for opening suggested questions activity. */
    public static final int SUGGESTED_QUESTION_REQUEST = 5;
    /** Request code for opening edit ticket activity. */
    public static final int EDIT_TICKET_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Only launch tutorial on first time
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirstStart = sharedPrefs.getBoolean("firstStartTeacher", true);

                if (isFirstStart) {

                    // Launch app intro
                    Intent i = new Intent(TeacherTicketListActivity.this, TeacherIntroActivity.class);
                    startActivity(i);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("firstStartTeacher", false);
                    editor.apply();
                }
            }
        });

        t.start();

        // Make user set name on first login
        if (!sharedPrefs.contains("teacher_name")) {
            DialogFragment createNameDialog = new TeacherCreateNameDialog();
            createNameDialog.setCancelable(false);
            createNameDialog.show(getFragmentManager(), "create name");
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTimeUpdater();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch new ticket activity
                Log.i(TAG, TeacherTicketListActivity.this.getCurrentClass().getName());
                Log.i(TAG, TeacherTicketListActivity.this.getCurrentClass().toString());
                Intent intent = new Intent(view.getContext(), CreateTicketActivity.class);
                intent.putExtra("classId", TeacherTicketListActivity.this.getCurrentClass().getId());
                intent.putExtra("className", TeacherTicketListActivity.this.getCurrentClass().getName());
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
        userDisplayName = (TextView) header.findViewById(R.id.user_info_top);
        userDisplayName.setText(sharedPrefs.getString("teacher_name", "Unidentified"));
        userEmail.setText(sharedPrefs.getString("email", "No email specified"));

        //On selecting a class, display all tickets for that class.
        classListView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.no_class_item) {
                    // No items present, other than text saying "No classes yet".
                    return false;
                }

                classDeletedText.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                currentClassName = item.getTitle().toString();
                currentClass = classMap.get(currentClassName);
                mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                mDatabaseReferenceTickets.addValueEventListener(mTicketChangeListener);
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

        userDisplayName.setText(sharedPrefs.getString("teacher_name", "No name specified"));

        // Load and display tickets for the currently selected class.
        mTicketChangeListener = new ValueEventListener() {
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

        // Reference to classes owned by the teacher
        mDatabaseReferenceClasses = mDatabaseReference.child("teachers").child(mAuth.getCurrentUser().getUid());
        // Sync classes in navigation menu with any changes
        mDatabaseReferenceClasses.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Load all classes owned by teacher into the side navigation menu.
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
                });

        // Initialize by picking a class and showing its tickets (fires only once on login).
        mDatabaseReferenceClasses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot classSnapshot: dataSnapshot.getChildren()) {
                        currentClass = classSnapshot.getValue(Classroom.class);
                        getSupportActionBar().setTitle(currentClass.getName());
                        mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                        mDatabaseReferenceTickets.addValueEventListener(mTicketChangeListener);
                        noClassText.setVisibility(View.GONE);
                        return;
                    }
                } else {
                    // No classes found
                    loadingIndicator.setVisibility(View.GONE);
                    noClassText.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Error on initialization" , databaseError.toException());
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
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
            DialogFragment addClassDialog = new TeacherAddClassDialog();
            addClassDialog.show(getFragmentManager(), "add class");
            return true;
        }

        if (id == R.id.change_name) {
            DialogFragment changeNameDialog = new TeacherCreateNameDialog();
            changeNameDialog.show(getFragmentManager(), "create name");
        }

        if (id == R.id.suggest) {
            Intent intent = new Intent(this, SuggestedQuestionsActivity.class);
            startActivityForResult(intent, SUGGESTED_QUESTION_REQUEST);
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
            // On clicking a ticket that is already launched, go to the ticket's details.
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
            // On click a ticket not yet launched, go to edit page.
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
                startActivityForResult(intent, EDIT_TICKET_REQUEST);
                break;
        }
        if (ticket == null) {
            Log.e("ERR", "Could not find ticket type");
        }
    }

    /**
     * Determine the local index of a ticket given its position in the entire sectioned recycler view.
     * @param position position of item within sectioned recycler view
     * @return local index within respective array list.
     */
    public int determineIndex(int position) {
        if (upcomingTickets.size() > 0 && position < upcomingTickets.size() + 1) {
            return position - 1;
        } else if (upcomingTickets.size() > 0 && position >= upcomingTickets.size() + 1) {
            return position - upcomingTickets.size() - 2;
        } else {
            return position - 1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Returning from edit ticket activity. Make sure that if class of ticket
        // was edited then switch to that class.
        if (requestCode == EDIT_TICKET_REQUEST) {
            if (resultCode == RESULT_OK) {
                String updatedClass = data.getStringExtra("class_name");
                currentClass = classMap.get(updatedClass);
                getSupportActionBar().setTitle(updatedClass);
                Toast.makeText(this, "Your ticket has been updated.", Toast.LENGTH_SHORT).show();
            }
        }

        // Returning from suggested questions activity. Start new ticket activity
        // populated with the selected question.
        if (requestCode == SUGGESTED_QUESTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, CreateTicketActivity.class);
                intent.putExtra("classId", this.getCurrentClass().getId());
                intent.putExtra("className", this.getCurrentClass().getName());
                intent.putExtra("classes", classList);
                intent.putExtra("classMap", classMap);
                intent.putExtra("pre_pop_question", data.getStringExtra("pre_pop_question"));
                startActivity(intent);
            }
        }
    }

    /**
     * Handle positive click on the new class dialog.
     * @param className The new class' name input by the teacher.
     * @param classCode The new class' code input by the teacher.
     * @param dialog The dialog--only dismiss if class creation successful (code and name aren't taken)
     */
    public void doNewClassDialogPositiveClick(final String className, final String classCode, final AlertDialog dialog) {
        for (String c : classList) {
            if (className.equals(c)) {
                Toast.makeText(this, "You already have a class with that name.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Handle adding new class.
        Query classRefByCode = mDatabaseReference.child("classes").orderByChild("courseCode").equalTo(classCode);
        final Query classRef = mDatabaseReference.child("classes").orderByChild("name").equalTo(className);
        classRefByCode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    // Class code already exists
                    Toast.makeText(TeacherTicketListActivity.this, "That class code is taken.", Toast.LENGTH_SHORT).show();
                } else {
                    classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                // Class name already exists
                                Toast.makeText(TeacherTicketListActivity.this, "That class name is taken.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Create and add class to database.
                                DatabaseReference newClassRef = mDatabaseReference.child("teachers").child(mAuth.getCurrentUser().getUid()).push();
                                String newClassId = newClassRef.getKey();
                                String teacherName = sharedPrefs.getString("teacher_name", "");
                                Classroom newClassroom = new Classroom(newClassId, className, teacherName, classCode);
                                newClassRef.setValue(newClassroom);
                                DatabaseReference newClassRefClasses = mDatabaseReference.child("classes").child(newClassId);
                                newClassRefClasses.setValue(newClassroom);

                                // Front-end view changes
                                classListView.getMenu().add(className);
                                currentClass = newClassroom;
                                getSupportActionBar().setTitle(currentClass.getName());
                                fab.setVisibility(View.VISIBLE);

                                // Attach ticket listener to class
                                mDatabaseReferenceTickets = mDatabaseReference.child("tickets").child(currentClass.getId());
                                mDatabaseReferenceTickets.addValueEventListener(mTicketChangeListener);

                                // Dismiss new class dialog since class creation successful
                                dialog.dismiss();
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

    @Override
    protected void onResume() {
        super.onResume();
        mSectionedTicketListAdapter.notifyDataSetChanged();
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
     * @param name New name for teacher
     */
    public void doCreateNameDialogClick(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("teacher_name", name);
        editor.apply();
        userDisplayName.setText(name);
        Toast.makeText(this, "Your name has been set to " + name, Toast.LENGTH_SHORT).show();
    }

    public Classroom getCurrentClass() {
        return this.currentClass;
    }
}
