package com.thakurprojects.waterresourcemanager;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONParser {

    static JSONObject jObj = null;
    static String json = "";

    Logger logger;

    // constructor
    public JSONParser() {

    }

    // HTTP GET implementation
    public JSONObject getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();

            logger.addRecordToLog("ALL Details channel url : "+url);

            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            json=EntityUtils.toString(httpResponse.getEntity());

            logger.addRecordToLog("Response : "+json);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.addRecordToLog("HTTP GET Client Error : Unsupported Format ");
            return jObj;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            logger.addRecordToLog("HTTP GET Client Error : Client protocol exception ");
            return jObj;
        } catch (IOException e) {
            e.printStackTrace();
            logger.addRecordToLog("HTTP GET Client Error : IO Exception ");
            return jObj;
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            logger.addRecordToLog("JSON GET Parser : Error parsing data " + e.toString());
            return jObj;
        }

        // return JSON String
        return jObj;

    }

    // HTTP POST implementation
    public JSONObject postJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();

            logger.addRecordToLog("ALL Details channel url : "+url);

            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);

             json = EntityUtils.toString(httpResponse.getEntity());
            logger.addRecordToLog("Response : "+json);
            //HttpEntity httpEntity = httpResponse.getEntity();
            //is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.addRecordToLog("HTTP POST Client Error : Unsupported Format ");
            return jObj;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            logger.addRecordToLog("HTTP POST Client Error : Client protocol exception ");
            return jObj;
        } catch (IOException e) {
            e.printStackTrace();
            logger.addRecordToLog("HTTP POST Client Error : IO Exception ");
            return jObj;
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            logger.addRecordToLog("JSON POST Parser : Error parsing data " + e.toString());
            return jObj;
        }

        // return JSON String
        return jObj;

    }
}
