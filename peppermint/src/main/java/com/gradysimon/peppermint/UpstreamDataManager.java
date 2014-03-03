package com.gradysimon.peppermint;

import android.util.Log;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.sync.JsonApiManager;

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
        List<Topic> topicList = JsonApiManager.getTopicList();
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
        topicQueue().addAll(getTopicListFromUpstream());
    }

    public Topic getNextTopic() {
        if (topicQueue().isEmpty()) {
            refreshTopicQueue();
        }
        return topicQueue().poll();
    }

    private UpstreamDataManager() {

    }

    public void indicateTopicPositive(Topic currentTopic) {
        // TODO: call JSON API to indicate interest in this topic
    }

    public void indicateTopicNegative(Topic currentTopic) {
        // TODO: call JSON API to indicate boredom with this topic
    }

    public List<Conversation> getConversationList() {
        // TODO: Implement this for real. Don't use fake conversations.
        return DataFaker.getFakeConversationList();
    }
}
