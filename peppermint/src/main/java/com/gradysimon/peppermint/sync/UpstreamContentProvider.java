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
    private static final int USERPROFILES = 1;
    private static final int USERPROFILE_LOCAL_ID = 2;
    private static final int USERPROFILE_UUID = 3;
    private static final int TOPICS = 4;
    private static final int UNSEEN_TOPICS = 5;
    private static final int TOPIC_LOCAL_ID = 6;
    private static final int TOPIC_UUID = 7;
    private static final int CONVERSATIONS = 8;
    private static final int CONVERSATION_LOCAL_ID = 9;
    private static final int CONVERSATION_UUID = 10;
    private static final int MESSAGES = 11;
    private static final int MESSAGES_FROM_CONVERSATION = 12;
    private static final int MESSAGES_LOCAL_ID = 13;
    private static final int MESSAGES_UUID = 14;

    static {
        uriMatcher.addURI(AUTHORITY, "userprofile", USERPROFILES);
        uriMatcher.addURI(AUTHORITY, "userprofile/localid/#", USERPROFILE_LOCAL_ID);
        uriMatcher.addURI(AUTHORITY, "userprofile/uuid/#", USERPROFILE_UUID);
        uriMatcher.addURI(AUTHORITY, "topic", TOPICS);
        uriMatcher.addURI(AUTHORITY, "topic/unseen", UNSEEN_TOPICS);
        uriMatcher.addURI(AUTHORITY, "topic/localid/#", TOPIC_LOCAL_ID);
        uriMatcher.addURI(AUTHORITY, "topic/uuid/#", TOPIC_UUID);
        uriMatcher.addURI(AUTHORITY, "conversation", CONVERSATIONS);
        uriMatcher.addURI(AUTHORITY, "conversation/localid/#", CONVERSATION_LOCAL_ID);
        uriMatcher.addURI(AUTHORITY, "conversation/uuid/#", CONVERSATION_UUID);
        uriMatcher.addURI(AUTHORITY, "message", MESSAGES);
        uriMatcher.addURI(AUTHORITY, "message/conversation/#", MESSAGES_FROM_CONVERSATION);
        uriMatcher.addURI(AUTHORITY, "message/localid/#", MESSAGES_LOCAL_ID);
        uriMatcher.addURI(AUTHORITY, "message/uuid/#", MESSAGES_UUID);
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
            case USERPROFILES:
                selection = null;
                projection = UpstreamContract.UserProfile.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.UserProfile.TABLE, projection, selection, null, null, null, null);
            case USERPROFILE_LOCAL_ID:
                String requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.UserProfile.ID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = UpstreamContract.UserProfile.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.UserProfile.TABLE, projection, selection, selectionArgs, null, null, null);
            case USERPROFILE_UUID:
                String requestedUpstreamId = uri.getLastPathSegment();
                selection = UpstreamContract.UserProfile.UUID + " = ?";
                selectionArgs = new String[]{requestedUpstreamId};
                projection = UpstreamContract.UserProfile.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.UserProfile.TABLE, projection, selection, selectionArgs, null, null, null);
            case TOPICS:
                selection = null;
                projection = UpstreamContract.Topic.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, null, null, null, null);
            case UNSEEN_TOPICS:
                selection = UpstreamContract.Topic.SEEN + " = ?";
                selectionArgs = new String[]{"0"};
                projection = UpstreamContract.Topic.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, selectionArgs, null, null, null);
            case TOPIC_LOCAL_ID:
                requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.Topic.ID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = UpstreamContract.Topic.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, selectionArgs, null, null, null);
            case TOPIC_UUID:
                requestedUpstreamId = uri.getLastPathSegment();
                selection = UpstreamContract.Topic.UUID + " = ?";
                selectionArgs = new String[]{requestedUpstreamId};
                projection = UpstreamContract.Topic.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Topic.TABLE, projection, selection, selectionArgs, null, null, null);
            case CONVERSATIONS:
                selection = null;
                projection = UpstreamContract.Conversation.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Conversation.TABLE, projection, selection, null, null, null, null);
            case CONVERSATION_LOCAL_ID:
                requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.Conversation.ID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = UpstreamContract.Conversation.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Conversation.TABLE, projection, selection, selectionArgs, null, null, null);
            case CONVERSATION_UUID:
                requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.Conversation.UUID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = UpstreamContract.Conversation.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Conversation.TABLE, projection, selection, selectionArgs, null, null, null);
            case MESSAGES:
                selection = null;
                projection = UpstreamContract.Message.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Message.TABLE, projection, selection, null, null, null, null);
            case MESSAGES_FROM_CONVERSATION:
                String conversationUuid = uri.getLastPathSegment();
                selection = UpstreamContract.Message.CONVERSATION_UUID + " = ?";
                selectionArgs = new String[]{conversationUuid};
                projection = UpstreamContract.Message.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Message.TABLE, projection, selection, selectionArgs, null, null, null);
            case MESSAGES_LOCAL_ID:
                requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.Message.ID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = UpstreamContract.Message.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Message.TABLE, projection, selection, selectionArgs, null, null, null);
            case MESSAGES_UUID:
                requestedId = uri.getLastPathSegment();
                selection = UpstreamContract.Message.UUID + " = ?";
                selectionArgs = new String[]{requestedId};
                projection = UpstreamContract.Message.PROJECTION_ALL;
                return readableDb().query(UpstreamContract.Message.TABLE, projection, selection, selectionArgs, null, null, null);
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
                UpstreamContract.Topic.AUTHOR_UUID + " integer NOT NULL REFERENCES " + UpstreamContract.UserProfile.TABLE + " (" + UpstreamContract.UserProfile.UUID + "), " +
                UpstreamContract.Topic.TEXT + " text NOT NULL," +
                UpstreamContract.Topic.LAST_MODIFIED + " timestamp with time zone, " +
                UpstreamContract.Topic.INTERESTED + " integer, " +
                UpstreamContract.Topic.SEEN + " integer NOT NULL" +
                ");";

        public static final String SQL_CREATE_TABLE_CONVERSATION =
                "CREATE TABLE " + UpstreamContract.Conversation.TABLE + " (" +
                UpstreamContract.Conversation.ID + " integer PRIMARY KEY, " +
                UpstreamContract.Conversation.UUID + " integer, " +
                UpstreamContract.Conversation.COUNTERPARTY_UUID + " integer NOT NULL REFERENCES " + UpstreamContract.UserProfile.TABLE + " (" + UpstreamContract.UserProfile.UUID + "), " +
                UpstreamContract.Conversation.TOPIC_UUID + " integer NOT NULL REFERENCES " + UpstreamContract.Topic.TABLE + " (" + UpstreamContract.Topic.UUID + "), " +
                ");";

        public static final String SQL_CREATE_TABLE_MESSAGE =
                "CREATE TABLE " + UpstreamContract.Message.TABLE +" (" +
                UpstreamContract.Message.ID + " integer PRIMARY KEY, " +
                UpstreamContract.Message.UUID + " integer, " +
                UpstreamContract.Message.CONVERSATION_UUID + " integer NOT NULL REFERENCES " + UpstreamContract.Conversation.TABLE + " (" + UpstreamContract.Conversation.UUID + "), " +
                UpstreamContract.Message.SENDER_UUID + " integer NOT NULL REFERENCES " + UpstreamContract.UserProfile.TABLE + " (" + UpstreamContract.UserProfile.UUID + "), " +
                UpstreamContract.Message.CONTENT + "text NOT NULL" +
                ");";

        UpstreamDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.i("Database", "Attempting to create tables.");
            try {
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USERPROFILE);
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TOPIC);
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_CONVERSATION);
                sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MESSAGE);
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
