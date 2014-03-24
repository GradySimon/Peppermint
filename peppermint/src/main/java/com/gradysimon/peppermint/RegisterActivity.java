package com.gradysimon.peppermint;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    Button submitButton;
    EditText firstNameEditText;
    EditText lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupViews();
    }

    private void setupViews() {
        submitButton = (Button) this.findViewById(R.id.submit_button);
        firstNameEditText = (EditText) this.findViewById(R.id.first_name_edittext);
        lastNameEditText = (EditText) this.findViewById(R.id.last_name_edittext);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonClicked();
            }
        });
    }

    private void submitButtonClicked() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        if (validateRegistration(firstName, lastName)) {
            GlobalApplication.getInstance().registerUserProfile(firstName, lastName);
            finish();
        } else {
            clearInputs();
            Toast.makeText(this, R.string.registration_invalid, 2);
        }
    }

    private void clearInputs() {
        firstNameEditText.clearComposingText();
        lastNameEditText.clearComposingText();
    }

    private boolean validateRegistration(String firstName, String lastName) {
        if (firstName.length() == 0 || lastName.length() == 0) {
            return false;
        }
        // TODO: check for invalid characters
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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
