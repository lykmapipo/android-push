package com.github.lykmapipo.push;

import android.content.Context;

import com.github.lykmapipo.push.api.Device;
import com.github.lykmapipo.push.api.DeviceApi;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 */

@Config(sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class PushTest {
    private Context context;
    private String registrationToken = "XtSVEtyw023iTy";
    private String apiAuthorizationToken = "TtSVEtyw023iTy";
    private String apiBaseUrl = "https://www.example.com/";

    @Before
    public void setup() {
        context = ShadowApplication.getInstance().getApplicationContext();
        Push.initialize(context, apiBaseUrl, apiAuthorizationToken);
        FirebaseApp.initializeApp(context);
    }


    @Test
    public void shouldBeAbleToGetPushInstance() {

        Push push = Push.getInstance();

        assertThat(push, is(not(equalTo(null))));
        assertThat(push.getApiBaseUrl(), is(not(equalTo(null))));
        assertThat(push.getApiAuthorizationToken(), is(not(equalTo(null))));
        assertThat(push.getDeviceApi(), is(not(equalTo(null))));
    }

    @Test
    public void shouldBeAbleToGetDeviceUUID() {
        Push push = Push.getInstance();

        String uuid1 = push.getUUID();
        String uuid2 = push.getUUID();

        assertThat(uuid1, is(not(equalTo(null))));
        assertThat(uuid2, is(not(equalTo(null))));
        assertThat(uuid1, is(equalTo(uuid2)));
    }

    @Test
    public void shouldBeAbleToInitializeDeviceApi() {

        Push push = Push.getInstance();

        DeviceApi deviceApi = push.getDeviceApi();
        assertThat(deviceApi, is(not(equalTo(null))));
    }


    @Test
    public void shouldBeAbleToSetApiBaseUrl() {

        Push push = Push.getInstance();
        String apiBaseUrl = push.setApiBaseUrl(this.apiBaseUrl);

        assertThat(apiBaseUrl, equalTo(this.apiBaseUrl));
    }

    @Test
    public void shouldBeAbleToGetApiBaseUrl() {

        Push push = Push.getInstance();
        push.setApiBaseUrl(this.apiBaseUrl);

        String apiBaseUrl = push.getApiBaseUrl();

        assertThat(apiBaseUrl, equalTo(this.apiBaseUrl));
    }

    @Test
    public void shouldBeAbleToSetApiAuthorizationToken() {

        Push push = Push.getInstance();
        String authorizationToken = push.setApiAuthorizationToken(this.apiAuthorizationToken);

        assertThat(authorizationToken, equalTo(this.apiAuthorizationToken));
    }

    @Test
    public void shouldBeAbleToGetApiAuthorizationToken() {

        Push push = Push.getInstance();
        push.setApiAuthorizationToken(this.apiAuthorizationToken);

        String apiAuthorizationToken = push.getApiAuthorizationToken();

        assertThat(apiAuthorizationToken, equalTo(this.apiAuthorizationToken));
    }

    @Test
    public void shouldBeAbleToGetInstanceId() {
    }


    @Test
    public void shouldBeAbleToSubscribeToPushTopic() {
        String topic = "MAIZE";
        Set<String> topics = Push.getInstance().subscribe(topic);
        assertThat(topics.contains(topic), equalTo(true));
    }

    @Test
    public void shouldBeAbleToUnSubscribeToPushTopic() {
        String topic = "MAIZE";

        Push push = Push.getInstance();
        push.subscribe(topic);

        Set<String> topics = push.unsubscribe(topic);
        assertThat(topics.contains(topic), equalTo(false));
    }

    @Test
    public void shouldBeAbleToObtainSubscribedPushTopics() {
        String topic = "MAIZE";

        Push push = Push.getInstance();
        push.subscribe(topic);

        Set<String> topics = push.getTopics();
        assertThat(topics.contains(topic), equalTo(true));
    }

    @Test
    public void shouldBeAbleToSaveDeviceRegistrationToken() {
        Push push = Push.getInstance();
        String token = push.setRegistrationToken(registrationToken);
        assertThat(token, equalTo(registrationToken));
    }

    @Test
    public void shouldBeAbleToGetDeviceRegistrationToken() {
        Push push = Push.getInstance();
        push.setRegistrationToken(registrationToken);

        String token = push.getRegistrationToken();
        assertThat(token, equalTo(registrationToken));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceHasInternetConnection() {
        Push push = Push.getInstance();

        boolean connected = push.isConnected();

        assertThat(connected, equalTo(true));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceHasWifiEnabled() {
        Push push = Push.getInstance();

        boolean wifiEnabled = push.isWifiEnabled();

        assertThat(wifiEnabled, equalTo(true));
    }

    @Test
    public void shouldBeAbleToSaveDeviceExtra() {
        Push push = Push.getInstance();
        Map<String, String> extras = push.putExtra("phone", "11111111111");
        assertThat(extras.get("phone"), equalTo("11111111111"));
    }

    @Test
    public void shouldBeAbleToGetDeviceExtras() {
        Push push = Push.getInstance();
        push.putExtra("phone", "11111111111");
        Map<String, String> extras = push.getExtras();
        assertThat(extras.get("phone"), equalTo("11111111111"));
    }

    @Test
    public void shouldBeAbleToGetExistingDeviceInfo() {
        Push push = Push.getInstance();
        Map<String, String> info = push.getInfo();
        assertThat(info, notNullValue());
        assertThat(info.size(), equalTo(34));

        //assert info
        assertThat(info.get(Device.AVAILABLE_EXTERNAL_MEMORY_SIZE), notNullValue());
        assertThat(info.get(Device.AVAILABLE_INTERNAL_MEMORY_SIZE), notNullValue());
        assertThat(info.get(Device.BOOTLOADER), notNullValue());
        assertThat(info.get(Device.BOARD), notNullValue());
        assertThat(info.get(Device.BRAND), notNullValue());
        assertThat(info.get(Device.COUNTRY_CODE), notNullValue());
        assertThat(info.get(Device.COUNTRY_NAME), notNullValue());
        assertThat(info.get(Device.DEVICE), notNullValue());
        assertThat(info.get(Device.DISPLAY), notNullValue());
        assertThat(info.get(Device.FINGERPRINT), notNullValue());
        assertThat(info.get(Device.HAS_EXTERNAL_MEMORY), notNullValue());
        assertThat(info.get(Device.HARDWARE), notNullValue());
        assertThat(info.get(Device.LANGUAGE_CODE), notNullValue());
        assertThat(info.get(Device.LANGUAGE_NAME), notNullValue());
        assertThat(info.get(Device.MANUFACTURER), notNullValue());
        assertThat(info.get(Device.MODEL), notNullValue());
        assertThat(info.get(Device.PACKAGE), notNullValue());
        assertThat(info.get(Device.PRODUCT), notNullValue());
        assertThat(info.get(Device.RADIO_VERSION), notNullValue());
        assertThat(info.get(Device.RELEASE), notNullValue());
        assertThat(info.get(Device.SCREEN_DENSITY), notNullValue());
        assertThat(info.get(Device.SCREEN_HEIGHT), notNullValue());
        assertThat(info.get(Device.SCREEN_WIDTH), notNullValue());
        assertThat(info.get(Device.SDK), notNullValue());
        assertThat(info.get(Device.SERIAL), notNullValue());
        assertThat(info.get(Device.TIMEZONE), notNullValue());
        assertThat(info.get(Device.TOTAL_EXTERNAL_MEMORY_SIZE), notNullValue());
        assertThat(info.get(Device.TOTAL_INTERNAL_MEMORY_SIZE), notNullValue());
        assertThat(info.get(Device.TAGS), notNullValue());
        assertThat(info.get(Device.TOTAL_RAM), notNullValue());
        assertThat(info.get(Device.TYPE), notNullValue());
        assertThat(info.get(Device.USER), notNullValue());
        assertThat(info.get(Device.VERSION_CODE), notNullValue());
        assertThat(info.get(Device.VERSION_NAME), notNullValue());
    }

    @Test
    public void shouldBeAbleToSaveDeviceInfo() {
        Push push = Push.getInstance();
        Map<String, String> info = push.putInfo("phone", "11111111111");
        assertThat(info.get("phone"), equalTo("11111111111"));
    }

    @Test
    public void shouldBeAbleToGetDeviceInfo() {
        Push push = Push.getInstance();
        push.putInfo("phone", "11111111111");
        Map<String, String> info = push.getInfo();
        assertThat(info.get("phone"), equalTo("11111111111"));
    }

    @After
    public void cleanup() {
        Push.getInstance().clear();
        context = null;
    }
}
