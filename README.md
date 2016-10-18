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

## Test
```sh
./gradlew test
```
