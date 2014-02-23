package com.gradysimon.peppermint;

/**
 * Created by grady on 2/22/14.
 */
public class OutboundMessage extends Message {
    Conversation conversation;

    public OutboundMessage() {
        // no args constructor for gson
    }

    public OutboundMessage(Conversation conversation, String text) {
        this.conversation = conversation;
        this.text = text;
    }
}
