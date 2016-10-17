package com.github.byteskode.push.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * receive new registration token, save it in shared preference and send to api server
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/17/16
 */
public class RegistrationTokenService extends IntentService {
    private static final String TAG = RegistrationTokenService.class.getSimpleName();

    /**
     * Creates an IntentService
     */
    public RegistrationTokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
