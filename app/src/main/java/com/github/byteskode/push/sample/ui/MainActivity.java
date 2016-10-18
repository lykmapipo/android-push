package com.github.byteskode.push.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.github.byteskode.push.Push;
import com.github.byteskode.push.sample.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(MainActivity.class.getSimpleName(), Push.getInstance().getInstanceId());
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
