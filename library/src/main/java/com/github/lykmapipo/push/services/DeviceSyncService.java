package com.github.lykmapipo.push.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.lykmapipo.localburst.LocalBurst;
import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.push.api.Device;
import com.github.lykmapipo.push.receivers.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import retrofit2.Response;

/**
 * sync device push details whenever network is become available
 *
 * @author lally elias <lallyelias87@gmail.com>
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
            boolean isConnected = push.isConnected();

            //obtain current registration token
            String currentRegistrationToken = push.getRegistrationToken();

            //obtain latest registration token from FirebaseInstanceId service
            String latestRegistrationToken = intent.getStringExtra(Push.REGISTRATION_TOKEN_PREF_KEY);

            // fetch token if not given
            if (TextUtils.isEmpty(latestRegistrationToken)) {
                fetchToken(intent);
            }
            // continue with syncing
            else {
                //check if token are different for updates
                boolean shouldUpdateServerToken =
                        !latestRegistrationToken.equals(currentRegistrationToken);

                if (isConnected && (shouldUpdateServerToken || forceSync)) {
                    //save or update current device registration token
                    push.setRegistrationToken(latestRegistrationToken);

                    //wait for api end point response
                    Response<Device> response;

                    //post device details
                    if (TextUtils.isEmpty(currentRegistrationToken)) {
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

    /**
     * Fetch already existing firebase registration token
     */
    private void fetchToken(Intent intent) {
        Task<InstanceIdResult> instanceId = FirebaseInstanceId.getInstance().getInstanceId();
        instanceId.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    // obtain task result
                    InstanceIdResult result = task.getResult();
                    if (result != null) {
                        // obtain token
                        String token = result.getToken();

                        // launch sync again
                        Intent service = new Intent(getApplicationContext(), DeviceSyncService.class);
                        if (intent != null) {
                            service.putExtras(intent);
                        }
                        service.putExtra(Push.REGISTRATION_TOKEN_PREF_KEY, token);
                        startService(service);
                    }
                }
            }
        });
    }
}
