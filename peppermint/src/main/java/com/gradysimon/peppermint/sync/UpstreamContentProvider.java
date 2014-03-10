package com.gradysimon.peppermint.sync;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class UpstreamContentProvider extends ContentProvider {
    // AUTHORITY must match AndroidManifest.xml
    public static final String AUTHORITY = "com.gradysimon.peppermint.upstream";

    private static final String DBNAME = "peppermintdb";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int USERPROFILE_UUID = 0;
    private static final int USERPROFILE_LOCAL_ID = 1;
    private static final int USERPROFILES = 2;
    private static final int TOPICS = 3;
    private static final int UNSEEN_TOPICS = 4;
    private static final int TOPIC_LOCAL_ID = 5;
    private static final int TOPIC_UUID = 6;
    private static final int CONVERSATIONS = 7;
    private static final int CONVERSATION_ID = 8;

    static {
        uriMatcher.addURI(AUTHORITY, "userprofile/uuid/#", USERPROFILE_UUID);
        uriMatcher.addURI(AUTHORITY, "userprofile/localid/#", USERPROFILE_LOCAL_ID);
        uriMatcher.addURI(AUTHORITY, "userprofile", USERPROFILES);
        uriMatcher.addURI(AUTHORITY, "topic", TOPICS);
        uriMatcher.addURI(AUTHORITY, "topic/unseen", UNSEEN_TOPICS);
        uriMatcher.addURI(AUTHORITY, "topic/localid/#", TOPIC_LOCAL_ID);
        uriMatcher.addURI(AUTHORITY, "topic/uuid/#", TOPIC_UUID);
        uriMatcher.addURI(AUTHORITY, "conversation", CONVERSATIONS);
        uriMatcher.addURI(AUTHORITY, "conversation/#", CONVERSATION_ID);
    }

    private UpstreamDatabaseHelper databaseHelper;

    private SQLiteDatabase readableDb() {
        return databaseHelper.getReadableDatabase();
    }

    private SQLiteDatabase writableDb() {
        return databaseHelper.getWritableDatabase();
    }

    public UpstreamContentProvider() {
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            // A single userprofile
            case USERPROFILES:
                selection = null;
                projection = new String[]{
                        UpstreamContract.UserProfile.ID,
                        UpstreamContract.UserProfile.UUID,
                        UpstreamContract.UserProfile.FIRST_NAME,
                        UpstreamContract.UserProfile.LAST_NAME,
                };
                return readableDb().query(UpstreamContract.UserProfile.TABLE, projection, selection, null, null, null, null);
            case USERPROFILE_LOCAL_ID:
                String requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.UserProfile.ID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = new String[]{
                        UpstreamContract.UserProfile.ID,
                        UpstreamContract.UserProfile.UUID,
                        UpstreamContract.UserProfile.FIRST_NAME,
                        UpstreamContract.UserProfile.LAST_NAME
                };
                return readableDb().query(UpstreamContract.UserProfile.TABLE, projection, selection, selectionArgs, null, null, null);
            case USERPROFILE_UUID:
                String requestedUpstreamId = uri.getLastPathSegment();
                selection = UpstreamContract.UserProfile.UUID + " = ?";
                selectionArgs = new String[]{requestedUpstreamId};
                projection = new String[]{
                        UpstreamContract.UserProfile.ID,
                        UpstreamContract.UserProfile.UUID,
                        UpstreamContract.UserProfile.FIRST_NAME,
                        UpstreamContract.UserProfile.LAST_NAME
                };
                return readableDb().query(UpstreamContract.UserProfile.TABLE, projection, selection, selectionArgs, null, null, null);
            // All topics
            case TOPICS:
                selection = null;
                projection = new String[]{
                        UpstreamContract.Topic.ID,
                        UpstreamContract.Topic.UUID,
                        UpstreamContract.Topic.AUTHOR_UUID,
                        UpstreamContract.Topic.TEXT,
                        UpstreamContract.Topic.SEEN,
                };
                return readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, null, null, null, null);
            case UNSEEN_TOPICS:
                selection = UpstreamContract.Topic.SEEN + " = ?";
                selectionArgs = new String[]{"0"};
                projection = new String[]{
                        UpstreamContract.Topic.ID,
                        UpstreamContract.Topic.UUID,
                        UpstreamContract.Topic.AUTHOR_UUID,
                        UpstreamContract.Topic.TEXT,
                        UpstreamContract.Topic.SEEN,
                };
                return  readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, selectionArgs, null, null, null);
            case TOPIC_LOCAL_ID:
                requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.UserProfile.ID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = new String[]{
                        UpstreamContract.Topic.ID,
                        UpstreamContract.Topic.UUID,
                        UpstreamContract.Topic.AUTHOR_UUID,
                        UpstreamContract.Topic.TEXT,
                        UpstreamContract.Topic.SEEN,
                };
                return  readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, selectionArgs, null, null, null);
            case TOPIC_UUID:
                requestedUpstreamId = uri.getLastPathSegment();
                selection = UpstreamContract.UserProfile.UUID + " = ?";
                selectionArgs = new String[]{requestedUpstreamId};
                projection = new String[]{
                        UpstreamContract.Topic.ID,
                        UpstreamContract.Topic.UUID,
                        UpstreamContract.Topic.AUTHOR_UUID,
                        UpstreamContract.Topic.TEXT,
                        UpstreamContract.Topic.SEEN,
                };
                return  readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, selectionArgs, null, null, null);
        }
        throw new UnsupportedOperationException("That type of query is not implemented yet.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case USERPROFILES:
                long id = writableDb().insert(UpstreamContract.UserProfile.TABLE, null, values);
                return ContentUris.appendId(uri.buildUpon(), id).build();
            case TOPICS:
                id = writableDb().insert(UpstreamContract.Topic.TABLE, null, values);
                return ContentUris.appendId(uri.buildUpon(), id).build();
        }
        throw new UnsupportedOperationException("That type of insert is not implemented yet.");
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new UpstreamDatabaseHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case TOPIC_LOCAL_ID:
                selection = UpstreamContract.Topic.ID + " =?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return writableDb().update(UpstreamContract.Topic.TABLE, values, selection, selectionArgs);
            case TOPIC_UUID:
                selection = UpstreamContract.Topic.UUID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return writableDb().update(UpstreamContract.Topic.TABLE, values, selection, selectionArgs);
            case USERPROFILE_LOCAL_ID:
                selection = UpstreamContract.UserProfile.ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return writableDb().update(UpstreamContract.UserProfile.TABLE, values, selection, selectionArgs);
            case USERPROFILE_UUID:
                selection = UpstreamContract.UserProfile.UUID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return writableDb().update(UpstreamContract.UserProfile.TABLE, values, selection, selectionArgs);
        }
        throw new UnsupportedOperationException("That type of update is not implemented yet.");
    }

    /**
     * The SQLiteOpenHelper that manages getting a valid SQLiteDatabase instance
     */
    public static class UpstreamDatabaseHelper extends SQLiteOpenHelper {
        private static final String SQL_CREATE_TABLE_USERPROFILE =
                "CREATE TABLE " + UpstreamContract.UserProfile.TABLE + " (" +
                UpstreamContract.UserProfile.ID + " integer PRIMARY KEY, " +
                UpstreamContract.UserProfile.UUID + " integer, " +
                UpstreamContract.UserProfile.FIRST_NAME + " varchar(50) NOT NULL, " +
                UpstreamContract.UserProfile.LAST_NAME + " last_name varchar(50) NOT NULL" +
                ")" +
                ";";
        // TODO: Add back NOT NULLs
        public static final String SQL_CREATE_TABLE_TOPIC =
                "CREATE TABLE " + UpstreamContract.Topic.TABLE + " (" +
                UpstreamContract.Topic.ID + " integer PRIMARY KEY, " +
                UpstreamContract.Topic.UUID + " integer, " +
                UpstreamContract.Topic.AUTHOR_UUID + " integer NOT NULL REFERENCES userprofile (" + UpstreamContract.UserProfile.UUID + "), " +
                UpstreamContract.Topic.TEXT + " text NOT NULL," +
                UpstreamContract.Topic.LAST_MODIFIED + " timestamp with time zone, " +
                UpstreamContract.Topic.INTERESTED + " integer, " +
                UpstreamContract.Topic.SEEN + " integer NOT NULL" +
                ")" +
                ";";
        public static final String SQL_CREATE_TABLE_CONVERSATION =
                "CREATE TABLE conversation (" +
                "    _ID integer NOT NULL PRIMARY KEY," +
                "    counterparty_id integer NOT NULL REFERENCES userprofile (_ID)," +
                "    topic_id integer NOT NULL REFERENCES topic (_ID)," +
                ")" +
                ";";
        public static final String SQL_CREATE_TABLE_MESSAGE =
                "CREATE TABLE message (" +
                "    _ID integer NOT NULL PRIMARY KEY," +
                "    conversation_id integer NOT NULL REFERENCES conversation (_ID)," +
                "    text text NOT NULL," +
                "    from_counterparty integer NOT NULL," +
                "    timestamp timestamp with time zone NOT NULL" +
                ")" +
                ";";

        UpstreamDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.i("Database", "Attempting to create tables.");
            try {
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USERPROFILE);
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TOPIC);
                // sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CONVERSATION);
                // sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("Database", "Tables created successfully.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            // TODO: Handle upgrades to DB
        }
    }
}
