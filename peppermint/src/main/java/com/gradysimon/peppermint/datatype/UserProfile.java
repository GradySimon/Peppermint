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
public class UserProfile implements Synchronizable {

    public static UserProfile getByUuid(int uuid, Context context) {
        Uri uri = UpstreamContract.UserProfile.uuidUri(uuid);
        return getByUri(uri, context);
    }

    public static UserProfile getByLocalId(int localId, Context context) {
        Uri uri = UpstreamContract.UserProfile.localIdUri(localId);
        return getByUri(uri, context);
    }

    public static UserProfile getByUri(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        UserProfile result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = new UserProfile(cursor);
        }
        cursor.close();
        return result;
    }

    private int localId = Synchronizable.NOT_IN_DB;
    private int uuid = Synchronizable.NEEDS_UPLOAD;
    private String firstName;

    private String lastName;

    public UserProfile(int uuid, String firstName, String lastName) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Assumes a valid Cursor. Don't pass a cursor to this constructor if it might be null or empty.
     * @param cursor
     */
    public UserProfile(Cursor cursor) {
        this.localId = cursor.getInt(UpstreamContract.UserProfile.ID_COL);
        int upstreamIdFromDb = cursor.getInt(UpstreamContract.UserProfile.UUID_COL);
        if (upstreamIdFromDb == Cursor.FIELD_TYPE_NULL) {
            this.uuid = Synchronizable.NEEDS_UPLOAD;
        } else {
            this.uuid = upstreamIdFromDb;
        }
        this.firstName = cursor.getString(UpstreamContract.UserProfile.FIRST_NAME_COL);
        this.lastName = cursor.getString(UpstreamContract.UserProfile.LAST_NAME_COL);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getWholeName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public boolean requiresUpdate(Synchronizable remoteObject) {
        return !this.equals(remoteObject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        if (!firstName.equals(that.firstName)) return false;
        if (!lastName.equals(that.lastName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (this.localId != Synchronizable.NOT_IN_DB) {
            contentValues.put(UpstreamContract.UserProfile.ID, this.localId);
        }
        if (this.uuid == Synchronizable.NEEDS_UPLOAD) {
            contentValues.putNull(UpstreamContract.UserProfile.UUID);
        } else {
            contentValues.put(UpstreamContract.UserProfile.UUID, this.uuid);
        }
        contentValues.put(UpstreamContract.UserProfile.FIRST_NAME, this.firstName);
        contentValues.put(UpstreamContract.UserProfile.LAST_NAME, this.lastName);
        return contentValues;
    }

    public int getLocalId() {
        return localId;
    }

    @Override
    public int getUuid() {
        return uuid;
    }

    @Override
    public Uri contentUri() {
        return UpstreamContract.UserProfile.CONTENT_URI;
    }

    public Uri uuidUri() {
        return UpstreamContract.UserProfile.uuidUri(this.uuid);
    }

    public Uri localIdUri() {
        return UpstreamContract.UserProfile.localIdUri(this.localId);
    }

    @Override
    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

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

    public String uuidColumnName() {
        return UpstreamContract.UserProfile.UUID;
    }

    public UpstreamRepresentation toUpstreamRepresentation() {
        return new UpstreamRepresentation(this.firstName, this.lastName);
    }

    public static List<UserProfile> listFromCursor(Cursor cursor) {
        List<UserProfile> userProfileList = new ArrayList<>();
        while (cursor.moveToNext()) {
            userProfileList.add(new UserProfile(cursor));
        }
        return userProfileList;
    }

    public class UpstreamRepresentation {
        private int id = Synchronizable.NEEDS_UPLOAD;
        private String firstName;
        private String lastName;

        public UpstreamRepresentation() {
            // no-args constructor for gson
        }

        public UpstreamRepresentation(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public UserProfile toUserProfile() {
            return new UserProfile(this.id, this.firstName, this.lastName);
        }

        /**
         * @return true if this UpstreamRepresentation came from upstream.
         */
        public boolean needsUpload() {
            return id != Synchronizable.NEEDS_UPLOAD;
        }
    }
}
