package com.greidan.greidan.greidan;

import android.content.ContentValues;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Daníel on 01/03/2016.
 */
public class Ad implements Parcelable {

    long id;
    String title;
    String content;
    String category;
    User author;
    Date timePosted;
    Location location;

    public Ad() {

    }

    public Ad(long id, String title, String content, String category, User author, Date timePosted, Location location) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
        this.timePosted = timePosted;
        this.category = category;
        this.location = location;
    }

    public Ad(JSONObject jsonAd) throws JSONException, ParseException {
        UserManager userManager = new UserManager(null);

        //String timePostedStr = jsonAd.getString("timePosted");
        //DateFormat df = DateFormat.getDateInstance();
        //Date timePosted = df.parse(timePostedStr);
        Date timeposted = new Date();

        String address = jsonAd.getString("address");
        double lat = jsonAd.getDouble("lat");
        double lng = jsonAd.getDouble("lng");

        this.id = jsonAd.getLong("id");
        this.title = jsonAd.getString("title");
        this.content = jsonAd.getString("content");
        this.category = jsonAd.getString("category");
        this.author = userManager.findUserById(jsonAd.getInt("author_id"));
        this.timePosted = timePosted;
        this.location = new Location(address);
        this.location.setLatitude(lat);
        this.location.setLongitude(lng);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(DbSchema.AdTable.Cols.ID, Long.toString(id));
        values.put(DbSchema.AdTable.Cols.TITLE, title);
        values.put(DbSchema.AdTable.Cols.CONTENT, content);
        values.put(DbSchema.AdTable.Cols.CATEGORY, category);
        values.put(DbSchema.AdTable.Cols.AUTHOR_ID, author.getId());
        values.put(DbSchema.AdTable.Cols.TIME_POSTED, timePosted.toString());
        values.put(DbSchema.AdTable.Cols.ADDRESS, location.getProvider());
        values.put(DbSchema.AdTable.Cols.LAT, location.getLatitude());
        values.put(DbSchema.AdTable.Cols.LNG, location.getLongitude());

        return values;
    }

    public ArrayList<NameValuePair> getRequestParams() {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("title", title));
        requestParams.add(new BasicNameValuePair("content", content));
        requestParams.add(new BasicNameValuePair("category", category));
        requestParams.add(new BasicNameValuePair("author_id", Integer.toString(author.getId())));
        requestParams.add(new BasicNameValuePair("lat", Double.toString(location.getLatitude())));
        requestParams.add(new BasicNameValuePair("lng", Double.toString(location.getLongitude())));

        return requestParams;
    }


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public User getAuthor() {
        return author;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public Location getLocation() {
        return location;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setTimePosted(Date timePosted) {
        this.timePosted = timePosted;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(category);
        dest.writeInt(author.getId());
        dest.writeString(timePosted.toString());
        dest.writeDouble(location.getLatitude());
        dest.writeDouble(location.getLongitude());
    }

    public Ad(Parcel in) throws ParseException {
        UserManager userManager = new UserManager(null);

        id = in.readLong();
        title = in.readString();
        content = in.readString();
        category = in.readString();
        author = userManager.findUserById(in.readInt());

//        String timePostedStr = in.readString();
//        DateFormat df = DateFormat.getDateInstance();
//        timePosted = df.parse(timePostedStr);
        timePosted = new Date();

        double lat = in.readDouble();
        double lng = in.readDouble();
        location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Ad createFromParcel(Parcel in) {
            try {
                return new Ad(in);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };
}
