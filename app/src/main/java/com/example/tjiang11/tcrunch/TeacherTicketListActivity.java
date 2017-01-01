package com.example.tjiang11.tcrunch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.example.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TeacherTicketListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mTicketListRecyclerView;
    private TicketListAdapter mTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

    private DatabaseReference mDatabaseReference;
    private Query mDatabaseReferenceTickets;
    private ValueEventListener mValueEventListener;

    private ArrayList<Ticket> ticketList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        //ticketList.add(new Ticket("question1", Ticket.QuestionType.FreeResponse, 0, 0, "class name"));
        mTicketListAdapter = new TicketListAdapter(ticketList);
        mTicketListRecyclerView.setAdapter(mTicketListAdapter);

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ticketList.clear();
                for (DataSnapshot ticketSnapshot: dataSnapshot.getChildren()) {
                    Ticket mTicket = ticketSnapshot.getValue(Ticket.class);
                    ticketList.add(mTicket);
                }
                Collections.sort(ticketList, Ticket.TicketTimeComparator);
                mTicketListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReferenceTickets = mDatabaseReference.child("tickets");
        mDatabaseReferenceTickets.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
}
