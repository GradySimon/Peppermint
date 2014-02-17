package com.gradysimon.peppermint;

import android.util.Log;

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
}
