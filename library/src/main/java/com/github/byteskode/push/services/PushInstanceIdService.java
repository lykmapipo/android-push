package com.github.byteskode.push.services;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * handle registration token updates
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/17/16
 */
public class PushInstanceIdService extends FirebaseInstanceIdService {


    /**
     * Called if InstanceID token is updated or generated for the first time.
     */
    @Override
    public void onTokenRefresh() {

        //prepare device details sync task
        OneoffTask deviceSyncTask = new OneoffTask.Builder()
                .setService(DeviceSyncService.class)
                .setExecutionWindow(0L, 30L)
                .setTag(DeviceSyncService.TASK_TAG)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .build();

        //schedule push device details sync task
        GcmNetworkManager.getInstance(getApplicationContext()).schedule(deviceSyncTask);

    }
}
