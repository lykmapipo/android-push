package com.github.lykmapipo.push;

import com.google.firebase.messaging.RemoteMessage;

/**
 * push notification message listener
 *
 * @author lally elias <lallyelias87@gmail.com>
 * @date 10/18/16
 */
public interface PushMessageListener {
    /**
     * Called when downstream message receive by device.
     *
     * @param remoteMessage
     */
    void onMessage(RemoteMessage remoteMessage);
}
