package com.example.tjiang11.tcrunch.models;

/**
 * Created by tjiang11 on 12/30/16.
 */

public class Response {

    private String author;
    private String response;

    public Response() {}


    public Response(String author, String response) {
        this.author = author;
        this.response = response;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getResponse() {
        return this.response;
    }
}
