package com.gradysimon.peppermint;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles individual API interactions against the server, using JSON.
 *
 * Created by grady on 2/14/14.
 */
public class JsonApiManager {

    private final static String API_BASE = "http://10.0.2.2:8000/";
    private final static String LIST_TOPICS = API_BASE + "topics/";

    public static List<Topic> getTopicList() {
        String jsonString;
        try {
            jsonString = apiGet(LIST_TOPICS, "");
        } catch (MalformedURLException e) {
            Log.e("Server Interaction", "Got MalformedURLException while trying to read from the Topic listing API endpoint.", e);
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Type topicListType = new TypeToken<List<Topic>>(){}.getType();
        Gson gson = gsonBuilder.create();
        List<Topic> topicList = gson.fromJson(jsonString, topicListType);
        if (topicList == null) {
            return new ArrayList<Topic>();
        } else {
            return topicList;
        }
    }

    private static String apiGet(String apiEndpoint, String apiSuffix) throws MalformedURLException {
        String result;
        URL apiCallURL = new URL(apiEndpoint + apiSuffix);
        try {
            HttpURLConnection apiConnection = (HttpURLConnection) apiCallURL.openConnection();
            apiConnection.setRequestMethod("GET");
            apiConnection.setRequestProperty("Accept", "application/json");
            InputStream in = new BufferedInputStream(apiConnection.getInputStream());
            result = Utils.inputStreamToString(in);
        } catch (IOException e) {
            Log.e("Server Interaction", "Error opening connection to JSON API.", e);
            return null;
        }
        return result;
    }
}
