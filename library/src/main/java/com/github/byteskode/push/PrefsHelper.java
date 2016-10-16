package com.github.byteskode.push;

import android.content.Context;
import android.provider.Settings.Secure;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;


/**
 * {@link android.content.SharedPreferences} Helper
 * 
 * Encapsulate behaviour based on {@link android.content.SharedPreferences}
 * 
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 */
public class PrefsHelper {
    public static final String FCM_TOKEN = "fcm_token";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    /**
     * non instantiable class.
     */
    private PrefsHelper() {

    }


    public static String getInstallationId(Context context){
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }


    /**
     * Save the FCM token in {@link android.content.SharedPreferences}.
     *
     * @param context  holding context
     * @param fcmToken token to be saved
     */
    public static void saveFCMToken(Context context, String fcmToken) {
        SharedPreferences pref = 
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        pref.edit().putString(FCM_TOKEN, fcmToken).apply();
    }


    /**
     * Retrieve FCM Token from {@link android.content.SharedPreferences}.
     *
     * @param context holding context
     * @return string represents FCM Token
     */
    public static String getFCMToken(Context context) {
        SharedPreferences pref = 
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return pref.getString(FCM_TOKEN, "");
    }


    /**
     * Remove FCM Token from {@link android.content.SharedPreferences}.
     *
     * @param context holding context
     */
    public static void removeFCMToken(Context context){
        SharedPreferences pref = 
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        pref.edit().remove(FCM_TOKEN).apply();
    }


    /**
     * Check if token is null
     *
     * @param context holding context
     * @param sent    true if token is not null, false otherwise
     */
    public static void sendFCMTokenToServer(Context context, boolean sent) {
        SharedPreferences pref = 
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        pref.edit().putBoolean(SENT_TOKEN_TO_SERVER, sent).apply();
    }


    /**
     * Check if installation has FCM token
     *
     * @param context holding context
     * @return true if token is not null, false otherwise
     */
    public static boolean hasFCMToken(Context context) {
        SharedPreferences pref = 
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return pref.getBoolean(SENT_TOKEN_TO_SERVER, false);
    }
}
