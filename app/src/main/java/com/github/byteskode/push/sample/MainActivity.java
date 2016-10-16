package com.github.byteskode.push.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.github.byteskode.push.Push;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(MainActivity.class.getSimpleName(), Push.getInstance(this).getInstanceId());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
