package com.github.byteskode.push.services;

import android.app.IntentService;
import android.content.Intent;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.api.Device;
import com.google.firebase.iid.FirebaseInstanceId;
import retrofit2.Response;

/**
 * receive new registration token, save it in shared preference and send to api server
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/17/16
 */
public class RegistrationTokenService extends IntentService {
    //TODO add logs
    private static final String TAG = RegistrationTokenService.class.getSimpleName();

    /**
     * Creates an IntentService
     */
    public RegistrationTokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //obtain push instance
            Push push = Push.getInstance(getApplicationContext());

            //obtain current registration token
            String currentRegistrationToken = push.getRegistrationToken();

            //obtain latest registration token from FirebaseInstanceId service
            String latestRegistrationToken = FirebaseInstanceId.getInstance().getToken();

            //check if token are different for updates
            boolean shouldUpdateServerToken = !currentRegistrationToken.equals(latestRegistrationToken);


            if (shouldUpdateServerToken) {
                //save or update current device registration token
                push.saveRegistrationToken(latestRegistrationToken);

                //prepare device push details
                Device device = new Device(push.getInstanceId(), latestRegistrationToken, push.getTopics());

                Response<Device> response;

                //post device details
                if (currentRegistrationToken.equals(null)) {
                    response = push.getDeviceApi().create(device).execute();
                }
                //put device details
                else {
                    response = push.getDeviceApi().update(device).execute();
                }

                if (response.isSuccessful()) {
                    //TODO handle success and notify new token
                } else {
                    //TODO handle failure and notify new token
                }

                //TODO in case of network failure schedule retries or use wake and boot events
            }

        } catch (Exception e) {
            //TODO notify event
        }
    }
}
