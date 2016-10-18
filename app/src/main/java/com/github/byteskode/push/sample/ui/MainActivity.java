package com.github.byteskode.push.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.PushMessageListener;
import com.github.byteskode.push.sample.R;
import com.google.firebase.messaging.RemoteMessage;


public class MainActivity extends Activity implements PushMessageListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Push.getInstance().registerPushMessageListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Push.getInstance().registerPushMessageListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Push.getInstance().unregisterPushMessageListener();
    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {
        Toast.makeText(this, "Push Received", Toast.LENGTH_SHORT);
    }
}
