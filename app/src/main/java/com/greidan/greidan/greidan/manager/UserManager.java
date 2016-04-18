package com.greidan.greidan.greidan.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.util.ServerRequest;
import com.greidan.greidan.greidan.model.User;
import com.greidan.greidan.greidan.activity.ProgressActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserManager {

    public static final String TAG = "UserManager";

    SharedPreferences prefs;
    Activity activity;
    String loginUrl;
    String logoutUrl;
    String registerUrl;
    String profileUrl;
    String imageUrl;

    public UserManager(Activity activity) {
        this.activity = activity;
        if(activity != null) {
            prefs = activity.getSharedPreferences("AppPref", Activity.MODE_PRIVATE);

            String host = activity.getString(R.string.host);
            String port = activity.getString(R.string.port);
            loginUrl = host + ":" + port + "/login";
            logoutUrl = host + ":" + port + "/logout";
            registerUrl = host + ":" + port + "/register";
            profileUrl = host + ":" + port + "/user";
            imageUrl = host + ":" + port + "/uploadUserImg";
        }
    }

    public void register(String username, String email, String password) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        UserTask registerTask = new UserTask((ProgressActivity) activity, registerUrl, params, true);

        registerTask.execute();
    }

    public void login(String username, String password) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        UserTask loginTask = new UserTask((ProgressActivity) activity, loginUrl, params, true);

        loginTask.execute();
    }

    public void logout() {
        //TODO: Invalidate session on server also?
        prefs.edit().putString("token", "").apply();
    }

    public static boolean isUsernameValid(String username) {
        // TODO: properly implement
        return username.length() >= 3;
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

    public String getLoggedInUsername() {
        return prefs.getString("username", "");
    }

    public void setProfileImagePath(String path) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("imagePath", path);
        editor.apply();
    }

    public String getProfileImagePath() {
        return prefs.getString("imagePath", null);
    }

//    public User findUserByUsername(String username) {
//        // TODO: Implement this; query database or contact server to find user
//        return new User(0, username, null);
//    }

//    public User getUserById(int id) {
//        // TODO: Implement this: query database or contact server to find user
////        return new User(id, "user_from_id", "userfrom@id.com");
//    }
    
    public void fetchLoggedInUserProfile() {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", getToken()));

        UserTask task = new UserTask((ProgressActivity) activity, profileUrl, params, false);
        task.execute();
    }

    public void fetchUserProfileById(String id) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("_id", id));

        UserTask task = new UserTask((ProgressActivity) activity, profileUrl, params, false);
        task.execute();
    }

    public void fetchUserProfileByUsername(String username) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));

        UserTask task = new UserTask((ProgressActivity) activity, profileUrl, params, false);
        task.execute();
    }

    public void postUserProfileUpdate(String username, String email, String extImageName) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("imagePath", "user_img" + "/" + extImageName));

        UserTask task = new UserTask((ProgressActivity) activity, profileUrl, params, true);
        task.execute();
    }

    public void uploadImage(File imageFile) {
        UserTask task = new UserTask((ProgressActivity) activity, imageUrl, "image", imageFile, "image/jpeg");
        task.execute();
    }

    private void handleRequestedData(JSONObject jObj, Bundle response) {

        if(jObj != null) {
            Iterator<?> keys = jObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();

                if(key.equals("userlist")) {
                    ArrayList<User> users = new ArrayList<>();
                    try {
                        JSONArray jArr = jObj.getJSONArray(key);
                        for(int i=0; i<jArr.length(); i++) {
                            users.add(new User(jArr.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    response.putParcelableArrayList("users", users);
                }

                if(key.equals("user")) {
                    try {
                        response.putParcelable("user", new User(jObj.getJSONObject(key)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ((ProgressActivity) activity).doUponCompletion(response);
    }

    // An async task that tries to authenticate the user against the server
    private class UserTask extends AsyncTask<Void, Void, JSONObject> {

        String url;
        List<NameValuePair> requestParams;
        boolean post;

        String paramName;
        File file;
        String fileType;

        ProgressActivity activity;

        public UserTask(ProgressActivity activity, String url, String paramName, File file, String fileType) {
            this.activity = activity;
            this.url = url;
            this.paramName = paramName;
            this.file = file;
            this.fileType = fileType;

            this.post = true;
        }

        public UserTask(ProgressActivity activity, String url, List<NameValuePair> params, boolean post) {
            this.url = url;
            this.requestParams = params;
            this.post = post;

            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            ServerRequest request = new ServerRequest();

            if(post) {
                if(file != null) {
                    Log.i(TAG, "Posting file " + file.getName());
                    return request.postFileToUrl(url, paramName, file, fileType);
                } else {
                    return request.postToUrl(url, requestParams);
                }
            } else {
                return request.getFromUrl(url, requestParams);
            }

        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            Bundle response = new Bundle();
            Boolean success = false;
            String message = "";

            if(jObj == null) {
                response.putBoolean("error", true);
            } else {
                Log.i(TAG, "Got back: " + jObj.toString());
                try { success = jObj.getBoolean("success"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                try { message = jObj.getString("response"); }
                catch (JSONException e) { e.printStackTrace(); }

                response.putBoolean("success", success);
                response.putString("message", message);
            }

            if(post) {
                if(url.equals(imageUrl)) {
                    Log.i(TAG, "Done uploading image");
                    String extFilename = null;
                    try { extFilename = jObj.getJSONObject("file").getString("filename"); }     // TODO: path or filename?
                    catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                    response.putString("extFilename", extFilename);
                } else {
                    if (success) {
                        try {
                            String token = jObj.getString("token");
                            String username = requestParams.get(0).getValue();  // Temp fix to get username

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", token);
                            editor.putString("username", username);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                activity.doUponCompletion(response);
            } else {
                handleRequestedData(jObj, response);
            }
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
