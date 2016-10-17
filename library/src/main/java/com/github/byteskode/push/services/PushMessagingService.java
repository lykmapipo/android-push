package com.github.byteskode.push.services;

import com.github.byteskode.push.Push;
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
     * @see {@link https://firebase.google.com/docs/cloud-messaging/android/receive}
     * @param message
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Push push = Push.getInstance(getApplicationContext());
        push.onPushNotification(message);
    }
}
