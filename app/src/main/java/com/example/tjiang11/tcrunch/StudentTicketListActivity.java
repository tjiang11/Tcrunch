package com.example.tjiang11.tcrunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.tjiang11.tcrunch.models.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;


public class StudentTicketListActivity extends AppCompatActivity implements ItemClickListener {

    private RecyclerView mRecyclerView;
    private SectionedTicketListAdapter mSectionedTicketListAdapter;
    private RecyclerView.LayoutManager mTicketListLayoutManager;

    private Section answered;
    private Section unanswered;

    private DatabaseReference mDatabaseReference;
    private Query mDatabaseReferenceTickets;
    private ValueEventListener mValueEventListener;

    private HashSet<Ticket> hasAnswered;

    private ArrayList<Ticket> answeredTickets;
    private ArrayList<Ticket> unansweredTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_ticket_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.student_ticket_list_recycler_view);
        answeredTickets = new ArrayList<Ticket>();
        unansweredTickets = new ArrayList<Ticket>();
        hasAnswered = new HashSet<Ticket>();
        mSectionedTicketListAdapter = new SectionedTicketListAdapter();
        answered = new TicketSection("ANSWERED", answeredTickets, this);
        unanswered = new TicketSection("NOT ANSWERED", unansweredTickets, this);
        mSectionedTicketListAdapter.addSection(unanswered);
        mSectionedTicketListAdapter.addSection(answered);
        mRecyclerView.setAdapter(mSectionedTicketListAdapter);
        mTicketListLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mTicketListLayoutManager);

        answered.setVisible(false);
        unanswered.setVisible(false);

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answeredTickets.clear();
                unansweredTickets.clear();
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                    if (!hasAnswered.contains(ticket)) {
                        unansweredTickets.add(ticket);
                    } else {
                        answeredTickets.add(ticket);
                    }
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
    public void onClick(View view, int position, String type) {

    }
}
