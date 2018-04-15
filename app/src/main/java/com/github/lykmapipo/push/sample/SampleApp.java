package com.github.lykmapipo.push.sample;

import android.app.Application;
import com.github.lykmapipo.push.Push;

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
        //TODO deploy sample api to heroku
        Push.initialize(this, "http://192.168.43.151:3000", "SHOZZEKSJT");
    }
}
