package com.gradysimon.peppermint;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.datatype.Message;

/**
 * Created by grady on 2/22/14.
 */
public class InboundMessage extends Message {
    Conversation conversation;

    public InboundMessage() {
        // no args constructor for gson
    }

    public InboundMessage(Conversation conversation, String text) {
        this.conversation = conversation;
        this.text = text;
    }
}
