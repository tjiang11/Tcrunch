package com.toniebalonie.tjiang11.tcrunch.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tjiang11 on 12/27/16.
 */

public class Ticket {

    public enum QuestionType { FreeResponse, MultipleChoice };
    private String id;
    private String className;
    private String question;
    private QuestionType questionType;
    private List<String> answerChoices = new ArrayList<>();

    private long startTime;
    private long endTime;
//    private HashMap<String, Object> startTime;
//    private HashMap<String, Object> endTime;

    public Ticket() {}

    public Ticket(String question,
                  QuestionType questionType, //ArrayList<String> answerChoices, //ArrayList<String> responses,
                  //HashMap<String, Object> startTime, HashMap<String, Object> endTime) {
                  long startTime, long endTime, String className) {
        this.question = question;
        this.questionType = questionType;
        this.answerChoices = answerChoices;
        //HashMap<String, Object> startTimeObj = new HashMap<String, Object>();
        //startTimeObj.put("date", ServerValue.TIMESTAMP);
        this.startTime = startTime;
        this.endTime = endTime;
        this.className = className;
    }
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getQuestion() { return this.question; }
    public void setQuestion(String question) { this.question = question; }
    public long getStartTime() { return this.startTime; }
    public long getEndTime() { return this.endTime; }
    public String getClassName() { return this.className; }
    public QuestionType getQuestionType() { return this. questionType; }
    public List<String> getAnswerChoices() { return this.answerChoices; }
    public void setAnswerChoices(ArrayList<String> answerChoices) { this.answerChoices = answerChoices; }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public static Comparator<Ticket> TicketTimeComparator = new Comparator<Ticket>() {
        @Override
        public int compare(Ticket o1, Ticket o2) {
            return o1.getStartTime() > o2.getStartTime() ? 1 : -1;
        }
    };

    @Override
    public String toString() {
        return "Question: " + question + ", id:  " + id;
    }

    //    public String getQuestionType() {
//        if (questionType == null) {
//            return null;
//        }
//        return questionType.name();
//    }
//    public void setQuestionType(String questionType) {
//        if (questionType == null) {
//            this.question = null;
//        } else {
//            this.questionType = QuestionType.valueOf(questionType);
//        }
//    }

}