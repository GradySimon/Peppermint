package com.gradysimon.peppermint.sync;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gradysimon.peppermint.Utils;
import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.datatype.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    private final static String TOPIC_ENDPOINT = API_BASE + "topics/";
    private final static String USER_PROFILE_ENDPOINT = API_BASE + "userprofiles/";

    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    public static List<Topic> getTopicList() {
        String jsonString = apiGet(TOPIC_ENDPOINT, "");
        return parseTopicList(jsonString);
    }

    public static Topic postTopic(Topic topic) {
        Topic.UpstreamRepresentation upstreamRepresentation = topic.toUpstreamRepresentation();
        Gson gson = getGson();
        String topicJson = gson.toJson(upstreamRepresentation);
        String responseJson = apiPost(TOPIC_ENDPOINT, topicJson);
        return parseTopic(responseJson);
    }

    public static List<UserProfile> getUserProfileList() {
        String jsonString = apiGet(USER_PROFILE_ENDPOINT, "");
        return parseUserProfileList(jsonString);
    }

    public static UserProfile getUserProfileDetail(int id) {
        String jsonString = apiGet(USER_PROFILE_ENDPOINT, Integer.toString(id));
        return parseUserProfile(jsonString);
    }

    /**
     * POSTs the UserProfile to the API, returns the UUID the server assigned.
     * @param userProfile
     * @return the UUID assigned by the server to the UserProfile
     */
    public static UserProfile postUserProfile(UserProfile userProfile) {
        UserProfile.UpstreamRepresentation upstreamRepresentation = userProfile.toUpstreamRepresentation();
        Gson gson = getGson();
        String userProfileJson = gson.toJson(upstreamRepresentation);
        String responseJson = apiPost(USER_PROFILE_ENDPOINT, userProfileJson);
        return parseUserProfile(responseJson);
    }

    private static Topic parseTopic(String json) {
        Gson gson = getGson();
        Type topicType = new TypeToken<Topic.UpstreamRepresentation>(){}.getType();
        Topic.UpstreamRepresentation upstreamRepresentation = gson.fromJson(json, topicType);
        return upstreamRepresentation.toTopic();
    }

    public static List<Topic> parseTopicList(String json) {
        Gson gson = getGson();
        Type topicListType = new TypeToken<List<Topic.UpstreamRepresentation>>(){}.getType();
        List<Topic.UpstreamRepresentation> upstreamRepresentations = gson.fromJson(json, topicListType);
        List<Topic> topics = new ArrayList<>();
        for (Topic.UpstreamRepresentation upstreamRepresentation : upstreamRepresentations) {
            topics.add(upstreamRepresentation.toTopic());
        }
        return topics;
    }

    private static UserProfile parseUserProfile(String json) {
        Gson gson = getGson();
        Type userProfileType = new TypeToken<UserProfile.UpstreamRepresentation>(){}.getType();
        UserProfile.UpstreamRepresentation upstreamRepresentation = gson.fromJson(json, userProfileType);
        return upstreamRepresentation.toUserProfile();
    }

    public static List<UserProfile> parseUserProfileList(String json) {
        Gson gson = getGson();
        Type userProfileListType = new TypeToken<List<UserProfile.UpstreamRepresentation>>(){}.getType();
        List<UserProfile.UpstreamRepresentation> upstreamRepresentations = gson.fromJson(json, userProfileListType);
        List<UserProfile> userProfiles = new ArrayList<>();
        for (UserProfile.UpstreamRepresentation upstreamRepresentation : upstreamRepresentations) {
            userProfiles.add(upstreamRepresentation.toUserProfile());
        }
        return userProfiles;
    }

    private static String apiGet(String apiEndpoint, String apiSuffix) {
        URL apiCallURL = buildUrl(apiEndpoint + apiSuffix);
        HttpURLConnection apiConnection = getApiConnection(apiCallURL, "GET", false);
        return getResponse(apiConnection);
    }

    private static String apiPost(String apiEndpoint, String json) {
        URL apiCallURL = buildUrl(apiEndpoint);
        HttpURLConnection apiConnection = getApiConnection(apiCallURL, "POST", true);
        writeOverConnection(apiConnection, json);
        return getResponse(apiConnection);
    }

    private static void writeOverConnection(HttpURLConnection connection, String json) {
        Log.i("Server Interaction", "Attempting to write JSON: " + json);
        try {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.e("Server Interaction", "Got IOException while trying to write over HTTP connection.", e);
        }
    }

    private static URL buildUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.e("Server Interaction", "Got MalformedURLException while trying to build API endpoint URL: " + url, e);
            return null;
        }
    }

    private static String getResponse(HttpURLConnection connection) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            Log.e("Server Interaction", "Error reading response from API.", e);
        }
        return Utils.inputStreamToString(in);
    }

    private static HttpURLConnection getApiConnection(URL url, String method, boolean requiresOutput) {
        try {
            HttpURLConnection apiConnection = (HttpURLConnection) url.openConnection();
            apiConnection.setRequestMethod(method);
            apiConnection.setRequestProperty("Accept", "application/json");
            if (requiresOutput) {
                apiConnection.setRequestProperty("Content-Type", "application/json");
                apiConnection.setDoOutput(true);
            }
            apiConnection.connect();
            return apiConnection;
        } catch (IOException e) {
            Log.e("Server Interaction", "Error opening connection to JSON API.", e);
            return null;
        }
    }
}
