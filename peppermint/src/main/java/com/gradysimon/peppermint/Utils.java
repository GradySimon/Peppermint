package com.gradysimon.peppermint;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gradysimon.peppermint.datatype.UserProfile;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by grady on 2/14/14.
 */
public class Utils {
    public static final String CONVERSATION_ID = "conversation_id";

    public static String inputStreamToString(InputStream inputStream) {
        String returnString = null;
        try {
            returnString = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            Log.e("Misc.", "Got IOException when trying to read an InputStream into a String.", e);
        }
        return returnString;
    }

    public static UserProfile getAppUserProfile() {
        return null;
    }

    public static void launchConversationActivity(int conversationId, Context context) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    public static void launchRegistrationActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
}
