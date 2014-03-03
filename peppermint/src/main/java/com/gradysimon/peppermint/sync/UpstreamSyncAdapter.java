package com.gradysimon.peppermint.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.datatype.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grady on 2/27/14.
 */
public class UpstreamSyncAdapter extends AbstractThreadedSyncAdapter {

    public UpstreamSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        List<Topic> remoteTopicList = JsonApiManager.getTopicList();
        List<Topic> localTopicList = getLocalTopics();
        List<UserProfile> remoteUserProfileList = JsonApiManager.getUserProfileList();
        List<UserProfile> localUserProfileList = getLocalUserProfiles();
        // TODO: delete old or seen Topics?
        synchronizeLocalObjects(remoteTopicList, localTopicList, provider);
        synchronizeLocalObjects(remoteUserProfileList, localUserProfileList, provider);
    }

    private void synchronizeLocalObjects(List<? extends Synchronizable> remoteObjects, List<? extends Synchronizable> localObjects, ContentProviderClient provider) {
        List<Synchronizable> uploadList = new ArrayList<>();
        for (Synchronizable localObject : localObjects) {
            if (localObject.getUuid() == Synchronizable.NEEDS_UPLOAD) {
                uploadList.add(localObject);
            }
        }
        executeSyncUpload(uploadList);
        Map<Integer, Synchronizable> localTopicMap = new HashMap<Integer, Synchronizable>();
        for (Synchronizable localObject : localObjects) {
            localTopicMap.put(localObject.getUuid(), localObject);
        }
        List<Synchronizable> updateList = new ArrayList<>();
        List<Synchronizable> insertList = new ArrayList<>();
        for (Synchronizable remoteObject : remoteObjects) {
            Synchronizable correspondingLocalObject = localTopicMap.get(remoteObject.getUuid());
            if (correspondingLocalObject == null) {
                // If there is no local topic with the same ID
                insertList.add(remoteObject);
            } else if (!correspondingLocalObject.requiresUpdate(remoteObject)){
                // If there is a local topic with the same ID, but it differs from the remote
                updateList.add(remoteObject);
                // TODO: What if it's changed locally and we want to upload those changes?
            }
        }
        executeSyncUpdate(updateList, provider);
        executeSyncInsert(insertList, provider);
    }

    private void executeSyncUpload(List<Synchronizable> uploadList) {
        for (Synchronizable uploadObject : uploadList) {
            if (uploadObject instanceof Topic) {
                Topic response = JsonApiManager.postTopic((Topic) uploadObject);
                uploadObject.setUuid(response.getUuid());
                uploadObject.save(this.getContext());
            }
            if (uploadObject instanceof UserProfile) {
                UserProfile response = JsonApiManager.postUserProfile((UserProfile) uploadObject);
                uploadObject.setUuid(response.getUuid());
                uploadObject.save(this.getContext());
            }
        }
    }

    private void executeSyncInsert(List<Synchronizable> insertList, ContentProviderClient provider) {
        if (!insertList.isEmpty()) {
            Uri uri = insertList.get(0).contentUri();
            List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
            for (Synchronizable object : insertList) {
                contentValuesList.add(object.toContentValues());
            }
            try {
                provider.bulkInsert(uri, contentValuesList.toArray(new ContentValues[0]));
            } catch (RemoteException e) {
                Log.e("Sync", "Failed to perform bulk insert of Topic objects.", e);
            }
        }
    }

    private void executeSyncUpdate(List<Synchronizable> updateList, ContentProviderClient provider) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for (Synchronizable object : updateList) {
            ContentValues contentValues = object.toContentValues();
            Uri uri = object.uuidUri();
            ContentProviderOperation operation = ContentProviderOperation.newUpdate(uri).withValues(contentValues).build();
            operations.add(operation);
        }
        try {
            provider.applyBatch(operations);
        } catch (RemoteException e) {
            Log.e("Sync", "Failed to perform bulk update of Topic objects.", e);
        } catch (OperationApplicationException e) {
            Log.e("Sync", "Failed to perform bulk update of Topic objects.", e);
        }
    }

    private List<Topic> getLocalTopics() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(UpstreamContract.Topic.CONTENT_URI, null, null, null, null);
        List<Topic> topicList = Topic.listFromCursor(cursor);
        cursor.close();
        return topicList;
    }

    private List<UserProfile> getLocalUserProfiles() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(UpstreamContract.UserProfile.CONTENT_URI, null, null, null, null);
        List<UserProfile> userProfileList = UserProfile.listFromCursor(cursor);
        cursor.close();
        return userProfileList;
    }
}
