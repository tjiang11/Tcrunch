package com.example.tjiang11.tcrunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherTicketDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ticket_detail);
        Bundle bundle = getIntent().getExtras();
        String question = (String) bundle.get("question");
        long startTime = (long) bundle.get("start_time");

        TextView questionText = (TextView) findViewById(R.id.ticket_detail_question);
        TextView startTimeText = (TextView) findViewById(R.id.ticket_detail_start_time);

        questionText.setText(question);

        Date date = new Date(startTime);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, h:mm a", Locale.US);
        String dateFormatted = formatter.format(date);
        startTimeText.setText(dateFormatted);
    }
}
