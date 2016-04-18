package com.greidan.greidan.greidan.model;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review implements Parcelable {

    private String id;
    private double rating;
    private String content;
    private String revieweeName;
    private String authorName;
    private Date timePosted;

    public Review(String id, double rating, String content, String revieweeName, String authorName, Date timePosted) {
        this.id = id;
        this.rating = rating;
        this.content = content;
        this.revieweeName = revieweeName;
        this.authorName = authorName;
        this.timePosted = timePosted;
    }

    public String getId() {
        return id;
    }

    public double getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getRevieweeName() {
        return revieweeName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public Review(JSONObject jObj) throws JSONException {
        this.id = jObj.getString("_id");
        this.rating = jObj.getDouble("stars");
        this.content = jObj.getString("content");
        this.revieweeName = jObj.getString("revieweeName");
        this.authorName = jObj.getString("authorName");
        this.timePosted = new Date(jObj.getLong("timePosted"));
    }

    public List<NameValuePair> getAsRequestParams() {
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("_id", id));
        params.add(new BasicNameValuePair("stars", Double.toString(rating)));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("revieweeName", revieweeName));
        params.add(new BasicNameValuePair("authorName", authorName));

        return params;
    }

    // Implement parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(rating);
        dest.writeString(content);
        dest.writeString(revieweeName);
        dest.writeString(authorName);
        dest.writeLong(timePosted.getTime());
    }

    public Review(Parcel in) throws ParseException {
        this.id = in.readString();
        this.rating = in.readDouble();
        this.content = in.readString();
        this.revieweeName = in.readString();
        this.authorName = in.readString();
        this.timePosted = new Date(in.readLong());
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
