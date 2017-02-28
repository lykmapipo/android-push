package com.github.byteskode.push.sample;

import android.app.Application;
import com.github.byteskode.push.Push;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/18/16
 */
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //TODO replace with your baseUrl and registration token
        Push.initialize(this, "http://192.168.1.125:3000", "SHOZZEKSJT");
    }
}
