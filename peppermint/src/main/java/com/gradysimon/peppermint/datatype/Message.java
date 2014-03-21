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
public class Message implements Synchronizable {
    private int localId = Synchronizable.NOT_IN_DB;
    private int uuid = Synchronizable.NEEDS_UPLOAD;
    private int conversationUuid;
    private int senderUuid;
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
        this.senderUuid = cursor.getInt(UpstreamContract.Message.SENDER_UUID_COL);
        this.content = cursor.getString(UpstreamContract.Message.CONTENT_COL);
    }

    @Override
    public boolean requiresUpdate(Synchronizable remoteObject) {
        if (!(remoteObject instanceof Message)) {
            return false;
        }
        return this.equals(remoteObject);
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
        contentValues.put(UpstreamContract.Message.SENDER_UUID, this.senderUuid);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (conversationUuid != message.conversationUuid) return false;
        if (senderUuid != message.senderUuid) return false;
        if (!content.equals(message.content)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = conversationUuid;
        result = 31 * result + senderUuid;
        result = 31 * result + content.hashCode();
        return result;
    }

    public class UpstreamRepresentation {
        private int id = Synchronizable.NEEDS_UPLOAD;
    }
}
