package com.gradysimon.peppermint.datatype;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.gradysimon.peppermint.sync.Synchronizable;
import com.gradysimon.peppermint.sync.UpstreamContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grady on 2/22/14.
 */
public class Message implements Synchronizable {
    private int localId = Synchronizable.NOT_IN_DB;
    private int uuid = Synchronizable.NEEDS_UPLOAD;
    private int conversationUuid;
    private boolean fromCounterparty;
    private String content;

    public static Message getByUuid(int uuid, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = UpstreamContract.Message.uuidUri(uuid);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Message result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = new Message(cursor);
        }
        cursor.close();
        return result;
    }

    public static List<Message> listFromCursor(Cursor cursor) {
        List<Message> messageList = new ArrayList<>();
        while (cursor.moveToNext()) {
            messageList.add(new Message(cursor));
        }
        return messageList;
    }

    public static Message newOutboundMessage(String text, Conversation conversation) {
        if (conversation.getUuid() == Synchronizable.NEEDS_UPLOAD) {
            Log.e("Message", "Returning a Message with an unuploaded Conversation uuid. Things are going to break.");
        }
        return new Message(conversation.getUuid(), false, text);
    }

    // --------------------------------------------------

    public Message(Cursor cursor) {
        this.localId = cursor.getInt(UpstreamContract.Message.ID_COL);
        int upstreamIdFromDb = cursor.getInt(UpstreamContract.Message.UUID_COL);
        if (upstreamIdFromDb == cursor.FIELD_TYPE_NULL) {
            this.uuid = Synchronizable.NEEDS_UPLOAD;
        } else {
            this.uuid = upstreamIdFromDb;
        }
        this.conversationUuid = cursor.getInt(UpstreamContract.Message.CONVERSATION_UUID_COL);
        this.fromCounterparty = cursor.getInt(UpstreamContract.Message.FROM_COUNTERPARTY_COL) == 1;
        this.content = cursor.getString(UpstreamContract.Message.CONTENT_COL);
    }

    public Message(int uuid, int conversationUuid, boolean fromCounterparty, String content) {
        this.uuid = uuid;
        this.conversationUuid = conversationUuid;
        this.fromCounterparty = fromCounterparty;
        this.content = content;
    }

    public Message(int conversationUuid, boolean fromCounterparty, String content) {
        this.conversationUuid = conversationUuid;
        this.fromCounterparty = fromCounterparty;
        this.content = content;
    }

    @Override
    public boolean requiresUpdate(Synchronizable remoteObject) {
        return !this.equals(remoteObject);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (this.localId != Synchronizable.NOT_IN_DB) {
            contentValues.put(UpstreamContract.Message.ID, this.localId);
        }
        if (this.uuid == Synchronizable.NEEDS_UPLOAD) {
            contentValues.putNull(UpstreamContract.Message.UUID);
        } else {
            contentValues.put(UpstreamContract.Message.UUID, this.uuid);
        }
        contentValues.put(UpstreamContract.Message.CONVERSATION_UUID, this.conversationUuid);
        contentValues.put(UpstreamContract.Message.FROM_COUNTERPARTY, this.fromCounterparty);
        contentValues.put(UpstreamContract.Message.CONTENT, this.content);
        return contentValues;
    }

    @Override
    public int getUuid() {
        return uuid;
    }

    @Override
    public Uri contentUri() {
        return UpstreamContract.Message.CONTENT_URI;
    }

    @Override
    public Uri uuidUri() {
        return UpstreamContract.Message.uuidUri(this.uuid);
    }

    @Override
    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public boolean isFromCounterparty() {
        return fromCounterparty;
    }

    public boolean isToCounterparty() {
        return !fromCounterparty;
    }

    public String getContent() {
        return content;
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

    public Uri localIdUri() {
        return UpstreamContract.Message.localIdUri(this.localId);
    }

    // equals() and hashCode() don't use localId or uuid because they might not be set when the methods are used.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (conversationUuid != message.conversationUuid) return false;
        if (fromCounterparty != message.fromCounterparty) return false;
        if (!content.equals(message.content)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = conversationUuid;
        result = 31 * result + (fromCounterparty ? 1 : 0);
        result = 31 * result + content.hashCode();
        return result;
    }

    public UpstreamRepresentation toUpstreamRepresentation() {
        return new UpstreamRepresentation(this.uuid, this.conversationUuid, this.fromCounterparty, this.content);
    }

    public class UpstreamRepresentation {
        private int messageId = Synchronizable.NEEDS_UPLOAD;
        private int conversationId;
        private boolean fromCounterparty;
        private String text;

        public UpstreamRepresentation() {
            // no args constructor for gson
        }

        public UpstreamRepresentation(int uuid, int conversationUuid, boolean fromCounterparty, String content) {
            this.messageId = uuid;
            this.conversationId = conversationUuid;
            this.fromCounterparty = fromCounterparty;
            this.text = content;
        }

        public Message toMessage() {
            return new Message(messageId, conversationId, fromCounterparty, text);
        }
    }
}
