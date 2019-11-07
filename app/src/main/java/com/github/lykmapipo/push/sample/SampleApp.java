package com.github.lykmapipo.push.sample;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.github.lykmapipo.common.provider.Provider;
import com.github.lykmapipo.push.Push;
import com.github.lykmapipo.retrofit.provider.AuthProvider;

/**
 * @author lally elias <lallyelias87@gmail.com>
 * @date 10/18/16
 */
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //TODO deploy sample api to heroku
        Push.of("http://192.168.43.78:3000", new Provider() {
            @NonNull
            @Override
            public Context getApplicationContext() {
                return SampleApp.this.getApplicationContext();
            }
        }, new AuthProvider() {
            @Override
            public String getToken() {
                return "SHOZZEKSJT";
            }
        });
    }
}
