package com.github.byteskode.push;


import android.content.Context;


/**
 * Singleton to manage push notifitication
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
     * @return
     */
    public static Push getInstance(Context context) {
        if (instance == null) {
            instance = new Push(context);
        }
        return instance;
    }

    public void getInstanceId() {
//        new GoogleApiClient();
//        String iid = InstanceID.getInstance(this.mContext).getId();
    }

}