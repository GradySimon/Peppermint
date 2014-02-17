package com.gradysimon.peppermint;

import android.util.Log;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by grady on 2/14/14.
 */
public class UpstreamDataManager {

    private static UpstreamDataManager instance;

    public static UpstreamDataManager getInstance() {
        if (instance == null) {
            instance = new UpstreamDataManager();
        }
        return instance;
    }

    public static List<Topic> getTopicListFromUpstream() {
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

    private Queue<Topic> topicQueue;

    private Queue<Topic> topicQueue() {
        if (topicQueue == null) {
            topicQueue = new PriorityQueue<Topic>();
            refreshTopicQueue();
        }
        return topicQueue;
    }

    private void refreshTopicQueue() {
        topicQueue().clear();
        topicQueue().addAll(getTopicListFromUpstream())
    }

    public Topic getNextTopic() {
        if (topicQueue().isEmpty()) {
            refreshTopicQueue();
        }
        return topicQueue().poll();
    }

    private UpstreamDataManager() {

    }
}
