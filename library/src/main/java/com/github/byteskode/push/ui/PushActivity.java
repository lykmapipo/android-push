package com.github.byteskode.push.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.PushMessageListener;
import com.github.byteskode.push.PushTokenListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * base push aware activity
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 11/01/16
 */
public abstract class PushActivity extends Activity
        implements PushMessageListener, PushTokenListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        register();
    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    private void register() {
        if (hasPlayServices()) {
            Push push = Push.getInstance();

            //register listeners
            push.registerPushMessageListener(this);
            push.registerPushTokenListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Push push = Push.getInstance();

        //unregister listener
        push.unregisterPushMessageListener(this);
        push.unregisterPushTokenListener(this);

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean hasPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog errorDialog =
                        apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                errorDialog.show();
            } else {
                finish();
            }

            return false;
        }

        return true;
    }
}
