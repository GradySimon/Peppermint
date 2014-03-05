package com.gradysimon.peppermint;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.sync.SyncUtils;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        title = this.getResources().getString(getNavigationTitleStringId());
        ((MainActivity) activity).onSectionAttached(title);
        SyncUtils.createSyncAccount(activity);
        setListAdapter(new ConversationListAdapter(
                this.getActivity(),
                R.layout.conversation_list_item,
                
                ));
    }

    @Override
    public int getNavigationTitleStringId() {
        return R.string.conversation_browse_fragment_title;
    }

    // TODO: Observers and other dynamic update stuff.
    public static class ConversationListAdapter extends ArrayAdapter<Conversation> {

        private final Context context;
        private final List<Conversation> conversations;

        public ConversationListAdapter(Context context, int resource, List<Conversation> objects) {
            super(context, resource, objects);
            this.context = context;
            this.conversations = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Conversation conversation = conversations.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            // TODO: Use the convertView to recycle rows
            View conversationRow = (LinearLayout) inflater.inflate(R.layout.conversation_list_item, parent, false);
            // TODO: Update avatar too
            ((TextView) conversationRow.findViewById(R.id.topic_preview)).setText(conversation.getTopic().getText());
            return conversationRow;
        }

        public int getViewTypeCount() {
            return 1;
        }
    }
}
