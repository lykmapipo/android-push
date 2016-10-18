byteskode-android-push(WIP)
=========================

[![](https://jitpack.io/v/lykmapipo/byteskode-android-push.svg)](https://jitpack.io/#lykmapipo/byteskode-android-push)


byteskode push - Android FCM library

## Installation
Add [https://jitpack.io](https://jitpack.io) to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
add `byteskode-android-push` dependency into your project

```gradle
dependencies {
    compile 'com.github.byteskode:byteskode-android-push:{latest version}'
}
```

## Usage

Initialize `byteskode-android-push`

```java
public class SampleApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize push
        Push.initialize(<context>, <apiBaseUrl>, <apiAuthorizationToken>);
    }

}
```

In activity start listen for the foreground push message

```java
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Push Received", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

## Test
```sh
./gradlew test
```
