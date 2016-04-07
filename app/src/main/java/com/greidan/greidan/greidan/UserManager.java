package com.greidan.greidan.greidan;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserManager {

    SharedPreferences prefs;
    Activity activity;
    String loginUrl;
    String registerUrl;

    public UserManager(Activity activity) {
        this.activity = activity;
        if(activity != null) {
            prefs = activity.getSharedPreferences("AppPref", Activity.MODE_PRIVATE);

            String host = activity.getString(R.string.host);
            String port = activity.getString(R.string.port);
            loginUrl = host + ":" + port + "/login";
            registerUrl = host + ":" + port + "/register";
        }
    }

    public void register(String email, String password) {
        AuthTask registerTask = new AuthTask((ProgressActivity) activity, email, password, registerUrl);

        registerTask.execute();
    }

    public void login(String email, String password) {
        AuthTask loginTask = new AuthTask((ProgressActivity) activity, email, password, loginUrl);

        loginTask.execute();
    }

    public void logout() {
        //TODO: Invalidate session on server also?
        prefs.edit().putString("token", "").apply();
    }

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public boolean isLoggedIn() {
        // TODO: Is this good enough?
        String token = prefs.getString("token", "");
        Log.i("UserManager", token);
        return !token.equals("");
    }

    public String getToken() {
        return prefs.getString("token", "");
    }

    public User getLoggedInUser() {
        // TODO: properly implement
        return new User(1337, "temp_user", "temp@user.com");
    }

    public String getLoggedInUsername() {
        return prefs.getString("username", "");
    }

    public User findUserByUsername(String username) {
        // TODO: Implement this; query database or contact server to find user
        return new User(0, username, null);
    }

    public User getUserById(int id) {
        // TODO: Implement this: query database or contact server to find user
        return new User(id, "user_from_id", "userfrom@id.com");
    }

    // An async task that tries to authenticate the user against the server
    private class AuthTask extends AsyncTask<Void, Void, JSONObject> {

        String email;
        String password;
        String url;
        ProgressActivity activity;

        public AuthTask(ProgressActivity activity, String email, String password, String url) {
            this.email = email;
            this.password = password;
            this.url = url;

            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("email", email));
            requestParams.add(new BasicNameValuePair("password", password));

            ServerRequest request = new ServerRequest();
            return request.postToUrl(url, requestParams);
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            Boolean success = false;
            String message = "";

            try { success = jObj.getBoolean("success"); }
            catch (JSONException | NullPointerException e) { e.printStackTrace(); }

            try { message = jObj.getString("response"); }
            catch (JSONException e) { e.printStackTrace(); }

            if(success) {
                try {
                    String token = jObj.getString("token");
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);
                    editor.putString("username", email);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Bundle data = new Bundle();
            data.putBoolean("success", success);
            data.putString("message", message);
            activity.doUponCompletion(data);
        }

        @Override
        protected void onCancelled() {
            Bundle data = new Bundle();
            data.putBoolean("success", false);
            data.putString("message", "Error");
            activity.doUponCompletion(data);
        }
    }
}
