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

import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.sync.SyncUtils;
import com.gradysimon.peppermint.sync.UpstreamAuthenticatorService;
import com.gradysimon.peppermint.sync.UpstreamContentProvider;

public class TopicBrowseFragment extends Fragment {
    private TextView topicTextView;
    private Button topicPositiveButton;
    private Button topicNegativeButton;
    private TextView topicAuthorNameTextView;

    private Topic currentTopic;

    public final String TITLE = this.getResources().getString(R.string.topic_browse_fragment_title);

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
        View rootView = inflater.inflate(R.layout.fragment_conversation_browse, container, false);
        initializeViews(rootView);
        registerEventListeners();
        return rootView;
    }

    private static Account createSyncAccount(Context context) {
        Account account = UpstreamAuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, UpstreamContentProvider.AUTHORITY, 1);
            Log.i("Accounts", "Account successfully added.");
            return account;
        } else {
            Log.e("Accounts", "Failed to add account.");
            return null;
        }
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
        (new ShowNextTopicTask()).execute();
    }

    private void topicNegativeButtonPressed() {
        requestSync();
    }

    private void launchConversation(Topic currentTopic) {

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
        ((MainActivity) activity).onSectionAttached(TITLE);
        SyncUtils.createSyncAccount(activity);
    }

    private class ShowNextTopicTask extends AsyncTask<Void, Void, Topic> {

        @Override
        protected Topic doInBackground(Void... voids) {
            Topic topic = new Topic(2, "Sunglasses are REALLY awesome.");
            topic.save(getActivity());
            return null;
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
