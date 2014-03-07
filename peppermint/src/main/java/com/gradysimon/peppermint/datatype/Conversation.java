package com.gradysimon.peppermint.datatype;

import com.gradysimon.peppermint.sync.Synchronizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grady on 2/22/14.
 */
public class Conversation implements Synchronizable{
    private int localId;
    private int uuid;
    private int topicUuid
    private int counterPartyUuid;


    public Topic getTopic() {
        return topic;
    }

    public Conversation() {
        // no arg constructor for gson
    }

    public Conversation(Topic topic, UserProfile counterParty, List<Message> messages) {
        this.topic = topic;
        this.messages = messages;
        this.counterParty = counterParty;
    }

    public Conversation(Topic topic, UserProfile counterParty) {
        this.topic = topic;
        this.messages = new ArrayList<Message>();
        this.counterParty = counterParty;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public UserProfile getCounterParty() {
        return counterParty;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
