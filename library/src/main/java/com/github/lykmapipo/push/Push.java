package com.github.lykmapipo.push;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.github.lykmapipo.push.api.Device;
import com.github.lykmapipo.push.api.DeviceApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * simplified push notification for android application
 *
 * @author lally elias(lykmapipo), byteskode Team & Contibutors
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 */
public class Push {
    /**
     * key used to signal device fcm registration token refresh
     */
    public static final String REGISTRATION_TOKEN_REFRESHED = "tokenRefreshed";

    /**
     * key used to signal new received push message
     */
    public static final String PUSH_MESSAGE_RECEIVED = "pushMessageReceived";


    /**
     * key used to stoke latest fcm registration token
     */
    public static final String REGISTRATION_TOKEN_PREF_KEY = "registrationToken";


    /**
     * key used to store application instance id
     */
    public static final String INSTANCE_ID_PREF_KEY = "instanceId";


    /**
     * key used to store application subscribed push topics
     */
    public static final String TOPICS_PREF_KEY = "topics";


    /**
     * key used to store application specific details about the device(installation)
     */
    public static final String EXTRAS_PREF_KEY = "extras";

    /**
     * key used to store application device information
     */
    public static final String INFO_PREF_KEY = "info";


    /**
     * key used to store api server end point to post and update device push details
     */
    public static final String API_BASE_URL_PREF_KEY = "apiBaseUrl";


    /**
     * key used to store api server authorization token
     */
    public static final String API_AUTHORIZATION_TOKEN_PREF_KEY = "apiAuthorizationToken";


    /**
     * class instance
     */
    private static Push instance = null;


    /**
     * shared preference instance
     */
    private SharedPreferences preferences;


    /**
     * set of application subscribed push topics
     */
    private Set<String> topics = new HashSet<String>();

    /**
     * map of application extra details about the device(installation)
     */
    private Map<String, String> extras = new HashMap<String, String>();

    /**
     * latest push registration token
     */
    private String registrationToken = "";

    /**
     * server api endpoint to post and update device push details
     */
    private String apiBaseUrl = "";


    /**
     * server api authorization token
     */
    private String apiAuthorizationToken = "";


    /**
     * device api used to sync device push details to remove server
     */
    private DeviceApi deviceApi;

    /**
     * unique device identifier based on hardware details
     */
    private String uuid;


    /**
     * holding context
     */
    private Context context;

    /**
     * push message listener
     */
    private Set<PushMessageListener> pushMessageListeners;

    /**
     * push token listener
     */
    private Set<PushTokenListener> pushTokenListeners;

    /**
     * device sync listener
     */
    private Set<DeviceSyncListener> deviceSyncListeners;

    /**
     * listen to registration token broadcast
     */
    private BroadcastReceiver registrationTokenReceiver;

    /**
     * listen to push message
     */
    private BroadcastReceiver messageReceiver;


    /**
     * Private constructor
     *
     * @param context
     */
    private Push(Context context) {
        this.context = context;
    }


    /**
     * obtain current push instance
     *
     * @return {@link com.github.lykmapipo.push.Push}
     */
    public static synchronized Push getInstance() {
        return instance;
    }


    /**
     * initialize new push instance
     *
     * @return {@link com.github.lykmapipo.push.Push}
     */
    public static synchronized Push initialize(Context context, String apiBaseUrl, String apiAuthorizationToken) {

        if (instance == null) {

            //instantiate new push
            instance = new Push(context);

            //initialize
            instance.init(apiBaseUrl, apiAuthorizationToken);
        }

        return instance;
    }


