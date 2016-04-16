package com.greidan.greidan.greidan.model;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

import java.text.ParseException;
import java.util.Date;

public class Review implements Parcelable {

    private String id;
    private String title;
    private String content;
    private double rating;
    private Date timePosted;

    public Review(String id, String title, String content, double rating, Date timePosted) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.timePosted = timePosted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(Date timePosted) {
        this.timePosted = timePosted;
    }

    // Implement parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeDouble(rating);
    }

    public Review(Parcel in) throws ParseException {
        this.id = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.rating = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            try {
                return new Review(source);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }
    };
}
