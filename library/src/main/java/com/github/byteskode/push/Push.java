package com.github.byteskode.push;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.github.byteskode.push.api.DeviceApi;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import retrofit2.Retrofit;

import java.util.HashSet;
import java.util.Set;


/**
 * simplified push notification for android application
 *
 * @author lally elias(lykmapipo), byteskode Team & Contibutors
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 */
public class Push {
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
     * key used to store api server end point to post and update device push details
     */
    public static final String API_BASE_URL_PREF_KEY = "apiBaseUrl";

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
     * latest push registration token
     */
    private String registrationToken;

    /**
     * server api endpoint to post and update device push details
     */
    private String apiBaseUrl;

    /**
     * device api used to sync device push details to remove server
     */
    private DeviceApi deviceApi;

    /**
     * holding context
     */
    private Context context;

    /**
     * Private constructor
     *
     * @param context
     */
    private Push(Context context) {
        this.context = context;
    }


    /**
     * Singleton instance method
     *
     * @param context
     * @return {@link com.github.byteskode.push.Push}
     */
    public static synchronized Push getInstance(Context context) {
        if (instance == null) {

            //instantiate push
            instance = new Push(context);

            //initialize push
            instance.init();
        }

        return instance;

    }


    /**
     * initialize push internal
     */
    private void init() {

        //obtain preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        //load existing topics
        this.topics = preferences.getStringSet(TOPICS_PREF_KEY, this.topics);

    }

    /**
     * initialize device api server
     */
    private void initDeviceApi() {
        if (deviceApi == null && apiBaseUrl != null && !apiBaseUrl.isEmpty()) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(this.apiBaseUrl).build();
            deviceApi = retrofit.create(DeviceApi.class);
        }
    }

    public DeviceApi getDeviceApi(){
        //TODO ensure it initialized
        return deviceApi;
    }

    /**
     * save server api endpoint to post and update device push details
     *
     * @param apiUrl
     * @return
     */
    public String setApiUrl(String apiUrl) {
        //save api url to shared preference
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_BASE_URL_PREF_KEY, apiUrl);
        editor.apply();

        //update in memory apiBaseUrl
        this.apiBaseUrl = apiUrl;

        //init device api
//        initDeviceApi();

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
            if (topic != null && !topic.isEmpty()) {
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
            if (topic != null & !topic.isEmpty()) {
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
     * Send push message
     *
     * @param message
     * @see {@link com.google.firebase.messaging.RemoteMessage}
     * @see {@link com.google.firebase.messaging.FirebaseMessaging}
     */
    public void send(RemoteMessage message) {
        //TODO implement sent and error callback
        FirebaseMessaging.getInstance().send(message);
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
     * Save device push registration token
     *
     * @param token
     * @return registrationToken
     */
    public String saveRegistrationToken(String token) {
        //persist push registration token
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(REGISTRATION_TOKEN_PREF_KEY, token);
        editor.apply();

        //set instance latest registration token
        registrationToken = token;

        return registrationToken;
    }

    /**
     * obtain current application push registration toke
     *
     * @return registrationToken
     */
    public String getRegistrationToken() {
        String token = preferences.getString(REGISTRATION_TOKEN_PREF_KEY, registrationToken);
        return token;
    }

    public void onPushNotification(RemoteMessage message) {
        //TODO implement message handler
    }

    public void clear() {
        //clear in memory topics
        topics.clear();

        //clear all push preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(REGISTRATION_TOKEN_PREF_KEY);
        editor.remove(INSTANCE_ID_PREF_KEY);
        editor.remove(TOPICS_PREF_KEY);
        editor.remove(API_BASE_URL_PREF_KEY);
        editor.apply();

    }

}