package com.github.lykmapipo.push;

import com.github.lykmapipo.push.api.Device;

/**
 * device sync listener
 *
 * @author lally elias <lallyelias87@gmail.com>
 * @date 10/18/16
 */
public interface DeviceSyncListener {
    /**
     * called once a device is synced to the backend api
     *
     * @param device
     */
    void onDeviceSynced(Device device);

    /**
     * called once device sync failed
     *
     * @param error
     */
    void onDeviceSyncError(String error);
}
