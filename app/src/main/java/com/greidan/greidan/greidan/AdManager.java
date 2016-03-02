package com.greidan.greidan.greidan;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Daníel on 01/03/2016.
 */
public class AdManager {

    UserManager userManager;
    DbHelper dbHelper;

    Activity activity;

//    static String getadUrl = "http://10.0.2.2:8080/ads";
//    static String postadUrl = "http://10.0.2.2:8080/postad";
    static String adUrl = "http://10.0.2.2:8080/ad";

    public AdManager(Activity activity) {
        this.activity = activity;
        userManager = new UserManager(activity);
        dbHelper = new DbHelper(activity);
    }

    public Ad fetchAd(int id) {
        // TODO: query db or server for data

        /*
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"*"};
        String selection = DbSchema.AdTable.Cols.ID;
        String[] selectionArgs = {Integer.toString(id)};

        Cursor cursor = db.query(
                DbSchema.AdTable.NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        if(cursor.getInt(cursor.getColumnIndex(DbSchema.AdTable.Cols.ID)) >= 0) {
            String title = cursor.getString(cursor.getColumnIndex(DbSchema.AdTable.Cols.TITLE));
            String content = cursor.getString(cursor.getColumnIndex(DbSchema.AdTable.Cols.CONTENT));
            String category = cursor.getString(cursor.getColumnIndex(DbSchema.AdTable.Cols.CATEGORY));
            int authorId = cursor.getInt(cursor.getColumnIndex(DbSchema.AdTable.Cols.AUTHOR_ID));
            String timePostedStr = cursor.getString(cursor.getColumnIndex(DbSchema.AdTable.Cols.TIME_POSTED));
            String address = cursor.getString(cursor.getColumnIndex(DbSchema.AdTable.Cols.ADDRESS));
            double lat = cursor.getDouble(cursor.getColumnIndex(DbSchema.AdTable.Cols.LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(DbSchema.AdTable.Cols.LNG));

            DateFormat df = DateFormat.getDateInstance();
            Date timePosted = null;
            try {
                timePosted = df.parse(timePostedStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Location location = new Location(address);
            location.setLatitude(lat);
            location.setLongitude(lng);

            return new Ad(id, title, content, category, userManager.findUserById(authorId), timePosted, location);
        } */

        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("id", Integer.toString(id)));

        ServerTask task = new ServerTask(activity, adUrl, false, requestParams);
        task.execute();

        return null;
    }

    public void postAdToServer(Ad ad) {
        ArrayList<NameValuePair> requestParams = ad.getRequestParams();
        
        ServerTask task = new ServerTask(activity, adUrl, true, requestParams);
        task.execute();
    }

    public void fetchAds(int categoryId) {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("categoryId", Integer.toString(categoryId)));

        ServerTask task = new ServerTask(activity, adUrl, false, requestParams);
        task.execute();
    }

    private void handleRequestedData(JSONObject jObj) {
        Log.i("AdManager", "handleRequestedData");

//        ArrayList<Ad> ads = new ArrayList<Ad>();
//        Ad ad1 = new Ad(13, "foo", "bar", "baz", new User(0, "foobar", ""), null, null);
//        Ad ad2 = new Ad(5, "bar", "baz", "foo", new User(1, "raboof", ""), null, null);
//        ads.add(ad1);
//        ads.add(ad2);
//
//        ((AdListActivity) activity).populateAdList(ads);

        ArrayList<Ad> ads = new ArrayList<Ad>();
        Iterator<?> keys = jObj.keys();

        while(keys.hasNext()) {
            String key = (String) keys.next();

            Ad ad;
            try {
                if(jObj.get(key) instanceof JSONObject) {
                    // We've got a list of ads
                    ad = new Ad((JSONObject) jObj.get(key));
                    ads.add(ad);
                } else {
                    // We've got a single ad
                    ad = new Ad(jObj);
                    ((AdViewActivity) activity).populateAdView(ad);
                    return;
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        ((AdListActivity) activity).populateAdList(ads);
    }

    private class ServerTask extends AsyncTask<Void, Void, JSONObject> {

        Activity activity;
        List<NameValuePair> requestParams;
        String url;
        boolean post;

        public ServerTask(Activity activity, String url, boolean post, List<NameValuePair> requestParams) {
            this.activity = activity;
            this.requestParams = requestParams;
            this.url = url;
            this.post = post;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            ServerRequest request = new ServerRequest();
            JSONObject jObj;

            if(post) {
                return request.postToUrl(url, requestParams);
            }

            return request.getFromUrl(url, requestParams);
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            // remove loading animation

            // do something with the data
            Boolean success = false;
            String message = "";
            int id = -1;

            try { success = jObj.getBoolean("success"); }
            catch (JSONException | NullPointerException e) { e.printStackTrace(); }

            try { message = jObj.getString("response"); }
            catch (JSONException | NullPointerException e) { e.printStackTrace(); }

            try { id = jObj.getInt("id"); }
            catch (JSONException | NullPointerException e) { e.printStackTrace(); }

            if(post) {
                ((NewAdActivity) activity).doAfterPost(success, message, id);
            } else {
                handleRequestedData(jObj);
            }
        }
    }
}
