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

        Push.initialize(this, "https://www.example.com", "622hs4853YtWT1Pa7");
    }
}
