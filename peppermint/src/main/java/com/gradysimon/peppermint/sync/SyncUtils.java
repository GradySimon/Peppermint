package com.gradysimon.peppermint.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

/**
 * Created by grady on 2/28/14.
 */
public class SyncUtils {
    public static Account createSyncAccount(Context context) {
        Account account = UpstreamAuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, UpstreamContentProvider.AUTHORITY, 1);
            Log.i("Accounts", "Account successfully added.");
            return account;
        } else {
            Log.e("Accounts", "Failed to add account.");
            return null;
        }
    }
}
