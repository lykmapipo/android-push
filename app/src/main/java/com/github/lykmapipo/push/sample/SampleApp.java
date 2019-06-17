package com.github.lykmapipo.push.sample;

import android.app.Application;

import com.github.lykmapipo.push.Push;

/**
 * @author lally elias <lallyelias87@gmail.com>
 * @date 10/18/16
 */
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //TODO replace with your baseUrl and registration token
        //TODO deploy sample api to heroku
        // Push.create(this, "http://192.168.43.151:3000", "SHOZZEKSJT");
        Push.create(this, "http://172.30.89.134:3000", "SHOZZEKSJT");
    }
}
