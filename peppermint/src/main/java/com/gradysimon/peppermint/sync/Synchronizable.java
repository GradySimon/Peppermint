package com.gradysimon.peppermint.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 * Created by grady on 3/1/14.
 */
public interface Synchronizable {
    public final int NEEDS_UPLOAD = -2;

    public final int NOT_IN_DB = -1;

    public boolean requiresUpdate(Synchronizable remoteObject);

    public ContentValues toContentValues();

    public int getUuid();

    public Uri contentUri();

    public Uri uuidUri();

    public void setUuid(int uuid);

    public void save(Context context);
}
