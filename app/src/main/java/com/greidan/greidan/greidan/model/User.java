package com.greidan.greidan.greidan.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class User implements Parcelable {

    String id;
    String username;
    String email;
    Date timeJoined;
    String extImagePath;
    String localImagePath;
    double rating;

    public User() {

    }

    public User(String id, String username, String email, Date timeJoined, double rating) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.timeJoined = timeJoined;
        this.rating = rating;
    }

    public User(JSONObject jObj) throws JSONException {
        this.id = jObj.getString("_id");
        this.username = jObj.getString("username");
        this.email = jObj.getString("email");
        this.timeJoined = new Date(jObj.getLong("time_joined"));
        if(jObj.has("imgPath")) {
            this.extImagePath = jObj.getString("imgPath");
        }
        if(jObj.has("rating")) {
            this.rating = jObj.getDouble("rating");
        } else {
            this.rating = 0;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTimeJoined() {
        return timeJoined;
    }

    public void setTimeJoined(Date timeJoined) {
        this.timeJoined = timeJoined;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getExtImagePath() {
        return extImagePath;
    }

    public void setExtImagePath(String extImagePath) {
        this.extImagePath = extImagePath;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImage) {
        this.localImagePath = localImage;
    }


    /// Impelemnt parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(timeJoined.toString());
        dest.writeString(extImagePath);
        dest.writeString(localImagePath);
        dest.writeDouble(rating);
    }

    public User(Parcel in) throws ParseException {
        this.id = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.timeJoined = new Date(in.readString());
        this.extImagePath = in.readString();
        this.localImagePath = in.readString();
        this.rating = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            try {
                return new User(source);
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
