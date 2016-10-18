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
    compile 'com.github.lykmapipo:byteskode-android-push:v0.1.0'
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

## API Server Implementation

API Server endpoint must implemnt `devices resource` and support both `POST` and `PUT` request.

- [See Sample](https://github.com/lykmapipo/byteskode-android-push/blob/master/api/index.js)


## Test
```sh
./gradlew test
```

## Contribute
It will be nice, if you open an issue first so that we can know what is going on, then, fork this repo and push in your ideas.
Do not forget to add a bit of test(s) of what value you adding.

## License

(The MIT License)

Copyright (c) 2011 lykmapipo, byteskode Group && Contributors

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
