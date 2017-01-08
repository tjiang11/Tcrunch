package com.example.tjiang11.tcrunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by tjiang11 on 12/30/16.
 */

public class Response implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
