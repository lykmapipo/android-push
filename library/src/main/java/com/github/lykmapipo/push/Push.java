package com.github.lykmapipo.push;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.lykmapipo.localburst.LocalBurst;
import com.github.lykmapipo.preference.Preferences;
import com.github.lykmapipo.push.api.Device;
import com.github.lykmapipo.push.api.DeviceApi;
import com.github.lykmapipo.push.services.DeviceSyncService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * simplified push notification for android application
 *
 * @author lally elias(lykmapipo) & Contibutors
 * @email lallyelias87@gmail.com
 */
public class Push implements LocalBurst.OnBroadcastListener {

    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String FORCE_DEVICE_SYNC = "forceDeviceSync";

    /**
     * key used to signal device fcm registration token refresh
     */
    public static final String REGISTRATION_TOKEN_REFRESHED = "tokenRefreshed";

    /**
     * key used to signal new received push message
     */
    public static final String PUSH_MESSAGE_RECEIVED = "pushMessageReceived";

    /**
     * key used to signal device synced
     */
    public static final String DEVICE_SYNCED = "deviceSynced";


    /**
     * key used to stoke latest fcm registration token
     */
    private static final String REGISTRATION_TOKEN_PREF_KEY = "registrationToken";


    /**
     * key used to store application subscribed push topics
     */
    private static final String TOPICS_PREF_KEY = "topics";


    /**
     * key used to store application specific details about the device(installation)
     */
    private static final String EXTRAS_PREF_KEY = "extras";

    /**
     * key used to store application device information
     */
    private static final String INFO_PREF_KEY = "info";


    /**
     * class instance
     */
    private static Push instance = null;

    /**
     * server api endpoint to post and update device push details
     */
    private String apiBaseUrl = "";

    /**
     * server api authorization token
     */
    private String apiAuthorizationToken = "";


    /**
     * set of application subscribed push topics
     */
    private Set<String> topics = new HashSet<String>();

    /**
     * latest push registration token
     */
    private String registrationToken = "";

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
     * Private constructor
     *
     * @param context
     */
    private Push(Context context, String apiBaseUrl, String apiAuthorizationToken) {
        this.context = context;
        this.apiBaseUrl = apiBaseUrl;
        this.apiAuthorizationToken = apiAuthorizationToken;
    }


    /**
     * obtain current push instance
     *
     * @return {@link com.github.lykmapipo.push.Push}
     */
    public static synchronized Push getInstance() {
        //TODO initialize here...
        return instance;
    }


    /**
     * initialize new push instance
     *
     * @return {@link com.github.lykmapipo.push.Push}
     */
    public static synchronized void initialize(
            @NonNull Context context, @NonNull String apiBaseUrl, @NonNull String apiAuthorizationToken) {

        if (instance == null) {

            //initialize preference
            Preferences.initialize(context.getApplicationContext());

            //instantiate new push
            instance = new Push(context.getApplicationContext(), apiBaseUrl, apiAuthorizationToken);

            //initialize local burst
            LocalBurst localBurst =
                    LocalBurst.initialize(context.getApplicationContext());
            localBurst.on(instance, REGISTRATION_TOKEN_REFRESHED, PUSH_MESSAGE_RECEIVED, DEVICE_SYNCED);

            //initialize
            instance.init();
        }
    }

    /**
     * Unregister push listener
     *
     * @param listener
     */
    public static synchronized void $unregister(Object listener) {
        Push instance = Push.getInstance();
        if (instance != null) {
            instance.unregister(listener);
        }
    }

    /**
     * Register push listeners
     *
     * @param listener
     */
    public static synchronized void $register(Object listener) {
        Push instance = Push.getInstance();
        if (instance != null && instance.isGooglePlayServiceAvailable()) {
            instance.register(listener);
        }
    }

