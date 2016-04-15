package com.greidan.greidan.greidan.manager;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.greidan.greidan.greidan.model.Message;
import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.ServerRequest;
import com.greidan.greidan.greidan.activity.ProgressActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageManager {
    UserManager userManager;
    Activity activity;
    String messageUrl;

    public MessageManager(Activity activity) {
        this.activity = activity;
        userManager = new UserManager(activity);

        if (activity != null) {
            String host = activity.getString(R.string.host);
            String port = "8080";

            messageUrl = host + ":" + port + "/message";
        }
    }

    public void postMessageToServer(Message message) {
        ArrayList<NameValuePair> requestParams = message.getAsRequestParams();
        requestParams.add(new BasicNameValuePair("token", userManager.getToken()));
        ServerTask task = new ServerTask((ProgressActivity) activity, messageUrl, true, requestParams);
        task.execute();
    }

    public void handleRequestedData(JSONObject jObj) {
        ArrayList<Message> messages = new ArrayList<Message>();

        if(jObj != null) {
            Iterator<?> keys = jObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();

                if (key.equals("messagelist")) {
                    try {
                        JSONArray jArr = jObj.getJSONArray(key);
                        for (int i = 0; i < jArr.length(); i++) {
                            messages.add(new Message(jArr.getJSONObject(i)));
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
