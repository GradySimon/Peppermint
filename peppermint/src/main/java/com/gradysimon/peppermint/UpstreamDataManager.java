package com.gradysimon.peppermint;

import android.util.Log;

import java.util.List;

/**
 * Created by grady on 2/14/14.
 */
public class UpstreamDataManager {

    public static List<Topic> getTopicList() {
        Log.i("Server Interaction", "Initiating GET against the topic list API using JSON.");
        List<Topic> topicList = JsonApiManager.getTopicList();
        Log.i("Server Interaction", "GET against the topic list API using JSON complete");
        if (topicList == null) {
            Log.w("Server Interaction", "Got null topicList from JsonApiManager.");
        }
        if (topicList.isEmpty()) {
            Log.w("Server Interaction", "Got no results from JSON API on getTopicList().");
        }
        return topicList;
    }
}
