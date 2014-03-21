package com.gradysimon.peppermint;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.sync.SyncUtils;
import com.gradysimon.peppermint.sync.UpstreamAuthenticatorService;
import com.gradysimon.peppermint.sync.UpstreamContentProvider;

import java.util.List;

public class TopicBrowseFragment extends Fragment implements Navigable{
    private TextView topicTextView;
    private Button topicPositiveButton;
    private Button topicNegativeButton;
    private TextView topicAuthorNameTextView;

    private Topic currentTopic;

    private Account account;

    private String title;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TopicBrowseFragment newInstance() {
        TopicBrowseFragment fragment = new TopicBrowseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TopicBrowseFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topic_browse, container, false);
        initializeViews(rootView);
        registerEventListeners();
        return rootView;
    }

    private void requestSync() {
        Bundle syncSettings = new Bundle();
        syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, UpstreamContentProvider.AUTHORITY, syncSettings);
    }

    private void registerEventListeners() {
        topicPositiveButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               topicPositiveButtonPressed();
           }
        });
        topicNegativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                topicNegativeButtonPressed();
            }
        });
    }

    private void topicPositiveButtonPressed() {
        currentTopic.markAsSeen();
        currentTopic.save(getActivity());
        launchConversation(currentTopic);
    }

    private void topicNegativeButtonPressed() {
        currentTopic.markAsSeen();
        currentTopic.save(getActivity());
        (new ShowNextTopicTask()).execute();
    }

    private void launchConversation(Topic currentTopic) {
        Conversation conversation = new Conversation(currentTopic);
        conversation.save(getActivity());
        (new ShowNextTopicTask()).execute();
        Utils.launchConversationActivity(conversation.getLocalId(), getActivity());
    }

    private void initializeViews(View rootView) {
        topicTextView = (TextView) rootView.findViewById(R.id.topic_text);
        topicAuthorNameTextView = (TextView) rootView.findViewById(R.id.topic_author_name);
        topicPositiveButton = (Button) rootView.findViewById((R.id.topic_positive_button));
        topicNegativeButton = (Button) rootView.findViewById(R.id.topic_negative_button);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        title = getString(getNavigationTitleStringId());
        ((MainActivity) activity).onSectionAttached(title);
        account = SyncUtils.createSyncAccount(activity);
    }

    @Override
    public int getNavigationTitleStringId() {
        return R.string.topic_browse_fragment_title;
    }

    private class ShowNextTopicTask extends AsyncTask<Context, Void, Topic> {

        @Override
        protected Topic doInBackground(Context... contexts) {
            List<Topic> topics = Topic.getBestTopics(contexts[0]);
            return topics.get(0);
        }

        protected void onPostExecute(Topic result) {
            currentTopic = result;
            if (currentTopic == null) {
                topicTextView.setText(getResources().getText(R.string.no_data_topic));
                topicAuthorNameTextView.setText(getResources().getText(R.string.no_data_name));
            } else {
                topicTextView.setText(result.getText());
                topicAuthorNameTextView.setText(result.getAuthor(getActivity()).getWholeName());
            }
        }
    }
}
