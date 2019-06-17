package com.github.lykmapipo.push.receivers;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.push.services.DeviceSyncService;

/**
 * listen for device network change and sync pending device details
 *
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/18/16
 */
public class NetworkChangeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Push push = Push.getInstance();
        boolean isConnected = push.isConnected();

        if (isConnected) {
            Intent service = new Intent(context, DeviceSyncService.class);
            startWakefulService(context, service);
        } else {
            completeWakefulIntent(intent);
        }

    }
}
