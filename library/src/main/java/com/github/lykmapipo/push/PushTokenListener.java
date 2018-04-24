package com.github.lykmapipo.push;

import com.github.lykmapipo.push.api.Device;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/22/16
 */
public interface PushTokenListener {
    /**
     * Called when device registration token is successfully refreshed and synced to the API server
     *
     * @param device
     */
    void onRegistrationTokenRefreshed(Device device);

    /**
     * @param error
     */
    void onRegistrationTokenError(String error);
}
