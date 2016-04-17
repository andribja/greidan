package com.greidan.greidan.greidan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ServerRequest {

    private static final String TAG = "ServerRequest";
    private static final int CONNECTION_TIMEOUT = 3000;

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public ServerRequest() {

    }

    public JSONObject postFileToUrl(String url, String paramName, File file, String fileType) {
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.addBinaryBody(paramName, file, ContentType.create(fileType), file.getName());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(multipartEntity.build());

        return executeRequest(httpPost);
    }

    public JSONObject getFromUrl(String url, List<NameValuePair> params) {
        String encodedUrl = url + "/?" + URLEncodedUtils.format(params, "utf-8");
        HttpGet httpGet = new HttpGet(encodedUrl);

        return executeRequest(httpGet);
    }

    public JSONObject postToUrl(String url, List<NameValuePair> params) {
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return executeRequest(httpPost);
    }

    public JSONObject executeRequest(HttpRequestBase requestBase) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);

        DefaultHttpClient httpClient = new DefaultHttpClient(params);
        Log.i(TAG, requestBase.getURI().toString());

        try {
            HttpResponse httpResponse = httpClient.execute(requestBase);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (ConnectTimeoutException e) {
            Log.i(TAG, "Connection timed out");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("n");
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jObj;
    }
}