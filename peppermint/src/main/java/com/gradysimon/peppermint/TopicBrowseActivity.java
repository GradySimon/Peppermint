package com.gradysimon.peppermint;

import android.app.Activity;
;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.TextView;

public class TopicBrowseActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_browse);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, BrowseFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.browse, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BrowseFragment extends Fragment {
        private TextView topicTextView;
        private Button topicPositiveButton;
        private Button topicNegativeButton;
        private TextView topicAuthorNameTextView;

        private Topic currentTopic;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BrowseFragment newInstance(int sectionNumber) {
            BrowseFragment fragment = new BrowseFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public BrowseFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_topic_browse, container, false);
            initializeViews(rootView);
            registerEventListeners();
            new ShowNextTopicTask().execute();
            return rootView;
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
            UpstreamDataManager.getInstance().indicateTopicPositive(currentTopic);
            launchConversation(currentTopic);
            (new ShowNextTopicTask()).execute();
        }

        private void topicNegativeButtonPressed() {
            UpstreamDataManager.getInstance().indicateTopicNegative(currentTopic);
            (new ShowNextTopicTask()).execute();
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
            ((TopicBrowseActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        private class ShowNextTopicTask extends AsyncTask<Void, Void, Topic> {

            @Override
            protected Topic doInBackground(Void... voids) {
                UpstreamDataManager dataManager = UpstreamDataManager.getInstance();
                Topic topic = dataManager.getNextTopic();
                return topic;
            }

            protected  void onPostExecute(Topic result) {
                currentTopic = result;
                if (currentTopic == null) {
                    topicTextView.setText(getResources().getText(R.string.no_data_topic));
                    topicAuthorNameTextView.setText(getResources().getText(R.string.no_data_name));
                } else {
                    topicTextView.setText(result.getText());
                    topicAuthorNameTextView.setText(result.getAuthor().getWholeName());
                }
            }
        }
    }

}
