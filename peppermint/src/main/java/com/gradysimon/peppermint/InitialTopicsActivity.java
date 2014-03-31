package com.gradysimon.peppermint;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gradysimon.peppermint.datatype.Topic;


public class InitialTopicsActivity extends Activity {

    TextView encouragementTextView;
    EditText topicEntryEditText;
    Button submitTopicButton;
    Button doneButton;

    int topicsSubmitted = 0;

    // TODO: use xml strings
    private String[] responseMessages = {
            "Mmm. Delicious. Two more to go.",
            "Oh man, even better than the last! One more to go.",
            "Thanks! Feel free to add more!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_topics);
        registerViews();
        registerEventListeners();
    }

    private void registerViews() {
        encouragementTextView = (TextView) findViewById(R.id.encouragement_text);
        topicEntryEditText = (EditText) findViewById(R.id.topic_text_edditext);
        submitTopicButton = (Button) findViewById(R.id.submit_topic_button);
        doneButton = (Button) findViewById(R.id.done_button);
    }

    private void registerEventListeners() {
        submitTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTopicButtonPressed();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneButtonPressed();
            }
        });
    }

    // TODO: get rid of the magic values and clean this up.
    private void submitTopicButtonPressed() {
        String enteredText = topicEntryEditText.getText().toString();
        if (enteredText.length() < 3) {
            Utils.showLongToast("C'mon. It's gotta be longer than that.", this);
            return;
        }
        Topic newTopic = Topic.createNewTopic(enteredText);
        newTopic.save(this);
        topicEntryEditText.setText("");
        String responseMessage = responseMessages[Math.min(topicsSubmitted++, 2)];
        Utils.showShortToast(responseMessage, this);
        if (topicsSubmitted >= 3)
            doneButton.setEnabled(true);
    }

    private void doneButtonPressed() {
        GlobalApplication.getInstance().requestImmediateSync();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.initial_topics, menu);
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

}
