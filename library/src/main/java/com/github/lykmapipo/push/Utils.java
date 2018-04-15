package com.github.lykmapipo.push;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.github.lykmapipo.push.api.Device;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * utils
 *
 * @author lally elias
 * @email lallyelias87@gmail.com
 */
public class Utils {

    //constants
    public static final String NETWORK_TYPE_WIFI = "WiFi";
    public static final String NETWORK_TYPE_MOBILE = "Mobile";


    //memory

    /**
     * Derive device memory information
     *
     * @return
     */
    public static synchronized Map<String, String> getMemoryInfo(Context context) {
        Map<String, String> memoryInfo = new HashMap<String, String>();

        memoryInfo.put(Device.AVAILABLE_EXTERNAL_MEMORY_SIZE,
                String.valueOf(getAvailableExternalMemorySize()));

        memoryInfo.put(Device.AVAILABLE_INTERNAL_MEMORY_SIZE,
                String.valueOf(getAvailableInternalMemorySize()));

        memoryInfo.put(Device.HAS_EXTERNAL_MEMORY, String.valueOf(hasExternalMemory()));

        memoryInfo.put(Device.TOTAL_EXTERNAL_MEMORY_SIZE,
                String.valueOf(getTotalExternalMemorySize()));

        memoryInfo.put(Device.TOTAL_INTERNAL_MEMORY_SIZE,
                String.valueOf(getTotalInternalMemorySize()));

        memoryInfo.put(Device.TOTAL_RAM,
                String.valueOf(getTotalRAM(context)));

        return memoryInfo;
    }


    /**
     * Check if device has external memory(SD Card)
     *
     * @return
     */
    public static synchronized boolean hasExternalMemory() {
        boolean hasExternalMemory = false;
        try {
            hasExternalMemory =
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            hasExternalMemory = false;
        }
        return hasExternalMemory;
    }

