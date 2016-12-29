package com.example.tjiang11.tcrunch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

public class TeacherTicketListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mTicketListRecyclerView;
    private RecyclerView.Adapter mTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        Ticket[] testList = new Ticket[5];
        ArrayList<String> empty = new ArrayList<String>();
        testList[0] = new Ticket("What is an eigenvector? How is an eigenvector useful? Describe how you find an eigenvetor. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues. What are eigenvalues? Describe the process for finding eigenvalues.", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
        testList[1] = new Ticket("question1", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
        testList[2] = new Ticket("question2", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
        testList[3] = new Ticket("question3", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");
        testList[4] = new Ticket("question4", Ticket.QuestionType.FreeResponse, empty, empty, "start", "end");

        mTicketListAdapter = new TicketListAdapter(testList);
        mTicketListRecyclerView.setAdapter(mTicketListAdapter);
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
