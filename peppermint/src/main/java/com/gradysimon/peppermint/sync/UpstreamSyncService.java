package com.gradysimon.peppermint.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpstreamSyncService extends Service {
    private static UpstreamSyncAdapter syncAdapter = null;

    private static final Object syncAdapterLock = new Object();

    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new UpstreamSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
