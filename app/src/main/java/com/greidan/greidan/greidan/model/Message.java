package com.greidan.greidan.greidan.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.greidan.greidan.greidan.manager.UserManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Parcelable {

    String id;
    String subject;
    String content;
    String recipientName;
    String authorName;
    Date timePosted;

    public Message() {

    }

    public Message(String id, String subject, String content, String recipientName, String authorName, Date timePosted) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.recipientName = recipientName;
        this.authorName = authorName;
        this.timePosted = timePosted;
    }

    public Message(JSONObject jsonMessage) throws JSONException, ParseException {
        UserManager userManager = new UserManager(null);

        this.id = jsonMessage.getString("_id");
        this.subject = jsonMessage.getString("subject");
        this.content = jsonMessage.getString("content");
        this.recipientName = jsonMessage.getString("recipient_name");
        this.authorName = jsonMessage.getString("author_name");
        this.timePosted = new Date(jsonMessage.getLong("timePosted"));
    }

    public ArrayList<NameValuePair> getAsRequestParams() {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("subject", subject));
        requestParams.add(new BasicNameValuePair("content", content));
        requestParams.add(new BasicNameValuePair("recipient_name", recipientName));

        return requestParams;
    }


    /* Implement Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subject);
        dest.writeString(content);
        dest.writeString(recipientName);
        dest.writeString(timePosted.toString());
    }

    public Message(Parcel in) throws ParseException {
        UserManager userManager = new UserManager(null);

        id = in.readString();
        subject = in.readString();
        content = in.readString();
        authorName = in.readString();
        recipientName = in.readString();
        timePosted = new Date(in.readLong());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Message createFromParcel(Parcel in) {
            try {
                return new Message(in);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    /* Getters and setters */

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setTimePosted(Date timePosted) {
        this.timePosted = timePosted;
    }
}