    /**
     * initialize push internal
     */
    private void init(String apiBaseUrl, String apiAuthorizationToken) {

        //obtain preference manager
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        //set api base url
        instance.setApiBaseUrl(apiBaseUrl);

        //set authorization token
        instance.setApiAuthorizationToken(apiAuthorizationToken);

        //load existing topics
        this.topics = preferences.getStringSet(TOPICS_PREF_KEY, this.topics);

        //initialize device server api endpoints
        if ((this.deviceApi == null) && (this.apiBaseUrl != null) && !this.apiBaseUrl.isEmpty()) {

            //prepare gson convertor
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                    .excludeFieldsWithoutExposeAnnotation()
                    .serializeNulls()
                    .create();

            //prepare retrofit device api endpoint client
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(this.apiBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            deviceApi = retrofit.create(DeviceApi.class);
        }

        //initialize local broadcast receiver to listen for token refresh
        registerTokenReceiver();

        //initialize local broadcast receiver to listen for push message
        registerMessageReceiver();
    }

    private void registerTokenReceiver() {
        if (registrationTokenReceiver == null) {
            registrationTokenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //check for success synced registration token
                    boolean isSuccess = intent.getBooleanExtra("success", false);

                    //notify token push listener
                    if (pushTokenListeners != null && !pushTokenListeners.isEmpty()) {
                        for (PushTokenListener pushTokenListener : pushTokenListeners) {
                            //notify success token refresh listener
                            if (isSuccess) {
                                pushTokenListener.onRegistrationTokenRefreshed(getDevice());
                            }
                            //notify token refresh error listener
                            else {
                                String errorMessage = intent.getStringExtra("message");
                                pushTokenListener.onRegistrationTokenError(errorMessage);
                            }
                        }
                    }
                }
            };

            LocalBroadcastManager.getInstance(context).registerReceiver(
                    registrationTokenReceiver,
                    new IntentFilter(REGISTRATION_TOKEN_REFRESHED)
            );
        }
    }

    private void registerMessageReceiver() {
        if (messageReceiver == null) {
            messageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //obtain remote message
                    RemoteMessage message = (RemoteMessage) intent.getParcelableExtra("message");

                    if ((pushMessageListeners != null) && !pushMessageListeners.isEmpty()) {
                        for (PushMessageListener pushMessageListener : pushMessageListeners) {
                            //notify push message listener
                            pushMessageListener.onMessage(message);
                        }
                    }
                }
            };

            LocalBroadcastManager.getInstance(context).registerReceiver(
                    messageReceiver,
                    new IntentFilter(PUSH_MESSAGE_RECEIVED)
            );
        }
    }


    /**
     * Returns a generated unique pseudo ID from android.os.Build Constants
     *
     * @return a pseudo id
     */
    public String getUUID() {
        if ((uuid != null) && !uuid.isEmpty()) {
            return uuid;
        } else {

            //prepare device uuid
            StringBuilder uuid = new StringBuilder();
            uuid.append(context.getPackageName()).append(":"); //name of this application's package
            uuid.append(Build.BOARD).append(":"); //underlying board
            uuid.append(Build.BRAND).append(":"); //consumer-visible brand with which the product/hardware will be associated, if any
            uuid.append(Build.DEVICE).append(":"); //name of industrial design
            uuid.append(Build.MANUFACTURER).append(":"); //manufacturer of the product/hardware
            uuid.append(Build.MODEL).append(":"); //end-user-visible name for the end product
            uuid.append(Build.PRODUCT).append(":"); //name of the overall product
            uuid.append(Build.SERIAL).append(":"); //hardware serial number
            uuid.append(Build.BOOTLOADER);//system bootloader version number

            //hash the uuid
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                messageDigest.update(uuid.toString().getBytes());
                this.uuid = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
            } catch (NoSuchAlgorithmException e) {
                this.uuid = Base64.encodeToString(uuid.toString().getBytes(), Base64.DEFAULT);
            }

            return this.uuid;
        }
    }

    /**
     * obtain device endpoint api client
     *
     * @return
     */
    public DeviceApi getDeviceApi() {
        return deviceApi;
    }

    /**
     * register push notification message listener
     *
     * @param listener
     */
    public void registerPushMessageListener(PushMessageListener listener) {
        if (this.pushMessageListeners == null) {
            this.pushMessageListeners = new HashSet<PushMessageListener>();
        }
        this.pushMessageListeners.add(listener);
    }

    /**
     * unregister push notification message listener
     */
    public void unregisterPushMessageListener(PushMessageListener pushMessageListener) {
        if (this.pushMessageListeners != null) {
            this.pushMessageListeners.remove(pushMessageListener);
        }
    }

    /**
     * register push token listener
     *
     * @param pushTokenListener
     */
    public void registerPushTokenListener(PushTokenListener pushTokenListener) {
        if (this.pushTokenListeners == null) {
            this.pushTokenListeners = new HashSet<PushTokenListener>();
        }
        this.pushTokenListeners.add(pushTokenListener);
    }

    /**
     * register push token listener
     */
    public void unregisterPushTokenListener(PushTokenListener pushTokenListener) {
        if (this.pushTokenListeners != null) {
            this.pushTokenListeners.remove(pushTokenListener);
        }
    }

    /**
     * register device sync listener
     *
     * @param deviceSyncListener
     */
    public void registerDeviceSyncListener(DeviceSyncListener deviceSyncListener) {
        if (this.deviceSyncListeners == null) {
            this.deviceSyncListeners = new HashSet<DeviceSyncListener>();
        }
        this.deviceSyncListeners.add(deviceSyncListener);
    }


    /**
     * register push token listener
     */
    public void unregisterDeviceSyncListener(DeviceSyncListener deviceSyncListener) {
        if (this.deviceSyncListeners != null) {
            this.deviceSyncListeners.remove(deviceSyncListener);
        }
    }

    /**
     * save server api endpoint to post and update device push details
     *
     * @param apiBaseUrl
     * @return
     */
    public String setApiBaseUrl(String apiBaseUrl) {
        //save api url to shared preference
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_BASE_URL_PREF_KEY, apiBaseUrl);
        editor.apply();

        //update in memory apiBaseUrl
        this.apiBaseUrl = apiBaseUrl;

        return this.apiBaseUrl;
    }

    /**
     * get server api endpoint to post and update device push details
     *
     * @return
     */
    public String getApiBaseUrl() {
        String apiUrl = preferences.getString(API_BASE_URL_PREF_KEY, this.apiBaseUrl);
        return apiUrl;
    }


    /**
     * save server api endpoint authorization token to post and update device push details
     *
     * @param authorizationToken
     * @return
     */
    public String setApiAuthorizationToken(String authorizationToken) {
        //save api authorization token to shared preference
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_AUTHORIZATION_TOKEN_PREF_KEY, authorizationToken);
        editor.apply();

        //update in memory api authorization token
        this.apiAuthorizationToken = authorizationToken;

        return this.apiAuthorizationToken;
    }


    /**
     * get server api endpoint authorization token to post and update device push details
     *
     * @return
     */
    public String getApiAuthorizationToken() {
        String apiAuthorizationToken =
                preferences.getString(API_AUTHORIZATION_TOKEN_PREF_KEY, this.apiAuthorizationToken);
        return apiAuthorizationToken;
    }


    /**
     * Compute installation unique instance id
     *
     * @return instanceId
     */
    public String getInstanceId() {
        String instanceId = FirebaseInstanceId.getInstance().getId();
        return instanceId;
    }


    /**
     * Subscribe to a given push topic
     *
     * @param topic
     * @see {@link com.google.firebase.messaging.FirebaseMessaging}
     */
    public Set<String> subscribe(String topic) {
        try {
            //add topic to application push topics
            if ((topic != null) && !topic.isEmpty()) {
                topics.add(topic);
            }

            // persist application push topics
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(TOPICS_PREF_KEY, topics);
            editor.apply();

            //subscribe application to firebase push topic
            FirebaseMessaging.getInstance().subscribeToTopic(topic);

            return topics;

        } catch (Exception e) {
            return topics;
        }
    }


    /**
     * Unsubscribe from a given push topic
     *
     * @param topic
     * @see {@link com.google.firebase.messaging.FirebaseMessaging}
     */
    public Set<String> unsubscribe(String topic) {
        try {
            if ((topic != null) & !topic.isEmpty()) {
                topics.remove(topic);
            }

            //persist application push topics
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(TOPICS_PREF_KEY, topics);
            editor.apply();

            //un subscribe application from firebase push topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);

            return topics;
        } catch (Exception e) {
            return topics;
        }
    }

    /**
     * obtain list of push topic an application subscribe on
     *
     * @return topics
     */
    public Set<String> getTopics() {
        Set<String> topics = preferences.getStringSet(TOPICS_PREF_KEY, this.topics);
        return topics;
    }

    /**
     * set application device info details
     *
     * @param info
     * @return
     */
    public Map<String, String> setInfo(Map<String, String> info) {
        //ensure info
        Map<String, String> _info = getDeviceInfo();
        _info.putAll(info);

        //save and return device info
        _info = saveMapOfPreferences(INFO_PREF_KEY, info);
        return _info;
    }


    /**
     * add application specific device details
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, String> putInfo(String key, String value) {
        Map<String, String> info = new HashMap<String, String>();

        if ((key != null && !key.isEmpty()) && (value != null && !value.isEmpty())) {
            info.put(key, value);
        }

        //update extras
        info = setInfo(info);

        return info;
    }

    /**
     * obtain device(installation) extra details
     *
     * @return topics
     */
    public Map<String, String> getInfo() {
        Map<String, String> info = setInfo(getDeviceInfo());
        return info;
    }


    /**
     * set application extra details on a push device
     *
     * @param extras
     * @return
     */
    public Map<String, String> setExtras(Map<String, String> extras) {
        //save and get extras
        Map<String, String> _extras = saveMapOfPreferences(EXTRAS_PREF_KEY, extras);
        return _extras;
    }


    /**
     * add application specific extra details
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, String> putExtra(String key, String value) {
        Map<String, String> extras = new HashMap<String, String>();

        if ((key != null && !key.isEmpty()) && (value != null && !value.isEmpty())) {
            extras.put(key, value);
        }

        //update extras
        extras = setExtras(extras);

        return extras;
    }

    /**
     * obtain device(installation) extra details
     *
     * @return topics
     */
    public Map<String, String> getExtras() {
        Map<String, String> extras = getMapOfPreferences(EXTRAS_PREF_KEY);
        return extras;
    }


    /**
     * Save device push registration token in shared preferences
     *
     * @param token
     * @return registrationToken
     */
    public String setRegistrationToken(String token) {
        //persist push registration token
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(REGISTRATION_TOKEN_PREF_KEY, token);
        editor.apply();

        //set instance latest registration token
        registrationToken = token;

        return registrationToken;
    }


    /**
     * obtain current application push registration token
     *
     * @return registrationToken
     */
    public String getRegistrationToken() {
        String token = preferences.getString(REGISTRATION_TOKEN_PREF_KEY, registrationToken);
        return token;
    }


    /**
     * Send push message
     *
     * @param message
     * @see {@link com.google.firebase.messaging.RemoteMessage}
     * @see {@link com.google.firebase.messaging.FirebaseMessaging}
     */
    public void send(RemoteMessage message) {
        //TODO implement sent and error callback
        //TODO send message to api server
        FirebaseMessaging.getInstance().send(message);
    }


    /**
     * create new device push registration details in remote api server(app server)
     *
     * @param registrationToken
     * @return Response<Device> or null
     * @throws IOException
     */
    public Response<Device> create(String registrationToken) throws IOException {
        if (this.deviceApi != null && this.isConnected()) {
            //prepare device
            Device device = getDevice(registrationToken);

            //prepare authorization header value
            String authorization = "Bearer " + this.getApiAuthorizationToken();

            //call POST /devices
            Call<Device> call = this.deviceApi.create(authorization, device);
            Response<Device> response = call.execute();
            return response;
        } else {
            return null;
        }
    }

    private Device getDevice(String registrationToken) {
        return new Device(this.getUUID(), this.getInstanceId(), registrationToken,
                this.getTopics(), this.getExtras(), this.getInfo());
    }

    private Device getDevice() {
        return new Device(this.getUUID(), this.getInstanceId(), this.getRegistrationToken(),
                this.getTopics(), this.getExtras(), this.getInfo());
    }


    /**
     * update existing device push registration details in remote api server(app server)
     *
     * @param registrationToken
     * @return Response<Device> or null
     * @throws IOException
     */
    public Response<Device> update(String registrationToken) throws IOException {
        if (this.deviceApi != null && this.isConnected()) {
            //prepare device
            Device device = getDevice(registrationToken);

            //prepare authorization header value
            String authorization = "Bearer " + this.getApiAuthorizationToken();

            //call PUT /devices
            Call<Device> call = this.deviceApi.update(authorization, device);
            Response<Device> response = call.execute();
            return response;
        } else {
            return null;
        }
    }


    /**
     * Checks if there is Internet connection or data connection on the device.
     *
     * @return boolean
     */
    public boolean isConnected() {
        return Utils.isConnected(context);
    }

    /**
     * Check if device Wi-Fi is enabled or disabled.
     *
     * @return boolean
     */
    public boolean isWifiEnabled() {
        return Utils.isWifiEnabled(context);
    }

    /**
     * Derive current device connected network type
     *
     * @return String
     */
    public String getNetworkType() {
        return Utils.getNetworkType(context);
    }

    /**
     * Checks if device connected on wifi
     *
     * @return boolean
     */
    public boolean isConnectedOnWiFi() {
        return isConnected() && getNetworkType().equals(Utils.NETWORK_TYPE_WIFI);
    }

    /**
     * Checks if device connected on mobile data
     *
     * @return boolean
     */
    public boolean isConnectedOnMobile() {
        return isConnected() && getNetworkType().equals(Utils.NETWORK_TYPE_MOBILE);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK
     */
    public boolean isGooglePlayServiceAvailable() {

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int status = api.isGooglePlayServicesAvailable(this.context);

        if (status == ConnectionResult.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * sync device details to backend api
     *
     * @return
     */
    public Device sync() {
        Map<String, String> extras = new HashMap<String, String>();
        Device sync = sync(extras);
        return sync;
    }

    /**
     * sync device details to backend api
     *
     * @param extraKey
     * @param extraValue
     * @return
     */
    public Device sync(String extraKey, String extraValue) {

        Map<String, String> extras = new HashMap<String, String>();

        if ((extraKey != null && !extraKey.isEmpty()) && (extraValue != null && !extraValue.isEmpty())) {
            extras.put(extraKey, extraValue);
        }

        Device device = sync(extras);

        return device;
    }

    /**
     * sync device details to backend api
     *
     * @param extras
     * @return
     */
    public Device sync(Map<String, String> extras) {
        try {
            //update device extras
            Map<String, String> _extras = setExtras(extras);

            //TODO ensure internet connections before sync

            //prepare device sync task
            AsyncTask<Void, Void, Device> syncTask = new AsyncTask<Void, Void, Device>() {
                @Override
                protected Device doInBackground(Void... params) {
                    final String registrationToken = getRegistrationToken();
                    try {
                        Response<Device> response = update(registrationToken);
                        if (response != null && response.isSuccessful()) {
                            Device device = response.body();
                            return device;
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Device device) {
                    if (device == null) {
                        device = getDevice();
                    }
                    //notify device sync listeners
                    if ((deviceSyncListeners != null) && !deviceSyncListeners.isEmpty()) {
                        for (DeviceSyncListener deviceSyncListener : deviceSyncListeners) {
                            deviceSyncListener.onDeviceSynced(device);
                        }
                    }
                }
            };
            //update device details asynchronous
            syncTask.execute();

            //return last updated device
            return getDevice();
        } catch (Exception e) {
            //TODO implement device sync error listener
            return null;
        }
    }


    /**
     * Obtain map of shared preferences by provide key
     *
     * @param key
     * @return
     */
    private Map<String, String> getMapOfPreferences(String key) {

        HashMap<String, String> preferences = new HashMap<String, String>();

        Set<String> preferenceSet = this.preferences.getStringSet(key, new HashSet<String>());

        for (String preference : preferenceSet) {

            //split to obtain key value
            try {
                String[] splits = preference.split(":");
                if (splits != null && splits.length == 2) {
                    preferences.put(splits[0], splits[1]);
                }
            } catch (Exception e) {
            }
        }

        return preferences;
    }


    /**
     * Save map of data shared preferences using specified key
     *
     * @param key
     * @param data
     * @return
     */
    private Map<String, String> saveMapOfPreferences(String key, Map<String, String> data) {

        //obtain existing preferences
        Map<String, String> _data = getMapOfPreferences(key);

        //merge with provided data
        try {

            _data.putAll(data);

            //save data as key:value in extra set
            Set<String> dataSet = new HashSet<>();
            for (String _key : _data.keySet()) {
                String value = _key + ":" + _data.get(_key);
                dataSet.add(value);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(key, dataSet);
            editor.apply();

        } catch (Exception e) {
        }

        return _data;

    }

    /**
     * Obtain device details
     *
     * @return
     */
    private Map<String, String> getDeviceInfo() {
        Map<String, String> deviceInfo = new HashMap<String, String>();

        /** @see Build.BOOTLOADER */
        deviceInfo.put(Device.BOOTLOADER, asEmpty(Build.BOOTLOADER));

        /** @see Build.BOARD */
        deviceInfo.put(Device.BOARD, asEmpty(Build.BOARD));

        /** @see Build.BRAND */
        deviceInfo.put(Device.BRAND, asEmpty(Build.BRAND));

        /** @see Locale */
        deviceInfo.put(Device.COUNTRY_CODE, asEmpty(getCountryCode()));

        /** @see Locale */
        deviceInfo.put(Device.COUNTRY_NAME, asEmpty(getCountryName()));

        /** @see Build.DEVICE */
        deviceInfo.put(Device.DEVICE, asEmpty(Build.DEVICE));

        /** @see Build.DISPLAY */
        deviceInfo.put(Device.DISPLAY, asEmpty(Build.DISPLAY));

        /** @see Build.FINGERPRINT */
        deviceInfo.put(Device.FINGERPRINT, asEmpty(Build.FINGERPRINT));

        /** @see Build.HARDWARE */
        deviceInfo.put(Device.HARDWARE, asEmpty(Build.HARDWARE));

        /** @see Locale */
        deviceInfo.put(Device.LANGUAGE_CODE, asEmpty(getLanguageCode()));

        /** @see Locale */
        deviceInfo.put(Device.LANGUAGE_NAME, asEmpty(getLanguageName()));

        /** @see Build.MANUFACTURER */
        deviceInfo.put(Device.MANUFACTURER, asEmpty(Build.MANUFACTURER));

        /** @see Build.MODEL */
        deviceInfo.put(Device.MODEL, asEmpty(Build.MODEL));

        /** @see Context */
        deviceInfo.put(Device.PACKAGE, asEmpty(context.getPackageName()));

        /** @see Build.PRODUCT */
        deviceInfo.put(Device.PRODUCT, asEmpty(Build.PRODUCT));

        /** @see Build.VERSION.RELEASE */
        deviceInfo.put(Device.RELEASE, asEmpty(Build.VERSION.RELEASE));

        /** @see Build.getRadioVersion() */
        deviceInfo.put(Device.RADIO_VERSION, asEmpty(Build.getRadioVersion()));

        /** @see DisplayMetrics */
        deviceInfo.put(Device.SCREEN_DENSITY, asEmpty(getScreenDensity()));

        /** @see WindowManager */
        deviceInfo.put(Device.SCREEN_HEIGHT, asEmpty(String.valueOf(getScreenHeight())));

        /** @see WindowManager */
        deviceInfo.put(Device.SCREEN_WIDTH, asEmpty(String.valueOf(getScreenWidth())));

        /** @see Build.VERSION.SDK_INT */
        deviceInfo.put(Device.SDK, asEmpty(String.valueOf(Build.VERSION.SDK_INT)));

        /** @see Build.SERIAL */
        deviceInfo.put(Device.SERIAL, asEmpty(Build.SERIAL));

        /** @see TimeZone */
        deviceInfo.put(Device.TIMEZONE, asEmpty(getTimezone()));

        /** @see Build.TYPE */
        deviceInfo.put(Device.TYPE, asEmpty(Build.TYPE));

        /** @see Build.TAGS */
        deviceInfo.put(Device.USER, asEmpty(Build.USER));

        /** @see Build.USER */
        deviceInfo.put(Device.TAGS, asEmpty(Build.TAGS));

        /** @see PackageInfo */
        deviceInfo.put(Device.VERSION_CODE, asEmpty(String.valueOf(getVersionCode())));

        /** @see PackageInfo */
        deviceInfo.put(Device.VERSION_NAME, asEmpty(String.valueOf(getVersionName())));

        //merge storage information
        deviceInfo.putAll(Utils.getMemoryInfo(context));

        return deviceInfo;

    }

    /**
     * Derive screen density
     *
     * @return
     */
    public String getScreenDensity() {
        String screenType = "";
        try {
            int density = context.getResources().getDisplayMetrics().densityDpi;
            switch (density) {
                case DisplayMetrics.DENSITY_LOW:
                    screenType = "ldpi";
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    screenType = "mdpi";
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    screenType = "hdpi";
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    screenType = "xhdpi";
                    break;
                default:
                    screenType = "other";
                    break;
            }
        } catch (Exception e) {
            screenType = "";
        }
        return screenType;
    }


    /**
     * Device device screen height
     *
     * @return
     */
    public int getScreenHeight() {
        int height = 0;
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            //display.getRealSize(outPoint); // include navigation bar
            display.getSize(size);
            height = size.y;
        } catch (Exception e) {
            height = 0;
        }
        return height;
    }

    /**
     * Derive screen width
     *
     * @return
     */
    public int getScreenWidth() {
        int width = 0;
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            //display.getRealSize(outPoint); // include navigation bar
            display.getSize(size);
            width = size.x;
        } catch (Exception e) {
            width = 0;
        }
        return width;
    }


    /**
     * Derive device country code
     *
     * @return
     */
    public String getCountryCode() {
        String countryCode = "";
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            countryCode = locale.getCountry();
        } catch (Exception e) {
            countryCode = "";
        }

        return countryCode;

    }

    /**
     * Derive device country name
     *
     * @return
     */
    public String getCountryName() {
        String countryName = "";
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            countryName = locale.getDisplayCountry();
        } catch (Exception e) {
            countryName = "";
        }

        return countryName;

    }

    /**
     * Derive device language code
     *
     * @return
     */
    public String getLanguageCode() {
        String languageCode = "";
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            languageCode = locale.getLanguage();
        } catch (Exception e) {
            languageCode = "";
        }

        return languageCode;

    }

    /**
     * Derive device language name
     *
     * @return
     */
    public String getLanguageName() {
        String languageName = "";
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            languageName = locale.getDisplayLanguage();
        } catch (Exception e) {
            languageName = "";
        }

        return languageName;

    }

    /**
     * Derive device timezone
     *
     * @return
     */
    public String getTimezone() {
        String timezone = "";
        try {
            TimeZone timeZone = TimeZone.getDefault();
            timezone = timeZone.getID();
        } catch (Exception e) {
            timezone = "";
        }

        return timezone;
    }

    /**
     * Derive application version name
     *
     * @return
     */
    public String getVersionName() {
        String versionName = "";
        try {
            PackageInfo packageInfo;
            packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception e1) {
            versionName = "";
        }
        return versionName;
    }

    /**
     * Derive application version code
     *
     * @return
     */
    public Integer getVersionCode() {
        Integer versionCode = null;
        try {
            PackageInfo packageInfo;
            packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e1) {
            versionCode = null;
        }
        return versionCode;
    }


    public String asEmpty(String value) {
        if (value == null) {
            value = "";
        }
        return value;
    }


    /**
     * clear push shared preferences
     */
    public void clear() {
        //clear all push preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(REGISTRATION_TOKEN_PREF_KEY);
        editor.remove(INSTANCE_ID_PREF_KEY);
        editor.remove(TOPICS_PREF_KEY);
        editor.remove(EXTRAS_PREF_KEY);
        editor.remove(INFO_PREF_KEY);
        editor.remove(API_BASE_URL_PREF_KEY);
        editor.remove(API_AUTHORIZATION_TOKEN_PREF_KEY);
        editor.apply();
    }

}