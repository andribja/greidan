package com.greidan.greidan.greidan.model;

import android.content.ContentValues;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.greidan.greidan.greidan.DbSchema;
import com.greidan.greidan.greidan.manager.UserManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
public class Ad implements Parcelable {

    String id;
    String title;
    String content;
    String category;
    String authorName;
    Date timePosted;
    Location location;

    public Ad() {

    }

    public Ad(String id, String title, String content, String category, String authorName, Date timePosted, Location location) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.authorName = authorName;
        this.timePosted = timePosted;
        this.category = category;
        this.location = location;
    }

    public Ad(JSONObject jsonAd) throws JSONException, ParseException {
        UserManager userManager = new UserManager(null);

        this.id = jsonAd.getString("_id");
        this.title = jsonAd.getString("title");
        this.content = jsonAd.getString("content");
        this.category = jsonAd.getString("category");
//        this.authorName = jsonAd.getString("authorName");
        this.authorName = "temp_author";    // TODO: remove this once authorName is provided by server
        this.timePosted = new Date(jsonAd.getLong("timePosted"));
        this.location = new Location("temp_address");
        JSONArray loc = jsonAd.getJSONArray("loc");
        this.location.setLatitude(loc.getDouble(0));
        this.location.setLongitude(loc.getDouble(0));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(DbSchema.AdTable.Cols.ID, id);
        values.put(DbSchema.AdTable.Cols.TITLE, title);
        values.put(DbSchema.AdTable.Cols.CONTENT, content);
        values.put(DbSchema.AdTable.Cols.CATEGORY, category);
        values.put(DbSchema.AdTable.Cols.AUTHOR_ID, authorName);
        values.put(DbSchema.AdTable.Cols.TIME_POSTED, timePosted.toString());
        values.put(DbSchema.AdTable.Cols.ADDRESS, location.getProvider());
        values.put(DbSchema.AdTable.Cols.LAT, location.getLatitude());
        values.put(DbSchema.AdTable.Cols.LNG, location.getLongitude());

        return values;
    }

    public ArrayList<NameValuePair> getAsRequestParams() {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("title", title));
        requestParams.add(new BasicNameValuePair("content", content));
        requestParams.add(new BasicNameValuePair("category", category));
        requestParams.add(new BasicNameValuePair("authorName", authorName));
        requestParams.add(new BasicNameValuePair("lat", Double.toString(location.getLatitude())));
        requestParams.add(new BasicNameValuePair("lng", Double.toString(location.getLongitude())));

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
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(category);
        dest.writeString(authorName);
        dest.writeString(timePosted.toString());
        dest.writeDouble(location.getLatitude());
        dest.writeDouble(location.getLongitude());
    }

    public Ad(Parcel in) throws ParseException {
        UserManager userManager = new UserManager(null);

        id = in.readString();
        title = in.readString();
        content = in.readString();
        category = in.readString();
        authorName = in.readString();
        timePosted = new Date(in.readLong());
        location = new Location("");
        location.setLatitude(in.readDouble());
        location.setLongitude(in.readDouble());
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

    /* Getters and setters */

    public String getId() {
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

    public String getAuthorName() {
        return authorName;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public Location getLocation() {
        return location;
    }

    public void setId(String id) {
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

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setTimePosted(Date timePosted) {
        this.timePosted = timePosted;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
