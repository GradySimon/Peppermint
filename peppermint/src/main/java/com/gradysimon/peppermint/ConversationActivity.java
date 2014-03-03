package com.gradysimon.peppermint;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gradysimon.peppermint.datatype.Conversation;

import java.util.List;

public class ConversationActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter conversationAdapter = new MessageListAdapter(
                this,
                R.layout.conversation_list_item,
                UpstreamDataManager.getInstance().getConversationList()
        );
        setListAdapter(conversationAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Conversation conversation = ((MessageListAdapter) getListAdapter()).getItem(position);
        //Utils.launchConversationActivity(conversation);
    }

    // TODO: Observers and other dynamic update stuff.
    public static class MessageListAdapter extends ArrayAdapter<Conversation> {

        private final Context context;
        private final List<Conversation> conversations;

        public MessageListAdapter(Context context, int resource, List<Conversation> objects) {
            super(context, resource, objects);
            this.context = context;
            this.conversations = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Conversation conversation = conversations.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            // TODO: Use the convertView to recycle rows
            View conversationRow = (LinearLayout) inflater.inflate(R.layout.conversation_list_item, parent, false);
            // TODO: Update avatar too
            ((TextView) conversationRow.findViewById(R.id.topic_preview)).setText(conversation.getTopic().getText());
            Log.d("Test", "Returned one.");
            return conversationRow;
        }

        public int getViewTypeCount() {
            return 1;
        }
    }
}
