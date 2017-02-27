package com.toniebalonie.tjiang11.tcrunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by tjiang11 on 12/30/16.
 */

public class Response implements Parcelable {

    private String author;
    private String response;
    private long time;

    public Response() {}

    public Response(String author, String response, long time) {
        this.author = author;
        this.response = response;
        this.time = time;
    }

    public Response(Parcel in) {
        this.author = in.readString();
        this.response = in.readString();
        this.time = in.readLong();
    }


    public String getAuthor() {
        return this.author;
    }

    public String getResponse() {
        return this.response;
    }

    public long getTime() { return this.time; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(response);
        dest.writeLong(time);
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

    public static Comparator<Response> ResponseTimeComparator = new Comparator<Response>() {
        @Override
        public int compare(Response o1, Response o2) {
            return o1.getTime() < o2.getTime() ? 1 : -1;
        }
    };
}
