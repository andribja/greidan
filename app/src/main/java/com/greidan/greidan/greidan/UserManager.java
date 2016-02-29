package com.greidan.greidan.greidan;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
    static String loginUrl = "http://10.0.2.2:8080/login";
    static String registerUrl = "http://10.0.2.2:8080/register";

    public UserManager(Activity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences("AppPref", activity.MODE_PRIVATE);
    }

    public void register(String email, String password) {
        AuthTask registerTask = new AuthTask(activity, email, password, registerUrl);

        registerTask.execute();
    }

    public void login(String email, String password) {
        AuthTask loginTask = new AuthTask(activity, email, password, loginUrl);

        loginTask.execute();
    }

    public void logout() {
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
        String token = prefs.getString("token", "");
        Log.i("UserManager", token);
        return !token.equals("");
    }

    // An async task that tries to authenticate the user against the server
    private class AuthTask extends AsyncTask<Void, Void, JSONObject> {

        String email;
        String password;
        String url;
        LoginActivity loginActivity;
        RegisterActivity registerActivity;

        public AuthTask(Activity activity, String email, String password, String url) {
            this.email = email;
            this.password = password;
            this.url = url;

            try {
                loginActivity = (LoginActivity) activity;
            } catch (ClassCastException e) {
                Log.i("UserManager", "Can not cast to LoginActivity");
                registerActivity = (RegisterActivity) activity;
            }
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
                    prefs.edit().putString("token", token).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(loginActivity != null) {
                loginActivity.doAfterLoginAttempt(success, message);
            } else {
                registerActivity.doAfterRegisterAttempt(success, message);
            }
        }

        @Override
        protected void onCancelled() {
            if(loginActivity != null) {
                loginActivity.doAfterLoginAttempt(false, "Unknown error");
            } else {
                registerActivity.doAfterRegisterAttempt(false, "Unknown error");
            }
        }
    }
}
