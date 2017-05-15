package com.toniebalonie.tjiang11.tcrunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuggestedQuestionsActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_questions);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Understanding student comprehension");
        listDataHeader.add("Understanding student confusion");
        listDataHeader.add("Understanding student interests");
        listDataHeader.add("Evaluating course material");
        listDataHeader.add("Evaluating assignments and assessments");
        listDataHeader.add("Evaluating your teaching strategies");
        listDataHeader.add("Evaluating classroom communication");

        // Adding child data
        List<String> g1 = new ArrayList<String>();
        g1.add("Explain today's most important concept briefly");
        g1.add("Name one important thing you learned today.");
        g1.add("What is one real-world application of what we talked about in class today?");
        g1.add("On a scale from 1 to 5, where 1 = \"I am lost\" and 5 = \"I understand everything\", how well did you understand the lecture content today?");

        List<String> g2 = new ArrayList<String>();
        g2.add("What was the most confusing concept from class today?");
        g2.add("What is one question you have from today's lecture?");
        g2.add("What is one concept you would like to review?");
        g2.add(" If we were to take the test on the material today, which subject are you least confident about?");

        List<String> g3 = new ArrayList<String>();
        g3.add("What are you most interested in learning about in this class?");
        g3.add("Do you think we should spend more or less time on material covered in class?");
        g3.add("Why are you taking this class?");
        g3.add("How engaged did you feel in class today?");
        g3.add("Does your participation in this class make you want to learn more outside of class?");
        g3.add("What are you most looking forward to this semester?");

        List<String> g4 = new ArrayList<String>();
        g4.add("Which classroom activities do you find beneficial and worthwhile?");
        g4.add("What materials helped you the most in preparing for class?");
        g4.add("Do you think we are going too slow or quick over material at this point of the semester?");
        g4.add("What do other students say about this class?");
        g4.add("If you could get rid of one lecture so far which would it be? Why?");
        g4.add("The most confusing thing about this class is _____?");
        g4.add("If you could change one thing about this class what would it be?");
        g4.add("What do you think the purpose of this class is?");
        g4.add("Do you think that the time that we spend working on application probems in class is valuable?");

        List<String> g5 = new ArrayList<String>();
        g5.add("How many hours did you spend on your homework?");
        g5.add("How many hours did you spend preparing for lecture this week?");
        g5.add("What is one thing that I could do to improve homework assignments?");
        g5.add("What is one thing you like about tests given?");
        g5.add("What class preparation did you complete?");
        g5.add("What do you do to study for this course?");
        g5.add("If you could get rid of one assignment or test which would it be? Why?");

        List<String> g6 = new ArrayList<String>();
        g6.add("I am trying out a new teaching strategy of using YouTube videos in the classroom. Do you think this improves your learning? Why/why not?");
        g6.add("At the end of the year you will rate me on how effective of a teacher I was. How would you say I am doing now?");
        g6.add("What is one way I could improve my teaching?");
        g6.add("Give an example of a good teaching strategy another teacher has used that I could implement to facilitate learning.");
        g6.add("What is one thing that I could do to improve your experience in this class?");
        g6.add("What has been your favorite aspect of the class thus far?");

        List<String> g7 = new ArrayList<String>();
        g7.add("Have you come to office hours? If not, why not?");
        g7.add("How well do you agree with the statement, \"I feel comfortable approaching the professor with a problem or question that I have.\"?");
        g7.add("Do you feel like you receive quality feedback on your assignments and/or tests?");
        g7.add("One of the goals for this class includes class participation. How well do you think I have facilitated that?");
        g7.add("How might technology be used to improve the classroom experience?");
        g7.add("What is one question you would like to ask me?");
        g7.add("What teaching or learning technology applications could be utilized or better utilized to improve the classroom experience? Explain.");

        listDataChild.put(listDataHeader.get(0), g1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), g2);
        listDataChild.put(listDataHeader.get(2), g3);
        listDataChild.put(listDataHeader.get(3), g4);
        listDataChild.put(listDataHeader.get(4), g5);
        listDataChild.put(listDataHeader.get(5), g6);
        listDataChild.put(listDataHeader.get(6), g7);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
