package com.toniebalonie.tjiang11.tcrunch.models;

import android.os.Parcel;
import android.os.Parcelable;

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

    public Response(Parcel in) {
        this.author = in.readString();
        this.response = in.readString();
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
        dest.writeString(author);
        dest.writeString(response);
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel source) {
            return new Response(source);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}
