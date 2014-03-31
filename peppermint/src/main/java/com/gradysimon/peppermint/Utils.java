package com.gradysimon.peppermint;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.gradysimon.peppermint.datatype.UserProfile;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by grady on 2/14/14.
 */
public class Utils {

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
        intent.putExtra(ConversationActivity.CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    public static void launchRegistrationActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    public static void showShortToast(String text, Context context) {
        showToast(text, Toast.LENGTH_SHORT, context);
    }

    public static void showLongToast(String text, Context context) {
        showToast(text, Toast.LENGTH_LONG, context);
    }

    public static void showToast(String text, int duration, Context context) {
        Toast.makeText(context, text, duration).show();
    }
}
