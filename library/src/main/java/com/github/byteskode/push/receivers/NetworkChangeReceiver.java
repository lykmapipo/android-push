package com.github.byteskode.push.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.services.DeviceSyncService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;

/**
 * listen for device network change and sync pending device details
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/18/16
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = Push.getInstance().isConnected();

        if (isConnected) {

            //prepare device details sync task
            OneoffTask deviceSyncTask = new OneoffTask.Builder()
                    .setService(DeviceSyncService.class)
                    .setExecutionWindow(0L, 30L)
                    .setTag(DeviceSyncService.TASK_TAG)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .build();

            //schedule push device details sync task
            GcmNetworkManager.getInstance(context.getApplicationContext()).schedule(deviceSyncTask);
        }

    }
}
