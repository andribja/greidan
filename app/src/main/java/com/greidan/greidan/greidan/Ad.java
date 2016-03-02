package com.greidan.greidan.greidan;

import android.content.ContentValues;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Daníel on 01/03/2016.
 */
public class Ad implements Serializable {

    int id;
    String title;
    String content;
    String category;
    User author;
    Date timePosted;
    Location location;

    public Ad() {

    }

    public Ad(int id, String title, String content, String category, User author, Date timePosted, Location location) {
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

        String timePostedStr = jsonAd.getString("timePosted");
        DateFormat df = DateFormat.getDateInstance();
        Date timePosted = df.parse(timePostedStr);

        String address = jsonAd.getString("address");
        double lat = jsonAd.getDouble("lat");
        double lng = jsonAd.getDouble("lng");

        this.id = jsonAd.getInt("id");
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

        values.put(DbSchema.AdTable.Cols.ID, Integer.toString(id));
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

    public int getId() {
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

    public void setId(int id) {
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
}
