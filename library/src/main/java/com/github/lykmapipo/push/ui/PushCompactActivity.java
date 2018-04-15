package com.github.lykmapipo.push.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.lykmapipo.push.DeviceSyncListener;
import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.push.PushMessageListener;
import com.github.lykmapipo.push.PushTokenListener;

/**
 * base push aware compact activity
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 11/01/16
 */
public abstract class PushCompactActivity extends AppCompatActivity
        implements PushMessageListener, PushTokenListener, DeviceSyncListener {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        register();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register();
    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    private void register() {
        if (isGooglePlayServiceAvailable()) {
            Push push = Push.getInstance();
            push.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Push push = Push.getInstance();
        push.unregister(this);

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
