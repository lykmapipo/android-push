package com.github.byteskode.push.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.api.Device;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.firebase.iid.FirebaseInstanceId;
import retrofit2.Response;

/**
 * sync device push details whenever network is become available
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/18/16
 */
public class DeviceSyncService extends GcmTaskService {

    public static final String TASK_TAG = "deviceSync";

    @Override
    public int onRunTask(TaskParams taskParams) {
        try {
            //obtain push instance
            Push push = Push.getInstance();

            //obtain current registration token
            String currentRegistrationToken = push.getRegistrationToken();

            //obtain latest registration token from FirebaseInstanceId service
            String latestRegistrationToken = FirebaseInstanceId.getInstance().getToken();

            //check if token are different for updates
            boolean shouldUpdateServerToken = !currentRegistrationToken.equals(latestRegistrationToken);

            //prepare task result
            int result = GcmNetworkManager.RESULT_SUCCESS;

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

                if ((response != null)) {
                    if (response.isSuccessful()) {
                        result = GcmNetworkManager.RESULT_SUCCESS;

                        //notify registration token updated or created successfully
                        Intent intent = new Intent(Push.REGISTRATION_TOKEN_REFRESHED);
                        intent.putExtra("success", true);
                        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);

                    } else {
                        //reschedule push device details sync
                        result = GcmNetworkManager.RESULT_RESCHEDULE;
                    }
                }

            }

            return result;

        } catch (Exception e) {

            //notify registration token update or create error
            Intent intent = new Intent(Push.REGISTRATION_TOKEN_REFRESHED);
            intent.putExtra("success", false);
            intent.putExtra("message", e.getMessage());
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);

            //reschedule push device details sync in case of any exception
            return GcmNetworkManager.RESULT_RESCHEDULE;

        }
    }
}
