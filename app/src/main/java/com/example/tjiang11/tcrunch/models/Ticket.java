package com.example.tjiang11.tcrunch.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

/**
 * Created by tjiang11 on 12/27/16.
 */

public class Ticket {

    public enum QuestionType { FreeResponse, MultipleChoice };

    private String question;
    private QuestionType questionType;
    private List<String> answerChoices;
    private List<String> responses;

    private String startTime;
    private String endTime;
//    private HashMap<String, Object> startTime;
//    private HashMap<String, Object> endTime;

    public Ticket() {}

    public Ticket(String question, QuestionType questionType, ArrayList<String> answerChoices, ArrayList<String> responses,
                  //HashMap<String, Object> startTime, HashMap<String, Object> endTime) {
                  String startTime, String endTime) {
        this.question = question;
        this.questionType = questionType;
        this.answerChoices = answerChoices;
        this.responses = responses;
        //HashMap<String, Object> startTimeObj = new HashMap<String, Object>();
        //startTimeObj.put("date", ServerValue.TIMESTAMP);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getQuestion() { return this.question; }

}
