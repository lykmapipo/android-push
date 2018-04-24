package com.github.lykmapipo.push.services;

import android.content.Intent;

import com.github.lykmapipo.push.Push;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * handle registration token updates
 *
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/17/16
 */
public class PushInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated or generated for the first time.
     */
    @Override
    public void onTokenRefresh() {

        Push push = Push.getInstance();
        boolean isConnected = push.isConnected();

        if (isConnected) {
            Intent service = new Intent(getApplicationContext(), DeviceSyncService.class);
            startService(service);
        }

    }
}
