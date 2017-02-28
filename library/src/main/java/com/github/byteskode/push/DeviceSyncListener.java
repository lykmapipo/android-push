package com.github.byteskode.push;

import com.github.byteskode.push.api.Device;

/**
 * device sync listener
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/18/16
 */
public interface DeviceSyncListener {
    /**
     * called once a device is synced to the backend api
     *
     * @param device
     */
    void onDeviceSynced(Device device);
}
