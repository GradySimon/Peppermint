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
 * Created by grady on 2/22/14.
 */
public class Conversation implements Synchronizable{
    private int localId = Synchronizable.NOT_IN_DB;
    private int uuid = Synchronizable.NEEDS_UPLOAD;
    private int topicUuid;
    private int counterpartyUuid;

    public static Conversation getByUuid(int uuid, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = UpstreamContract.Conversation.uuidUri(uuid);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Conversation result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = new Conversation(cursor);
        }
        cursor.close();
        return result;
    }

    public static List<Conversation> listFromCursor(Cursor cursor) {
        List<Conversation> conversationList = new ArrayList<>();
        while (cursor.moveToNext()) {
            conversationList.add(new Conversation(cursor));
        }
        return conversationList;
    }

    // ------------------------------------------------

    public Conversation(Cursor cursor) {
        this.localId = cursor.getInt(UpstreamContract.Message.ID_COL);
        int upstreamIdFromDb = cursor.getInt(UpstreamContract.Message.UUID_COL);
        if (upstreamIdFromDb == cursor.FIELD_TYPE_NULL) {
            this.uuid = Synchronizable.NEEDS_UPLOAD;
        } else {
            this.uuid = upstreamIdFromDb;
        }
        this.topicUuid = cursor.getInt(UpstreamContract.Conversation.TOPIC_UUID_COL);
        this.counterpartyUuid = cursor.getInt(UpstreamContract.Conversation.TOPIC_UUID_COL);
    }

    public Conversation() {
        // no arg constructor for gson
    }

    // For launched conversations
    public Conversation(Topic topic) {
        this.topicUuid = topic.getUuid();
        this.counterpartyUuid = topic.getAuthorUuid();
    }

    public Conversation(int uuid, int topicUuid, int counterpartyUuid) {
        this.uuid = uuid;
        this.topicUuid = topicUuid;
        this.counterpartyUuid = counterpartyUuid;
    }

    public int getTopicUuid() {
        return this.topicUuid;
    }

    public List<Message> getMessages() {
        throw new UnsupportedOperationException();
    }


    public int getCounterpartyUuid() {
        return this.counterpartyUuid;
    }

    public void addMessage(Message message) {

    }

    public int getLocalId() {
        return localId;
    }

    @Override
    public boolean requiresUpdate(Synchronizable remoteObject) {
        if (!(remoteObject instanceof Conversation)) {
            return false;
        }
        return this.equals(remoteObject);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (this.localId != Synchronizable.NOT_IN_DB) {
            contentValues.put(UpstreamContract.Conversation.ID, this.localId);
        }
        if (this.uuid == Synchronizable.NEEDS_UPLOAD) {
            contentValues.putNull(UpstreamContract.Conversation.UUID);
        } else {
            contentValues.put(UpstreamContract.Conversation.UUID, this.uuid);
        }
        contentValues.put(UpstreamContract.Conversation.TOPIC_UUID, this.topicUuid);
        contentValues.put(UpstreamContract.Conversation.COUNTERPARTY_UUID, this.counterpartyUuid);
        return contentValues;
    }

    @Override
    public int getUuid() {
        return this.uuid;
    }

    @Override
    public Uri contentUri() {
        return UpstreamContract.Conversation.CONTENT_URI;
    }

    @Override
    public Uri uuidUri() {
        return UpstreamContract.Conversation.uuidUri(this.uuid);
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

    public Topic getTopic(Context context) {
        return Topic.getByUuid(this.topicUuid, context);
    }

    public Uri localIdUri() {
        return UpstreamContract.Conversation.localIdUri(this.localId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conversation that = (Conversation) o;

        if (counterpartyUuid != that.counterpartyUuid) return false;
        if (topicUuid != that.topicUuid) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = topicUuid;
        result = 31 * result + counterpartyUuid;
        return result;
    }

    public UpstreamRepresentation toUpstreamRepresentation() {
        return new UpstreamRepresentation(this.uuid, this.topicUuid, this.counterpartyUuid);
    }

    public class UpstreamRepresentation {
        private int id = Synchronizable.NEEDS_UPLOAD;
        private int topicId;
        private int counterpartyId;

        public UpstreamRepresentation() {
            // no args constructor for gson
        }

        public UpstreamRepresentation(int uuid, int topicUuid, int counterpartyUuid) {
            this.id = uuid;
            this.topicId = topicUuid;
            this.counterpartyId = counterpartyUuid;
        }

        public Conversation toConversation() {
            return new Conversation(id, topicId, counterpartyId);
        }
    }
}
