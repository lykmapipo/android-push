package com.github.byteskode.push.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.api.Device;
import com.github.byteskode.push.receivers.NetworkChangeReceiver;
import com.google.firebase.iid.FirebaseInstanceId;
import retrofit2.Response;

/**
 * sync device push details whenever network is become available
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/18/16
 */
public class DeviceSyncService extends IntentService {

    public static final String TAG = DeviceSyncService.class.getSimpleName();

    public DeviceSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //obtain push instance
            Push push = Push.getInstance();

            //obtain current registration token
            String currentRegistrationToken = push.getRegistrationToken();

            //obtain latest registration token from FirebaseInstanceId service
            String latestRegistrationToken = FirebaseInstanceId.getInstance().getToken();

            //check if token are different for updates
            boolean shouldUpdateServerToken = !currentRegistrationToken.equals(latestRegistrationToken);

            if (shouldUpdateServerToken) {
                //save or update current device registration token
                push.setRegistrationToken(latestRegistrationToken);

                //wait for api end point response
                Response<Device> response;

                //post device details
                if (currentRegistrationToken.equals(null) || currentRegistrationToken.isEmpty()) {
                    response = push.create(latestRegistrationToken);
                }

                //put device details
                else {
                    response = push.update(latestRegistrationToken);
                }

                if ((response != null) && response.isSuccessful()) {

                    //notify registration token updated or created successfully
                    Intent successIntent = new Intent(Push.REGISTRATION_TOKEN_REFRESHED);
                    successIntent.putExtra("success", true);
                    LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(successIntent);

                }
            }

            //finish syncing device details
            NetworkChangeReceiver.completeWakefulIntent(intent);

        } catch (Exception e) {

            //notify registration token update or create error
            Intent failIntent = new Intent(Push.REGISTRATION_TOKEN_REFRESHED);
            failIntent.putExtra("success", false);
            failIntent.putExtra("message", e.getMessage());
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(failIntent);

            //reschedule push device details sync in case of any exception
            NetworkChangeReceiver.completeWakefulIntent(intent);

        }
    }
}
