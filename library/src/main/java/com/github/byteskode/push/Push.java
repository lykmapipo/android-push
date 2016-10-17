package com.github.byteskode.push;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Singleton to manage push notification
 *
 * @author lally elias(lykmapipo), byteskode Team & Contibutors
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 */
public class Push {
    /**
     * key used to stoke latest fcm registration token
     */
    public static final String REGISTRATION_ID_PREF_KEY = "registrationToken";

    /**
     * key used to store application instance id
     */
    public static final String INSTANCE_ID_PREF_KEY = "instanceId";

    /**
     * class instance
     */
    private static Push instance = null;

    /**
     * shared preference instance
     */
    private static SharedPreferences preferences;

    /**
     * holding context
     */
    private Context mContext;

    /**
     * Private constructor
     *
     * @param mContext
     */
    private Push(Context mContext) {
        this.mContext = mContext;
//		init();
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

            //obtain preference manager
            preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        }

        return instance;

    }

    /**
     * Initialize push
     *
     * @param context
     * @return {@link com.github.byteskode.push.Push}
     */
    public static synchronized Push init(Context context) {
        return getInstance(context);
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
    public void subscribe(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    /**
     * Unsubscribe from a given push topic
     *
     * @param topic
     * @see {@link com.google.firebase.messaging.FirebaseMessaging}
     */
    public void unsubscribe(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    /**
     * Send push message
     *
     * @param message
     * @see {@link com.google.firebase.messaging.RemoteMessage}
     * @see {@link com.google.firebase.messaging.FirebaseMessaging}
     */
    public void send(RemoteMessage message) {
        FirebaseMessaging.getInstance().send(message);
    }

}