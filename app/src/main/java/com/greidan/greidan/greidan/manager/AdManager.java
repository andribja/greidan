package com.greidan.greidan.greidan.manager;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.util.ServerRequest;
import com.greidan.greidan.greidan.activity.ProgressActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdManager {

    private static final String TAG = "AdManager";

    UserManager userManager;

    Activity activity;

    String adUrl;
    String imageUploadUrl;
    String imageUrl;
    String userAdsUrl;

    public AdManager(Activity activity) {
        this.activity = activity;
        userManager = new UserManager(activity);

        if(activity != null) {
            String host = activity.getString(R.string.host);
            String port = activity.getString(R.string.port);

            adUrl = host + ":" + port + "/ad";
            imageUploadUrl = host + ":" + port + "/uploadAdImg";
            imageUrl = host + ":" + port + "/ad_img";
            userAdsUrl = host + ":" + port + "/userAds";
        }
    }

    public void postAdToServer(Ad ad) {
        ArrayList<NameValuePair> requestParams = ad.getAsRequestParams();
        requestParams.add(new BasicNameValuePair("token", userManager.getToken()));

        ServerTask task = new ServerTask((ProgressActivity) activity, adUrl, true, requestParams);
        task.execute();
    }

    public void fetchAds(String category, Location location, double radius) {
        ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("category", category));
        requestParams.add(new BasicNameValuePair("maxDistance", Double.toString(radius)));

        if(location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Log.i("AdManager", "Location: " + lat + ", " + lng);

            requestParams.add(new BasicNameValuePair("lat", Double.toString(lat)));
            requestParams.add(new BasicNameValuePair("lng", Double.toString(lng)));
        }

        ServerTask task = new ServerTask((ProgressActivity) activity, adUrl, false, requestParams);
        task.execute();
    }

    public void fetchAdsByToken() {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", userManager.getToken())); // INSECURE

        ServerTask task = new ServerTask((ProgressActivity) activity, adUrl, false, params);
        task.execute();
    }

    public void fetchAdsByUsername(String username) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("author_name", username));

        ServerTask task = new ServerTask((ProgressActivity) activity, userAdsUrl, false, params);
        task.execute();
    }

    public void fetchCategories() {
        // TODO: properly implement
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Handverk");
        categories.add("Skutl");
        categories.add("Lán á tólum");
        categories.add("Forritun");
        categories.add("Annað");

        Bundle data = new Bundle();
        data.putSerializable("categories", categories);
        ((ProgressActivity) activity).doUponCompletion(data);
    }

    public void uploadImage(File imageFile) {
        ServerTask task = new ServerTask((ProgressActivity) activity, imageUploadUrl, "image", imageFile, "image/jpeg");
        task.execute();
    }

    public void fetchImageBytes(String filename) {
        String url = imageUrl + "/" + filename;
        BytesTask task = new BytesTask((ProgressActivity) activity, url, null);
        task.execute();
    }

    private void handleRequestedData(JSONObject jObj, Bundle response) {
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

        response.putParcelableArrayList("ads", ads);
        ((ProgressActivity) activity).doUponCompletion(response);
    }

    private class BytesTask extends AsyncTask<Void, Void, byte[]> {

        ProgressActivity activity;
        String url;
        List<NameValuePair> requestParams;

        public BytesTask(ProgressActivity activity, String url, List<NameValuePair> params) {
            this.activity = activity;
            this.url = url;
            this.requestParams = params;
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            ServerRequest request = new ServerRequest();

            return request.getBytesFromUrl(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Log.i("BytesTask", "Received " + bytes.length);

            Bundle response = new Bundle();
            response.putByteArray("bytes", bytes);

            activity.doUponCompletion(response);
        }
    }

    private class ServerTask extends AsyncTask<Void, Void, JSONObject> {

        ProgressActivity activity;
        List<NameValuePair> requestParams;
        String url;
        boolean post;

        String paramName;
        File file;
        String fileType;

        public ServerTask(ProgressActivity activity, String url, String paramName, File file, String fileType) {
            this.activity = activity;
            this.url = url;
            this.paramName = paramName;
            this.file = file;
            this.fileType = fileType;

            this.post = true;
        }

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
                if(file != null) {
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
            boolean success = false;
            String message = "";
            Bundle data = new Bundle();

            if(jObj == null) {
                Log.e("AdListActivity", "Json object is null");
                data.putBoolean("error", true);
                data.putString("message", "An error occurred");
            } else {
                Log.i(TAG, "Got back:" + jObj.toString());

                try { success = jObj.getBoolean("success"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                try { message = jObj.getString("response"); }
                catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                data.putBoolean("success", success);
                data.putString("message", message);
            }

            if(post) {
                if(file != null) {
                    String extFilename = null;
                    try { extFilename = jObj.getJSONObject("file").getString("filename"); }     // TODO: path or filename?
                    catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                    data.putString("extFilename", extFilename);
                } else {
                    String id = "";

                    try { id = jObj.getString("_id"); }
                    catch (JSONException | NullPointerException e) { e.printStackTrace(); }

                    data.putString("id", id);
                }

                activity.doUponCompletion(data);
            } else {
                handleRequestedData(jObj, data);
            }
        }
    }
}
