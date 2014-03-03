package com.gradysimon.peppermint.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by grady on 2/28/14.
 */
public class SyncUtils {
    public static void createSyncAccount(Context context) {
        Account account = UpstreamAuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            Log.i("Accounts", "Successfully made account.");
        }
    }
}
