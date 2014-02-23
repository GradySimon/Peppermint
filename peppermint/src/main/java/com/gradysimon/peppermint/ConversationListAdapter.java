package com.gradysimon.peppermint;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by grady on 2/22/14.
 */

// TODO: Observers and other dynamic update stuff.
public class ConversationListAdapter extends ArrayAdapter<Conversation> {

    private final Context context;
    private final List<Conversation> conversations;

    public ConversationListAdapter(Context context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        this.context = context;
        this.conversations = objects;
    }

    @Override
    public LinearLayout getView(int position, View convertView, ViewGroup parent) {
        Conversation conversation = conversations.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // TODO: Use the convertView to recycle rows
        LinearLayout conversationRow = (LinearLayout) inflater.inflate(R.layout.conversation_list_item, parent, false);
        // TODO: Update avatar too
        ((TextView) conversationRow.findViewById(R.id.topic_preview)).setText(conversation.getTopic().getText());
        Log.d("Test", "Returned one.");
        return conversationRow;
    }

    public int getViewTypeCount() {
        return 1;
    }
}