    /**
     * Derive device total external memory size
     *
     * @return
     */
    public static synchronized long getTotalExternalMemorySize() {
        long totalExternalMemorySize = 0;
        try {
            boolean hasExternalMemory = hasExternalMemory();
            if (hasExternalMemory) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize;
                long totalBlocks;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    blockSize = stat.getBlockSizeLong();
                    totalBlocks = stat.getBlockCountLong();
                } else {
                    blockSize = stat.getBlockSize();
                    totalBlocks = stat.getBlockCount();
                }
                totalExternalMemorySize = (totalBlocks * blockSize);
            }
        } catch (Exception e) {
            totalExternalMemorySize = 0;
        }
        return totalExternalMemorySize;
    }

    /**
     * Derive device available external memory size
     *
     * @return
     */
    public static synchronized long getAvailableExternalMemorySize() {
        long availableExternalMemorySize = 0;
        try {
            boolean hasExternalMemory = hasExternalMemory();
            if (hasExternalMemory) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize;
                long availableBlocks;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    blockSize = stat.getBlockSizeLong();
                    availableBlocks = stat.getAvailableBlocksLong();
                } else {
                    blockSize = stat.getBlockSize();
                    availableBlocks = stat.getAvailableBlocks();
                }
                availableExternalMemorySize = (availableBlocks * blockSize);
            }
        } catch (Exception e) {
            availableExternalMemorySize = 0;
        }
        return availableExternalMemorySize;
    }

    /**
     * Derive derive total internal memory size
     *
     * @return
     */
    public static synchronized long getTotalInternalMemorySize() {
        long totalInternalMemorySize = 0;
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize;
            long totalBlocks;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
            }
            totalInternalMemorySize = (totalBlocks * blockSize);
        } catch (Exception e) {
            totalInternalMemorySize = 0;
        }
        return totalInternalMemorySize;
    }

    /**
     * Derive device available internal memory size
     *
     * @return
     */
    public static synchronized long getAvailableInternalMemorySize() {
        long availableInternalMemorySize = 0;
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize, availableBlocks;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
            availableInternalMemorySize = (availableBlocks * blockSize);
        } catch (Exception e) {
            availableInternalMemorySize = 0;
        }
        return availableInternalMemorySize;
    }


    /**
     * Derive device total RAM
     *
     * @param context
     * @return
     */
    public static synchronized long getTotalRAM(Context context) {
        long totalRAM = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                ActivityManager activityManager =
                        (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
                activityManager.getMemoryInfo(mi);
                return mi.totalMem;
            } else {
                RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
                String load = reader.readLine().replaceAll("\\D+", "");
                totalRAM = (long) Integer.parseInt(load);
                reader.close();
            }
        } catch (IOException e) {
            totalRAM = 0;
        }
        return totalRAM;
    }


    //network

    /**
     * Checks if there is Internet connection or data connection on the device.
     *
     * @return boolean
     */
    public static synchronized boolean isConnected(Context context) {

        boolean isConnected = false;

        try {
            ConnectivityManager connectivity =
                    (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity != null) {
                NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
                isConnected = networkInfo.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            isConnected = false;
        }

        return isConnected;

    }


    /**
     * Check if device Wi-Fi is enabled or disabled.
     *
     * @param context
     * @return
     */
    public static synchronized boolean isWifiEnabled(Context context) {
        boolean isWifiEnabled = false;
        try {
            WifiManager wifiManager =
                    (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                isWifiEnabled = wifiManager.isWifiEnabled();
            }
        } catch (Exception e) {
            isWifiEnabled = false;
        }
        return isWifiEnabled;
    }

    /**
     * Derive current device connected network type
     *
     * @param context
     * @return
     */
    public static synchronized String getNetworkType(Context context) {
        String networkType = "";
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    int type = activeNetwork.getType();

                    boolean isWifi = (type == ConnectivityManager.TYPE_WIFI
                            || type == ConnectivityManager.TYPE_WIMAX);

                    boolean isMobile = (type == ConnectivityManager.TYPE_MOBILE);

                    if (isWifi) {
                        networkType = NETWORK_TYPE_WIFI;
                    }

                    if (isMobile) {
                        networkType = NETWORK_TYPE_MOBILE;
                    }
                }
            }
        } catch (Exception e) {
            networkType = "";
        }
        return networkType;
    }


    //display

    /**
     * Derive device information information
     *
     * @return
     */
    public static synchronized Map<String, String> getDisplayInfo(Context context) {
        Map<String, String> displayInfo = new HashMap<String, String>();

        /** @see DisplayMetrics */
        displayInfo.put(Device.SCREEN_DENSITY, asEmpty(getScreenDensity(context)));

        /** @see WindowManager */
        displayInfo.put(Device.SCREEN_HEIGHT, asEmpty(String.valueOf(getScreenHeight(context))));

        /** @see WindowManager */
        displayInfo.put(Device.SCREEN_WIDTH, asEmpty(String.valueOf(getScreenWidth(context))));

        return displayInfo;
    }

    /**
     * Derive screen density
     *
     * @return
     */
    public static synchronized String getScreenDensity(Context context) {
        String screenType = "";
        try {
            int density =
                    context.getApplicationContext().getResources().getDisplayMetrics().densityDpi;
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
    public static synchronized int getScreenHeight(Context context) {
        int height = 0;
        try {
            WindowManager wm =
                    (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
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
    public static synchronized int getScreenWidth(Context context) {
        int width = 0;
        try {
            WindowManager wm =
                    (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
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


    //locale

    public static synchronized String asEmpty(String value) {
        if (value == null) {
            value = "";
        }
        return value;
    }

    /**
     * Derive device country code
     *
     * @return
     */
    public static synchronized String getCountryCode(Context context) {
        String countryCode = "";
        try {
            Locale locale =
                    context.getApplicationContext().getResources().getConfiguration().locale;
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
    public static synchronized String getCountryName(Context context) {
        String countryName = "";
        try {
            Locale locale =
                    context.getApplicationContext().getResources().getConfiguration().locale;
            countryName = locale.getDisplayCountry();
        } catch (Exception e) {
            countryName = "";
        }

        return countryName;

    }

    /**
     * Derive device information information
     *
     * @return
     */
    public static synchronized Map<String, String> getLocaleInfo(Context context) {
        Map<String, String> localeInfo = new HashMap<String, String>();

        /** @see Locale */
        localeInfo.put(Device.COUNTRY_CODE, asEmpty(getCountryCode(context)));

        /** @see Locale */
        localeInfo.put(Device.COUNTRY_NAME, asEmpty(getCountryName(context)));

        /** @see Locale */
        localeInfo.put(Device.LANGUAGE_CODE, asEmpty(getLanguageCode(context)));

        /** @see Locale */
        localeInfo.put(Device.LANGUAGE_NAME, asEmpty(getLanguageName(context)));

        /** @see TimeZone */
        localeInfo.put(Device.TIMEZONE, asEmpty(getTimezone()));

        return localeInfo;
    }

    /**
     * Derive device language code
     *
     * @return
     */
    public static synchronized String getLanguageCode(Context context) {
        String languageCode = "";
        try {
            Locale locale =
                    context.getApplicationContext().getResources().getConfiguration().locale;
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
    public static synchronized String getLanguageName(Context context) {
        String languageName = "";
        try {
            Locale locale =
                    context.getApplicationContext().getResources().getConfiguration().locale;
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
    public static synchronized String getTimezone() {
        String timezone = "";
        try {
            TimeZone timeZone = TimeZone.getDefault();
            timezone = timeZone.getID();
        } catch (Exception e) {
            timezone = "";
        }

        return timezone;
    }


    //package

    /**
     * Derive application version name
     *
     * @return
     */
    public static synchronized String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo packageInfo;
            packageInfo =
                    context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
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
    public static synchronized String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageInfo packageInfo;
            packageInfo =
                    context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packageInfo.versionCode);
        } catch (Exception e1) {
            versionCode = null;
        }
        return versionCode;
    }

    /**
     * Derive device information information
     *
     * @return
     */
    public static synchronized Map<String, String> getPackageInfo(Context context) {
        Map<String, String> packageInfo = new HashMap<String, String>();

        /** @see PackageInfo */
        packageInfo.put(Device.VERSION_CODE, asEmpty(String.valueOf(getVersionCode(context))));

        /** @see PackageInfo */
        packageInfo.put(Device.VERSION_NAME, asEmpty(String.valueOf(getVersionName(context))));

        return packageInfo;
    }
}
