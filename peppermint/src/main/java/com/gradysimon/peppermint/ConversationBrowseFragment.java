package com.gradysimon.peppermint;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.sync.SyncUtils;
import com.gradysimon.peppermint.sync.UpstreamContract;

import java.util.List;

public class ConversationBrowseFragment extends ListFragment implements Navigable {

    private String title;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ConversationBrowseFragment newInstance() {
        ConversationBrowseFragment fragment = new ConversationBrowseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ConversationBrowseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_conversation_browse, container, false);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Utils.launchConversationActivity((int)id, getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        title = this.getResources().getString(getNavigationTitleStringId());
        ((MainActivity) activity).onSectionAttached(title);
        SyncUtils.createSyncAccount(activity);
        setListAdapter(new ConversationListAdapter(
                activity,
                GlobalApplication.getInstance().getLocalConversations(),
                true
            ));

    }

    @Override
    public int getNavigationTitleStringId() {
        return R.string.conversation_browse_fragment_title;
    }

    // TODO: Observers and other dynamic update stuff.
    public static class ConversationListAdapter extends CursorAdapter {


        public ConversationListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.conversation_list_item, null);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String topicText = (new Conversation(cursor)).getTopic(context).getText();
            TextView textView = (TextView) view.findViewById(R.id.topic_preview);
            textView.setText(topicText);
        }
    }
}
