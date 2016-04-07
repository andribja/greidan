package com.greidan.greidan.greidan;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class AdManager {

    UserManager userManager;
    DbHelper dbHelper;

    Activity activity;

    String adUrl;

    public AdManager(Activity activity) {
        this.activity = activity;
        userManager = new UserManager(activity);
        dbHelper = new DbHelper(activity);

        if(activity != null) {
            String host = activity.getString(R.string.host);
            String port = activity.getString(R.string.port);

            adUrl = host + ":" + port + "/ad";
        }
    }

    public Ad fetchAd(int id) {
        // TODO: query db or server for data
        // TODO: Properly implement

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

        ServerTask task = new ServerTask((ProgressActivity) activity, adUrl, false, requestParams);
        task.execute();

        return null;
    }

    public void saveAd(Ad ad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = ad.getContentValues();

        long newRowId = db.insert(
                DbSchema.AdTable.NAME,
                null,
                values
        );
    }

    public void postAdToServer(Ad ad) {
        ArrayList<NameValuePair> requestParams = ad.getAsRequestParams();
        requestParams.add(new BasicNameValuePair("token", userManager.getToken()));
        
        ServerTask task = new ServerTask((ProgressActivity) activity, adUrl, true, requestParams);
        task.execute();
    }

    public void fetchAds(String category) {
        // TODO: Properly implement
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("category", category));

        ServerTask task = new ServerTask((ProgressActivity) activity, adUrl, false, requestParams);
        task.execute();
    }

    private void handleRequestedData(JSONObject jObj) {
        ArrayList<Ad> ads = new ArrayList<Ad>();

        if(jObj != null) {
            Iterator<?> keys = jObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();

                if(key.equals("adlist")) {
                    try {
                        JSONArray jArr = jObj.getJSONArray(key);
                        for(int i=0; i<jArr.length(); i++) {
                            ads.add(new Ad(jArr.getJSONObject(i)));
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Bundle data = new Bundle();
        data.putParcelableArrayList("ads", ads);
        ((ProgressActivity) activity).doUponCompletion(data);
    }

    private class ServerTask extends AsyncTask<Void, Void, JSONObject> {

        ProgressActivity activity;
        List<NameValuePair> requestParams;
        String url;
        boolean post;

        public ServerTask(ProgressActivity activity, String url, boolean post, List<NameValuePair> requestParams) {
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

            if(post) {
                Boolean success = false;
                String message = "";
                long id = -1;

                try { id = jObj.getInt("_id"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                try { success = jObj.getBoolean("success"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                try { message = jObj.getString("response"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                Bundle data = new Bundle();
                data.putBoolean("success", success);
                data.putString("message", message);
                data.putLong("id", id);
                activity.doUponCompletion(data);
            } else {
                handleRequestedData(jObj);
            }
        }
    }
}
