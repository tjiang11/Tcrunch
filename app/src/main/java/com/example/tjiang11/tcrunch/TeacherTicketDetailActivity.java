package com.example.tjiang11.tcrunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TeacherTicketDetailActivity extends AppCompatActivity {

    private ResponseListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ticket_detail);
        Bundle bundle = getIntent().getExtras();
        String question = (String) bundle.get("question");
        long startTime = (long) bundle.get("start_time");
        ArrayList<Response> responseList = getIntent().getParcelableArrayListExtra("responses");

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
        String dateFormatted = formatter.format(date);
        startTimeText.setText(dateFormatted);
    }
}
