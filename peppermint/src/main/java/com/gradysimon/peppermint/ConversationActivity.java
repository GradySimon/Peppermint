package com.gradysimon.peppermint;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.datatype.Message;
import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.sync.UpstreamContract;


public class ConversationActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String CONVERSATION_ID = "conversation_id";

    Conversation conversation;

    CursorAdapter messageListAdapter;

    ListView messageListView;
    EditText newMessageEditText;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        getConversationInstance();
        registerViews();
        setupMessageList();
        setupSendButton();
        getLoaderManager().initLoader(0, null, this);
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void registerViews() {
        messageListView = (ListView) this.findViewById(R.id.message_list);
        newMessageEditText = (EditText) this.findViewById(R.id.new_message_edittext);
        sendButton = (Button) this.findViewById(R.id.send_button);
    }

    private void setupMessageList() {
        messageListAdapter = new MessageListAdapter(this, null, 0);
        messageListView.setAdapter(messageListAdapter);
    }

    private void getConversationInstance() {
        int conversationId = getIntent().getIntExtra(CONVERSATION_ID, -1);
        if (conversationId == -1) {
            conversation = null;
        } else {
            conversation = Conversation.getByLocalId(conversationId, this);
        }
    }

    private void sendMessage() {
        String messageText = newMessageEditText.getText().toString();
        if (messageText.length() > 0) {
            Message newMessage = Message.newOutboundMessage(messageText, conversation);
            newMessage.save(this);
            GlobalApplication.getInstance().requestImmediateSync();
            newMessageEditText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation, menu);
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
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, conversation.getMessageUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        messageListAdapter.swapCursor(cursor);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        messageListAdapter.swapCursor(null);
    }

    public static class MessageListAdapter extends CursorAdapter {
        public MessageListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            Message message = new Message(cursor);
            View view;
            if (message.isFromCounterparty()) {
                view = LayoutInflater.from(context).inflate(R.layout.message_from, null);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.message_to, null);
            }
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String messageText = (new Message(cursor)).getContent();
            TextView textView = (TextView) view.findViewById(R.id.message_text);
            textView.setText(messageText);
        }
    }
}
