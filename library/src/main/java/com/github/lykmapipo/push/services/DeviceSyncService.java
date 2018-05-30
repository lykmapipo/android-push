package com.github.lykmapipo.push.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.github.lykmapipo.localburst.LocalBurst;
import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.push.api.Device;
import com.github.lykmapipo.push.receivers.NetworkChangeReceiver;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Response;

/**
 * sync device push details whenever network is become available
 *
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/18/16
 */
public class DeviceSyncService extends IntentService {

    public static final String TAG = DeviceSyncService.class.getSimpleName();

    public DeviceSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //obtain action
        String action = intent.getAction();
        action = action != null ? action : Push.REGISTRATION_TOKEN_REFRESHED;

        //force sync
        boolean forceSync = intent.getBooleanExtra(Push.FORCE_DEVICE_SYNC, false);

        try {

            //obtain push instance
            Push push = Push.getInstance();

            //obtain current registration token
            String currentRegistrationToken = push.getRegistrationToken();

            //obtain latest registration token from FirebaseInstanceId service
            String latestRegistrationToken = FirebaseInstanceId.getInstance().getToken();

            //check if token are different for updates
            boolean shouldUpdateServerToken = !currentRegistrationToken.equals(latestRegistrationToken);

            if (shouldUpdateServerToken || forceSync) {
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

                    //update device
                    Device device = response.body();
                    push.merge(device);

                    //notify registration token updated or created successfully
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Push.SUCCESS, true);
                    LocalBurst.$emit(action, bundle);

                }
            }

            //finish syncing device details
            NetworkChangeReceiver.completeWakefulIntent(intent);

        } catch (Exception e) {

            //notify registration token update or create error
            Bundle bundle = new Bundle();
            bundle.putBoolean(Push.SUCCESS, false);
            bundle.putString(Push.MESSAGE, e.getMessage());
            LocalBurst.$emit(action, bundle);

            //reschedule push device details sync in case of any exception
            NetworkChangeReceiver.completeWakefulIntent(intent);

        }
    }
}
