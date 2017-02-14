package com.toniebalonie.tjiang11.tcrunch.models;

import java.io.Serializable;

/**
 * Created by tjiang11 on 1/23/17.
 */

public class Classroom implements Serializable {

    private String id;
    private String name;
    private String courseCode; //6-digit alphanumeric code

    public Classroom() {}
    public Classroom(String id, String name, String courseCode) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCourseCode() { return this.courseCode; }

    public String toString() {
        return "id: " + this.id + " name: " + this.name;
    }

}
