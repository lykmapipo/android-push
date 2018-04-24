package com.github.lykmapipo.push.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.github.lykmapipo.push.DeviceSyncListener;
import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.push.PushMessageListener;
import com.github.lykmapipo.push.PushTokenListener;

/**
 * base push aware activity
 *
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 11/01/16
 */
public abstract class PushActivity extends Activity
        implements PushMessageListener, PushTokenListener, DeviceSyncListener {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Push.$register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Push.$register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Push.$register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Push.$unregister(this);
    }

    /**
     * Check the device to make sure it has the Google Play Services.
     */
    protected boolean isGooglePlayServiceAvailable() {
        Push push = Push.getInstance();
        boolean hasPlayService = (push != null && push.isGooglePlayServiceAvailable());
        return hasPlayService;
    }
}
