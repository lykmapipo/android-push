package com.github.lykmapipo.push.sample.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.push.api.Device;
import com.github.lykmapipo.push.sample.R;
import com.github.lykmapipo.push.ui.PushCompactActivity;
import com.google.firebase.messaging.RemoteMessage;


public class MainActivity extends PushCompactActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        Log.d(TAG, "UUID x1:" + Push.getInstance().getUUID());
        Log.d(TAG, "UUID x2:" + Push.getInstance().getUUID());
        Log.d(TAG, "Token:" + Push.getInstance().getRegistrationToken());

        //simulate force device sync
        Button syncButton = (Button) findViewById(R.id.sync);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Force device sync
                Push.getInstance().sync("phone", "255714999999");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message To: " + remoteMessage.getTo());
        Log.d(TAG, "Message Type: " + remoteMessage.getMessageType());
        Log.d(TAG, "Message Data: " + remoteMessage.getData());
        Log.d(TAG, "Message Notification: " + remoteMessage.getNotification());
        Toast.makeText(MainActivity.this, "Push Message Received", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegistrationTokenRefreshed(Device device) {
        Log.d(TAG, "DEVICE UUID: " + device.getUuid());
        Log.d(TAG, "DEVICE Registration Token: " + device.getRegistrationToken());
        Log.d(TAG, "DEVICE Registration InstanceID: " + device.getInstanceId());
        Toast.makeText(MainActivity.this, "Registration Token Refreshed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegistrationTokenError(String error) {
        Log.d(TAG, "PUSH ERROR: " + error);
    }

    @Override
    public void onDeviceSynced(Device device) {
        Log.d(TAG, "Synced DEVICE UUID: " + device.getUuid());
        Log.d(TAG, "Synced DEVICE Registration Token: " + device.getRegistrationToken());
        Log.d(TAG, "Synced DEVICE Registration InstanceID: " + device.getInstanceId());
        Log.d(TAG, "Synced DEVICE Extras: " + device.getExtras());
        Toast.makeText(MainActivity.this, "Device Synced", Toast.LENGTH_SHORT).show();

    }
}
