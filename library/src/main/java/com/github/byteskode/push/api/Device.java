package com.github.byteskode.push.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Set;

/**
 * device push details to be synced(posted & updated) to a remove server
 *
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/17/16
 */
public class Device implements Serializable {

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

    public Device() {
    }

    public Device(String uuid, String instanceId, String registrationToken, Set<String> topics) {
        this.uuid = uuid;
        this.instanceId = instanceId;
        this.registrationToken = registrationToken;
        this.topics = topics;
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
}
