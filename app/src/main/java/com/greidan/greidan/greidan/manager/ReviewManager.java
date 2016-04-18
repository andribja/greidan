package com.greidan.greidan.greidan.manager;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.greidan.greidan.greidan.DbHelper;
import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.activity.ProgressActivity;
import com.greidan.greidan.greidan.model.Review;
import com.greidan.greidan.greidan.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewManager {

    private static final String TAG = "ReviewManager";

    UserManager userManager;

    private String reviewUrl;

    Activity activity;

    public ReviewManager(Activity activity) {
        this.activity = activity;
        userManager = new UserManager(activity);

        if(activity != null) {
            String host = activity.getString(R.string.host);
            String port = activity.getString(R.string.port);

            this.reviewUrl = host + ":" + port + "/review";
        }
    }

    public void getReviewsForUser(String userId) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("user_id", userId));

        ReviewTask task = new ReviewTask((ProgressActivity) activity, reviewUrl, false, params);
        task.execute();
    }

    public void postReviewToServer(Review review) {
        List<NameValuePair> params = review.getAsRequestParams();
        params.add(new BasicNameValuePair("token" ,userManager.getToken()));

        ReviewTask task = new ReviewTask((ProgressActivity) activity, reviewUrl, true, params);
        task.execute();
    }

    private void handleRequestedData(JSONObject jObj, Bundle response) {
        // TODO: Parse json and call activity.doUponCompletion()
    }

    private class ReviewTask extends AsyncTask<Void, Void, JSONObject> {

        ProgressActivity activity;
        String url;
        boolean post;
        List<NameValuePair> requestParams;

        public ReviewTask(ProgressActivity activity, String url, boolean post, List<NameValuePair> params) {
            this.activity = activity;
            this.url = url;
            this.post = post;
            this.requestParams = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            ServerRequest request = new ServerRequest();

            if(post) {
                return request.postToUrl(url, requestParams);
            } else {
                return request.getFromUrl(url, requestParams);
            }
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            String message = "";
            boolean success = false;

            Bundle response = new Bundle();

            if(jObj == null) {
                Log.e(TAG, "Json object is null");
                response.putBoolean("error", true);
                response.putString("message", "An error occurred");
            } else {
                Log.i(TAG, "Got back:" + jObj.toString());

                try { success = jObj.getBoolean("success"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                try { message = jObj.getString("response"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                response.putBoolean("success", success);
                response.putString("message", message);
            }

            if(post) {
                // do after post
                activity.doUponCompletion(response);
            } else {
                // do after get
            }
        }
    }
}
