package com.github.lykmapipo.push.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.github.lykmapipo.push.Push;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * receive and handover push notification message to push
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
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
        Intent intent = new Intent(Push.PUSH_MESSAGE_RECEIVED);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
