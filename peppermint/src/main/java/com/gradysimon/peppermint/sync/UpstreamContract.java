package com.gradysimon.peppermint.sync;

import android.database.Cursor;
import android.net.Uri;

import com.gradysimon.peppermint.datatype.UserProfile;

import java.net.URI;
import java.util.List;

/**
* Created by grady on 2/27/14.
*/
public final class UpstreamContract {
    public static final String AUTHORITY = UpstreamContentProvider.AUTHORITY;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class UserProfile {
        public static final String TABLE = "userprofile";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE).build();

        public static final int ID_COL = 0;
        public static final int UUID_COL = 1;
        public static final int FIRST_NAME_COL = 2;
        public static final int LAST_NAME_COL = 3;

        public static final String ID = "_id";
        public static final String UUID = "uuid";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";

        public static Uri uuidUri(int uuid) {
            return CONTENT_URI.buildUpon().appendPath("uuid").appendPath(Integer.toString(uuid)).build();
        }

        public static Uri localIdUri(int localId) {
            return CONTENT_URI.buildUpon().appendPath("localid").appendPath(Integer.toString(localId)).build();
        }
    }

    // TODO: Write DatatypeContract interface/abstract class
    public static class Topic {
        public static final String TABLE = "topic";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE).build();

        public static final int ID_COL = 0;
        public static final int UUID_COL = 1;
        public static final int AUTHOR_UUID_COL = 2;
        public static final int TEXT_COL = 3;
        public static final int LAST_MODIFIED_COL = 4;
        public static final int INTERESTED_COL = 5;
        public static final int SEEN_COL = 6;


        public static final String ID = "_id";
        public static final String UUID = "uuid";
        public static final String AUTHOR_UUID = "author_uuid";
        public static final String TEXT = "text";
        public static final String LAST_MODIFIED = "last_modified";
        public static final String INTERESTED = "interested";
        public static final String SEEN = "seen";

        public static Uri uuidUri(int uuid) {
            return CONTENT_URI.buildUpon().appendPath("uuid").appendPath(Integer.toString(uuid)).build();
        }

        public static Uri localIdUri(int localId) {
            return CONTENT_URI.buildUpon().appendPath("localid").appendPath(Integer.toString(localId)).build();
        }
    }
}
