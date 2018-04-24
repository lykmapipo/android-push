package com.github.lykmapipo.push.api;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * device push details to be synced(posted & updated) to a remove server
 *
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/17/16
 */
public class Device implements Serializable {
    public static final String AVAILABLE_EXTERNAL_MEMORY_SIZE = "availableExternalMemorySize";
    public static final String AVAILABLE_INTERNAL_MEMORY_SIZE = "availableInternalMemorySize";
    public static final String BOOTLOADER = "bootloader";
    public static final String BOARD = "board";
    public static final String BRAND = "brand";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String COUNTRY_NAME = "countryName";
    public static final String DEVICE = "device";
    public static final String DISPLAY = "display";
    public static final String FINGERPRINT = "fingerprint";
    public static final String HARDWARE = "hardware";
    public static final String HAS_EXTERNAL_MEMORY = "hasExternalMemory";
    public static final String LANGUAGE_CODE = "languageCode";
    public static final String LANGUAGE_NAME = "languageName";
    public static final String MANUFACTURER = "manufacturer";
    public static final String MODEL = "model";
    public static final String PACKAGE = "package";
    public static final String PRODUCT = "product";
    public static final String RADIO_VERSION = "radioVersion";
    public static final String RELEASE = "release";
    public static final String SCREEN_DENSITY = "screenDensity";
    public static final String SCREEN_HEIGHT = "screenHeight";
    public static final String SCREEN_WIDTH = "screenWidth";
    public static final String SDK = "sdk";
    public static final String SERIAL = "serial";
    public static final String TIMEZONE = "timezone";
    public static final String TOTAL_EXTERNAL_MEMORY_SIZE = "totalExternalMemorySize";
    public static final String TOTAL_INTERNAL_MEMORY_SIZE = "totalInternalMemorySize";
    public static final String TAGS = "tags";
    public static final String TOTAL_RAM = "totalRAM";
    public static final String TYPE = "type";
    public static final String USER = "user";
    public static final String VERSION_CODE = "versionCode";
    public static final String VERSION_NAME = "versionName";

    @Expose
    @SerializedName("uuid")
    private String uuid;

    @Expose
    @SerializedName("instanceId")
    private String instanceId;

    @Expose
    @SerializedName("registrationToken")
    private String registrationToken;

    @Expose
    @SerializedName("topics")
    private Set<String> topics;

    @Expose
    @SerializedName("extras")
    private Map<String, String> extras;

    @Expose
    @SerializedName("info")
    private Map<String, String> info;

    public Device() {
    }

    public Device(String uuid, String instanceId, String registrationToken, Set<String> topics) {
        this.uuid = uuid;
        this.instanceId = instanceId;
        this.registrationToken = registrationToken;
        this.topics = topics;
    }

    public Device(String uuid, String instanceId, String registrationToken,
                  Set<String> topics, Map<String, String> extras) {
        this.uuid = uuid;
        this.instanceId = instanceId;
        this.registrationToken = registrationToken;
        this.topics = topics;
        this.extras = extras;
    }

    public Device(String uuid, String instanceId, String registrationToken,
                  Set<String> topics, Map<String, String> extras, Map<String, String> info) {
        this.uuid = uuid;
        this.instanceId = instanceId;
        this.registrationToken = registrationToken;
        this.topics = topics;
        this.extras = extras;
        this.info = info;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }
}
