package com.gradysimon.peppermint.datatype;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gradysimon.peppermint.sync.Synchronizable;
import com.gradysimon.peppermint.sync.UpstreamContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grady on 2/14/14.
 */

//TODO: Write "DataType" abstract class to capture stuff like "getDetailUri", "getXListFromCursor", etc.
public class Topic implements Synchronizable {

    private int localId = Synchronizable.NOT_IN_DB;
    private int uuid = Synchronizable.NEEDS_UPLOAD;
    private int authorUuid;
    private String text;

    public static Topic getByUuid(int uuid, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = UpstreamContract.Topic.uuidUri(uuid);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Topic result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = new Topic(cursor);
        }
        cursor.close();
        return result;
    }

    public static List<Topic> listFromCursor(Cursor cursor) {
        List<Topic> topicList = new ArrayList<>();
        while (cursor.moveToNext()) {
            topicList.add(new Topic(cursor));
        }
        return topicList;
    }

    // ------------------------

    public Topic(int uuid, int authorUuid, String text) {
        this.uuid = uuid;
        this.authorUuid = authorUuid;
        this.text = text;
    }

    public Topic(int authorUuid, String text) {
        this.authorUuid = authorUuid;
        this.text = text;
    }

    public Topic(Cursor cursor) {
        this.localId = cursor.getInt(UpstreamContract.Topic.ID_COL);
        int upstreamIdFromDb = cursor.getInt(UpstreamContract.Topic.UUID_COL);
        if (upstreamIdFromDb == cursor.FIELD_TYPE_NULL) {
            this.uuid = Synchronizable.NEEDS_UPLOAD;
        } else {
            this.uuid = upstreamIdFromDb;
        }
        this.authorUuid = cursor.getInt(UpstreamContract.Topic.AUTHOR_UUID_COL);
        this.text = cursor.getString(UpstreamContract.Topic.TEXT_COL);
    }

    public String getText() {
        return this.text;
    }

    public String toString() {
        return "Topic ID: " + this.localId + " | Author ID: " + this.authorUuid + " | Text: " + this.text;
    }

    public int getLocalId() {
        return localId;
    }

    @Override
    public int getUuid() {
        return uuid;
    }

    public UserProfile getAuthor(Context context) {
        return UserProfile.getByUuid(this.authorUuid, context);
    }

    @Override
    public Uri contentUri() {
        return UpstreamContract.Topic.CONTENT_URI;
    }

    public Uri uuidUri() {
        return UpstreamContract.Topic.uuidUri(this.uuid);
    }

    public Uri localIdUri() {
        return UpstreamContract.Topic.localIdUri(this.localId);
    }

    @Override
    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    @Override
    public void save(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = this.toContentValues();
        if (this.localId == Synchronizable.NOT_IN_DB) {
            Uri newUri = contentResolver.insert(contentUri(), contentValues);
            this.localId = Integer.parseInt(newUri.getLastPathSegment());
        } else {
            contentResolver.update(localIdUri(), contentValues, null, null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        if (authorUuid != topic.authorUuid) return false;
        if (localId != topic.localId) return false;
        if (text != null ? !text.equals(topic.text) : topic.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = localId;
        result = 31 * result + authorUuid;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public boolean requiresUpdate(Synchronizable remoteObject) {
        if (!(remoteObject instanceof Topic)) {
            return false;
        }
        return this.equals(remoteObject);
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (this.localId != Synchronizable.NOT_IN_DB) {
            contentValues.put(UpstreamContract.Topic.ID, this.localId);
        }
        if (this.uuid == Synchronizable.NEEDS_UPLOAD) {
            contentValues.putNull(UpstreamContract.Topic.UUID);
        } else {
            contentValues.put(UpstreamContract.Topic.UUID, this.uuid);
        }
        contentValues.put(UpstreamContract.Topic.AUTHOR_UUID, this.authorUuid);
        contentValues.put(UpstreamContract.Topic.TEXT, this.text);
        return contentValues;
    }

    public UpstreamRepresentation toUpstreamRepresentation() {
        return new UpstreamRepresentation(this.uuid, this.authorUuid, this.text);
    }

    public class UpstreamRepresentation {
        private int id = Synchronizable.NEEDS_UPLOAD;
        private int authorId;
        private String text;

        public UpstreamRepresentation() {
            // no-args constructor for gson
        }

        public UpstreamRepresentation(int uuid, int authorUuid, String text) {
            this.id = uuid;
            this.authorId = authorUuid;
            this.text = text;
        }

        public Topic toTopic() {
            return new Topic(this.id, this.authorId, this.text);
        }

        /**
         * @return true if this UpstreamRepresentation came from upstream.
         */
        public boolean needsUpload() {
            return id != Synchronizable.NEEDS_UPLOAD;
        }
    }
}