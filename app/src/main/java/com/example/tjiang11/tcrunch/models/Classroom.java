package com.example.tjiang11.tcrunch.models;

/**
 * Created by tjiang11 on 1/23/17.
 */

public class Classroom {

    private String id;
    private String name;

    public Classroom() {}
    public Classroom(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "id: " + this.id + " name: " + this.name;
    }

}
