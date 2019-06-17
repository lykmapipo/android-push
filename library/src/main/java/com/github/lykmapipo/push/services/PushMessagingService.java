package com.github.lykmapipo.push.services;

import android.content.Intent;
import android.os.Bundle;

import com.github.lykmapipo.localburst.LocalBurst;
import com.github.lykmapipo.push.Push;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * receive and handover push notification message to push
 *
 * @author lally elias <lallyelias87@gmail.com>
 * @date 10/17/16
 */
public class PushMessagingService extends FirebaseMessagingService {

    /**
     * handle received push notification message
     *
     * @param message
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        //broadcast received push message
        Bundle bundle = new Bundle();
        bundle.putParcelable(Push.MESSAGE, message);
        LocalBurst.$emit(Push.PUSH_MESSAGE_RECEIVED, bundle);
    }

    /**
     * Called if InstanceID token is updated or generated for the first time.
     */
    @Override
    public void onNewToken(String token) {
        // fire sync registration token
        Intent service = new Intent(getApplicationContext(), DeviceSyncService.class);
        service.putExtra(Push.REGISTRATION_TOKEN_PREF_KEY, token);
        startService(service);
    }
}
