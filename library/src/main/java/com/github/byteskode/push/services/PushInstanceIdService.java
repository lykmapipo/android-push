package com.github.byteskode.push.services;

import android.content.Intent;
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
     * @see {@link https://firebase.google.com/docs/cloud-messaging/android/client}
     */
    @Override
    public void onTokenRefresh() {
        startService(new Intent(this, RegistrationTokenService.class));
    }
}
