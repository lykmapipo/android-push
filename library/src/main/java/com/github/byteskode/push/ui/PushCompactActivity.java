package com.github.byteskode.push.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.PushMessageListener;
import com.github.byteskode.push.PushTokenListener;

/**
 * base push aware compact activity
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 11/01/16
 */
public abstract class PushCompactActivity extends AppCompatActivity
        implements PushMessageListener, PushTokenListener {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Push push = Push.getInstance();

        //register listeners
        push.registerPushMessageListener(this);
        push.registerPushTokenListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Push push = Push.getInstance();

        //register listeners
        push.registerPushMessageListener(this);
        push.registerPushTokenListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Push push = Push.getInstance();

        //unregister listener
        push.unregisterPushMessageListener(this);
        push.unregisterPushTokenListener(this);

    }
}
