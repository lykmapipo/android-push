package com.github.byteskode.push;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import com.github.byteskode.push.api.Device;
import com.github.byteskode.push.api.DeviceApi;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


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
    private PushMessageListener pushMessageListener;


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
     * @return {@link com.github.byteskode.push.Push}
     */
    public static synchronized Push getInstance() {
        return instance;
    }


    /**
     * initialize new push instance
     *
     * @return {@link com.github.byteskode.push.Push}
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
            uuid.append(Build.DISPLAY).append(":"); //build ID string meant for displaying to the user
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
        this.pushMessageListener = listener;
    }

    /**
     * unregister push notification message listener
     */
    public void unregisterPushMessageListener() {
        this.pushMessageListener = null;
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
        FirebaseMessaging.getInstance().send(message);
    }


    public void onPushNotification(RemoteMessage message) {
        //TODO handle topic message and dispatch them to specific listener
        if (this.pushMessageListener != null) {
            pushMessageListener.onMessage(message);
        }
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
            Device device = new Device(this.getUUID(), this.getInstanceId(), registrationToken, this.getTopics());

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
            Device device = new Device(this.getUUID(), this.getInstanceId(), registrationToken, this.getTopics());

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

        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;

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
        editor.remove(API_BASE_URL_PREF_KEY);
        editor.remove(API_AUTHORIZATION_TOKEN_PREF_KEY);
        editor.apply();
    }

}