package com.example.tjiang11.tcrunch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.example.tjiang11.tcrunch.LoginActivity.PREFS_NAME;

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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<Ticket> upcomingTickets;
    private ArrayList<Ticket> launchedTickets;
    private ArrayList<Ticket> ticketList;

    private DrawerLayout mDrawerLayout;
    private NavigationView classListView;
    private ArrayAdapter classListViewAdapter;
    private ArrayList<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG
//                        .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), CreateTicketActivity.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        classList = new ArrayList<String>();
        classList.add("classy class");
        classListViewAdapter = new ArrayAdapter<String>(this, R.layout.class_list_item, classList);
        classListView = (NavigationView) findViewById(R.id.nav_view);
        classListView.setNavigationItemSelectedListener(this);

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

        mDatabaseReferenceTickets = mDatabaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("tickets");
        mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

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
            intent.putParcelableArrayListExtra("responses", (ArrayList<? extends Parcelable>) ticket.getResponses());
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
}
