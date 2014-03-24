package com.gradysimon.peppermint;

import android.accounts.Account;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gradysimon.peppermint.datatype.UserProfile;
import com.gradysimon.peppermint.sync.SyncUtils;

/**
 * Created by grady on 3/21/14.
 */
public class GlobalApplication extends Application {

    public static final String PREFERENCES_REGISTERED_USER_LOCAL_ID = "registered_user_local_id";
    public static final int LOCAL_USER_NOT_REGISTERED = -1;

    public static GlobalApplication instance;

    public static GlobalApplication getInstance() {
        return instance;
    }

    private Account account;

    public void registerUserProfile(String firstName, String lastName) {
        UserProfile user = new UserProfile(firstName, lastName);
        int userLocalId = storeUserProfileLocally(user);
        registerUserProfileLocalId(userLocalId);
    }

    private int storeUserProfileLocally(UserProfile user) {
        user.save(this);
        return user.getLocalId();
    }

    private void registerUserProfileLocalId(int userLocalId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(PREFERENCES_REGISTERED_USER_LOCAL_ID, userLocalId);
        editor.apply();
    }

    public boolean userRegistered() {
        int registeredUserId = PreferenceManager.getDefaultSharedPreferences(this).getInt(PREFERENCES_REGISTERED_USER_LOCAL_ID, LOCAL_USER_NOT_REGISTERED);
        return registeredUserId != LOCAL_USER_NOT_REGISTERED;
    }

    public int getRegisteredUserLocalId() {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(PREFERENCES_REGISTERED_USER_LOCAL_ID, LOCAL_USER_NOT_REGISTERED);
    }

    public UserProfile getRegisteredUser() {
        int localId = getRegisteredUserLocalId();
        return UserProfile.getByLocalId(localId, this);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void requestImmediateSync() {
        SyncUtils.requestImmediateSync(account);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        instance.setAccount(SyncUtils.createSyncAccount(instance));
    }
}