    /**
     * initialize push internal
     */
    private void init() {

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
            this.uuid = Utils.getUUID(context);
            return this.uuid;
        }
    }

    /**
     * register push listener(s)
     *
     * @param listener
     */
    public void register(Object listener) {

        //ensure listener
        boolean isValidListener =
                (listener != null && ((listener instanceof PushMessageListener) ||
                        (listener instanceof PushTokenListener) ||
                        (listener instanceof DeviceSyncListener)));

        if (isValidListener) {

            //register push message listener
            if (listener instanceof PushMessageListener) {
                if (this.pushMessageListeners == null) {
                    this.pushMessageListeners = new HashSet<PushMessageListener>();
                }
                this.pushMessageListeners.add((PushMessageListener) listener);
            }

            //register push token listener
            if (listener instanceof PushTokenListener) {
                if (this.pushTokenListeners == null) {
                    this.pushTokenListeners = new HashSet<PushTokenListener>();
                }
                this.pushTokenListeners.add((PushTokenListener) listener);
            }

            //register device sync listener
            if (listener instanceof DeviceSyncListener) {
                if (this.deviceSyncListeners == null) {
                    this.deviceSyncListeners = new HashSet<DeviceSyncListener>();
                }
                this.deviceSyncListeners.add((DeviceSyncListener) listener);
            }

        }
    }

    /**
     * unregister push listener(s)
     *
     * @param listener
     */
    public void unregister(Object listener) {

        //ensure listener
        boolean isValidListener =
                (listener != null && ((listener instanceof PushMessageListener) ||
                        (listener instanceof PushTokenListener) ||
                        (listener instanceof DeviceSyncListener)));

        if (isValidListener) {

            //register push message listener
            if (listener instanceof PushMessageListener) {
                if (this.pushMessageListeners == null) {
                    this.pushMessageListeners.remove((PushMessageListener) listener);
                }
            }

            //register push token listener
            if (listener instanceof PushTokenListener) {
                if (this.pushTokenListeners == null) {
                    this.pushTokenListeners.remove((PushTokenListener) listener);
                }
            }

            //register device sync listener
            if (listener instanceof DeviceSyncListener) {
                if (this.deviceSyncListeners == null) {
                    this.deviceSyncListeners.remove((DeviceSyncListener) listener);
                }
            }

        }
    }


    /**
     * Compute installation unique instance id
     *
     * @return instanceId
     */
    private String getInstanceId() {
        String instanceId = FirebaseInstanceId.getInstance().getId();
        return instanceId;
    }

    /**
     * merge device details
     *
     * @return {@link Device}
     */
    public Device merge(Device device) {

        //get current device
        Device _device = getDevice();

        //1...merge extras
        HashMap<String, String> extras = new HashMap<String, String>();

        //1.1...merge existing extras
        extras.putAll(_device.getExtras());

        //1.2...merge provided extras
        extras.putAll(device.getExtras());

        //1.3...update existing extras
        setExtras(extras);

        //2...merge info
        HashMap<String, String> info = new HashMap<String, String>();

        //2.1...merge existing info
        info.putAll(_device.getInfo());

        //2.2...merge provided info
        info.putAll(device.getInfo());

        //2.3...update existing info
        setInfo(info);

        //3...merge topics
        HashSet<String> topics = new HashSet<String>();
        topics.addAll(device.getTopics());
        for (String topic : topics) {
            subscribe(topic);
        }


        //return new device
        _device = getDevice();
        return _device;
    }


    /**
     * Subscribe to a given push topic
     *
     * @param topic
     * @see com.google.firebase.messaging.FirebaseMessaging
     */
    public Set<String> subscribe(@NonNull String topic) {
        try {

            Set<String> topics = getTopics();

            //add topic to application push topics
            if (!TextUtils.isEmpty(topic)) {
                topics.add(topic);
            }

            // persist application push topics
            Preferences.set(TOPICS_PREF_KEY, topics);

            //subscribe application to firebase push topic
            FirebaseMessaging.getInstance().subscribeToTopic(topic);

            return getTopics();

        } catch (Exception e) {
            return getTopics();
        }
    }


    /**
     * Unsubscribe from a given push topic
     *
     * @param topic
     * @see com.google.firebase.messaging.FirebaseMessaging
     */
    public Set<String> unsubscribe(@NonNull String topic) {
        try {

            Set<String> topics = getTopics();

            if (!TextUtils.isEmpty(topic)) {
                topics.remove(topic);
            }

            //persist application push topics
            Preferences.set(TOPICS_PREF_KEY, topics);

            //un subscribe application from firebase push topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);

            return getTopics();
        } catch (Exception e) {
            return getTopics();
        }
    }

    /**
     * obtain list of push topic an application subscribe on
     *
     * @return topics
     */
    public Set<String> getTopics() {
        Set<String> topics = Preferences.get(TOPICS_PREF_KEY, this.topics);
        return topics;
    }

    /**
     * set application device info details
     *
     * @param info
     * @return
     */
    private Map<String, String> setInfo(Map<String, String> info) {
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
     * obtain device(installation) info details
     *
     * @return topics
     */
    public Map<String, String> getInfo() {
        Map<String, String> info = setInfo(getDeviceInfo());
        return info;
    }

    /**
     * obtain device info
     *
     * @param key
     * @return
     */
    public String getInfo(String key) {
        Map<String, String> info = getInfo();
        String _info = info.get(key);
        return _info;
    }

    /**
     * obtain device info
     *
     * @param key
     * @return
     */
    public String getInfo(String key, String defaultValue) {
        Map<String, String> info = getInfo();
        String _info = info.get(key);
        if (_info == null) {
            _info = defaultValue;
        }
        return _info;
    }


    /**
     * set application extra details on a push device
     *
     * @param extras
     * @return
     */
    private Map<String, String> setExtras(Map<String, String> extras) {
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
     * add application specific extra details
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, String> putExtra(String key, Float value) {
        try {
            String _value = String.valueOf(value);
            return putExtra(key, _value);
        } catch (Exception e) {
            return getExtras();
        }
    }


    /**
     * add application specific extra details
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, String> putExtra(String key, Long value) {
        try {
            String _value = String.valueOf(value);
            return putExtra(key, _value);
        } catch (Exception e) {
            return getExtras();
        }
    }


    /**
     * add application specific extra details
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, String> putExtra(String key, Integer value) {
        try {
            String _value = String.valueOf(value);
            return putExtra(key, _value);
        } catch (Exception e) {
            return getExtras();
        }
    }


    /**
     * add application specific extra details
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, String> putExtra(String key, Boolean value) {
        try {
            String _value = String.valueOf(value);
            return putExtra(key, _value);
        } catch (Exception e) {
            return getExtras();
        }
    }


    /**
     * obtain device(installation) extra details
     *
     * @return extras
     */
    public Map<String, String> getExtras() {
        Map<String, String> extras = getMapOfPreferences(EXTRAS_PREF_KEY);
        return extras;
    }


    /**
     * obtain device(installation) extra detail
     *
     * @param key
     * @return
     */
    public String getExtra(String key) {
        Map<String, String> extras = getExtras();
        return extras.get(key);
    }


    /**
     * obtain device(installation) extra detail
     *
     * @param key
     * @return
     */
    public String getExtra(String key, String defaultValue) {
        Map<String, String> extras = getExtras();
        String extra = extras.get(key);
        if (extra == null) {
            extra = defaultValue;
        }
        return extra;
    }


    /**
     * obtain device(installation) extra detail
     *
     * @param key
     * @return
     */
    public Float getExtra(String key, Float defaultValue) {
        try {
            Map<String, String> extras = getExtras();
            String extra = extras.get(key);
            Float _extra = Float.valueOf(extra);
            return _extra;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * obtain device(installation) extra detail
     *
     * @param key
     * @return
     */
    public Long getExtra(String key, Long defaultValue) {
        try {
            Map<String, String> extras = getExtras();
            String extra = extras.get(key);
            Long _extra = Long.valueOf(extra);
            return _extra;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * obtain device(installation) extra detail
     *
     * @param key
     * @return
     */
    public Integer getExtra(String key, Integer defaultValue) {
        try {
            Map<String, String> extras = getExtras();
            String extra = extras.get(key);
            Integer _extra = Integer.valueOf(extra);
            return _extra;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * obtain device(installation) extra detail
     *
     * @param key
     * @return
     */
    public Boolean getExtra(String key, Boolean defaultValue) {
        try {
            Map<String, String> extras = getExtras();
            String extra = extras.get(key);
            Boolean _extra = Boolean.valueOf(extra);
            return _extra;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Save device push registration token in shared preferences
     *
     * @param token
     * @return registrationToken
     */
    public String setRegistrationToken(@NonNull String token) {

        //persist push registration token
        Preferences.set(REGISTRATION_TOKEN_PREF_KEY, token);

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
        String token = Preferences.get(REGISTRATION_TOKEN_PREF_KEY, registrationToken);
        return token;
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
            String authorization = "Bearer " + this.apiAuthorizationToken;

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
            String authorization = "Bearer " + this.apiAuthorizationToken;

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
        boolean isGooglePlayServiceAvailable = false;
        try {
            GoogleApiAvailability api = GoogleApiAvailability.getInstance();
            int status = api.isGooglePlayServicesAvailable(this.context);

            if (status == ConnectionResult.SUCCESS) {
                isGooglePlayServiceAvailable = true;
            } else {
                isGooglePlayServiceAvailable = false;
            }
        } catch (Exception e) {
            isGooglePlayServiceAvailable = false;
        }

        return isGooglePlayServiceAvailable;
    }

    /**
     * sync device details to backend api
     *
     * @return
     */
    public void sync() {
        Map<String, String> extras = new HashMap<String, String>();
        sync(extras);
    }

    /**
     * sync device details to backend api
     *
     * @param extraKey
     * @param extraValue
     * @return
     */
    public void sync(String extraKey, String extraValue) {

        Map<String, String> extras = new HashMap<String, String>();

        if ((extraKey != null && !extraKey.isEmpty()) && (extraValue != null && !extraValue.isEmpty())) {
            extras.put(extraKey, extraValue);
        }

        sync(extras);
    }

    /**
     * sync device details to backend api
     *
     * @param extras
     * @return
     */
    private void sync(Map<String, String> extras) {

        //update device extras
        Map<String, String> _extras = setExtras(extras);

        //start sync service
        Intent intent = new Intent(context.getApplicationContext(), DeviceSyncService.class);
        intent.putExtra(FORCE_DEVICE_SYNC, true);
        intent.setAction(DEVICE_SYNCED);
        context.startService(intent);

    }


    /**
     * Obtain map of shared preferences by provide key
     *
     * @param key
     * @return
     */
    private Map<String, String> getMapOfPreferences(String key) {

        HashMap<String, String> preferences = new HashMap<String, String>();

        Set<String> preferenceSet = Preferences.get(key, new HashSet<String>());

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
            Set<String> dataSet = new HashSet<String>();
            for (String _key : _data.keySet()) {
                String value = _key + ":" + _data.get(_key);
                dataSet.add(value);
            }

            Preferences.set(key, dataSet);

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

        //merge build info
        deviceInfo.putAll(Utils.getBuildInfo());

        //merge locale info
        deviceInfo.putAll(Utils.getLocaleInfo(context));

        //merge display info
        deviceInfo.putAll(Utils.getDisplayInfo(context));

        //merge package info
        deviceInfo.putAll(Utils.getPackageInfo(context));

        //merge storage information
        deviceInfo.putAll(Utils.getMemoryInfo(context));

        return deviceInfo;

    }


    /**
     * clear push shared preferences
     */
    public void clear() {
        //clear all push preferences
        Preferences.remove(
                REGISTRATION_TOKEN_PREF_KEY, TOPICS_PREF_KEY,
                EXTRAS_PREF_KEY, INFO_PREF_KEY
        );
    }


    @Override
    public void onBroadcast(String action, Bundle extras) {

        //handle refreshed token
        if (action.equals(REGISTRATION_TOKEN_REFRESHED)) {
            //check for success synced registration token
            boolean isSuccess = extras.getBoolean(Push.SUCCESS, false);

            //notify push token listener
            if (pushTokenListeners != null && !pushTokenListeners.isEmpty()) {
                for (PushTokenListener pushTokenListener : pushTokenListeners) {
                    //notify success token refresh listener
                    if (isSuccess) {
                        pushTokenListener.onRegistrationTokenRefreshed(getDevice());
                    }
                    //notify token refresh error listener
                    else {
                        String errorMessage = extras.getString(Push.MESSAGE);
                        pushTokenListener.onRegistrationTokenError(errorMessage);
                    }
                }
            }
        }

        //handle push message received
        if (action.equals(PUSH_MESSAGE_RECEIVED)) {
            //obtain remote message
            RemoteMessage message = (RemoteMessage) extras.getParcelable(Push.MESSAGE);

            if ((pushMessageListeners != null) && !pushMessageListeners.isEmpty()) {
                for (PushMessageListener pushMessageListener : pushMessageListeners) {
                    //notify push message listener
                    pushMessageListener.onMessage(message);
                }
            }
        }

        //handle device synced
        if (action.equals(DEVICE_SYNCED)) {
            //check for success synced device
            boolean isSuccess = extras.getBoolean(Push.SUCCESS, false);

            //notify device sync listener
            if (deviceSyncListeners != null && !deviceSyncListeners.isEmpty()) {
                for (DeviceSyncListener deviceSyncListener : deviceSyncListeners) {

                    //notify success device sync listener
                    if (isSuccess) {
                        deviceSyncListener.onDeviceSynced(getDevice());
                    }

                    //notify device sync error listener
                    else {
                        String errorMessage = extras.getString(Push.MESSAGE);
                        deviceSyncListener.onDeviceSyncError(errorMessage);
                    }

                }
            }
        }
    }
}