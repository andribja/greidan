package com.greidan.greidan.greidan;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Daníel on 01/03/2016.
 */
public class AdManager {

    Activity activity;

    static String getAdsUrl = "http://10.0.2.2:8080/ads";
    static String postAdsUrl = "http://10.0.2.2:8080/postad";

    public AdManager(Activity activity) {
        this.activity = activity;
    }

    public Ad getAd(int id) {



        return null;
    }

    public void postAdToServer(Ad ad) {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("title", ad.getTitle()));
        requestParams.add(new BasicNameValuePair("content", ad.getContent()));
        //requestParams.add(new BasicNameValuePair("category", ad.getCategory()));
        requestParams.add(new BasicNameValuePair("author", ad.getAuthor().getUsername()));
        
        ServerTask task = new ServerTask(activity, postAdsUrl, true, requestParams);
        task.execute();
    }

    private void loadAdsFromServer(int categoryId) {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("categoryId", Integer.toString(categoryId)));

        ServerTask task = new ServerTask(activity, getAdsUrl, false, requestParams);
        task.execute();
    }

    private void handleRequestedData(JSONObject jObj) {
        ArrayList<String> titles = new ArrayList<String>();
//        Iterator<?> keys = jObj.keys();

//        while(keys.hasNext()) {
//            String key = (String) keys.next();
//            JSONObject foo = new JSONObject();
//            try {
//                foo = (JSONObject) jObj.get(key);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                titles.add(foo.getString("title"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        ((AdListActivity) activity).populateAdList(titles);
    }

    private class ServerTask extends AsyncTask<Void, Void, JSONObject> {

        //AdListActivity activity;
        List<NameValuePair> requestParams;
        String url;
        boolean post;

        public ServerTask(Activity activity, String url, boolean post, List<NameValuePair> requestParams) {
            //this.activity = (AdListActivity) activity;
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
            catch (JSONException e) { e.printStackTrace(); }

            try { id = jObj.getInt("id"); }
            catch (JSONException e) { e.printStackTrace(); }

            if(post) {
                ((NewAdActivity) activity).doAfterPost(success, message, id);
            } else {
                handleRequestedData(jObj);
            }
        }
    }
}
