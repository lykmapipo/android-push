package com.github.lykmapipo.push;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.github.lykmapipo.push.api.Device;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author lally elias <lallyelias87@gmail.com>
 */

@RunWith(RobolectricTestRunner.class)
public class PushTest {
    private Context context;
    private String registrationToken = "XtSVEtyw023iTy";
    private String apiAuthorizationToken = "TtSVEtyw023iTy";
    private String apiBaseUrl = "https://www.example.com/";

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        Push.initialize(context, apiBaseUrl, apiAuthorizationToken);
        FirebaseApp.initializeApp(context);
    }


    @Test
    public void shouldBeAbleToGetPushInstance() {

        Push push = Push.getInstance();

        assertThat(push, is(not(equalTo(null))));
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

        assertThat(connected, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceHasWifiEnabled() {
        Push push = Push.getInstance();

        boolean wifiEnabled = push.isWifiEnabled();

        assertThat(wifiEnabled, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckDeviceNetworkType() {
        Push push = Push.getInstance();

        String networkType = push.getNetworkType();

        assertThat(networkType, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceIsConnectedOnWifi() {
        Push push = Push.getInstance();

        boolean connectedOnWiFi = push.isConnectedOnWiFi();

        assertThat(connectedOnWiFi, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceIsConnectedOnMobile() {
        Push push = Push.getInstance();

        boolean connectedOnMobile = push.isConnectedOnMobile();

        assertThat(connectedOnMobile, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceHasGooglePlayServiceAvailable() {
        Push push = Push.getInstance();

        boolean googlePlayServiceAvailable = push.isGooglePlayServiceAvailable();

        assertThat(googlePlayServiceAvailable, is(notNullValue()));
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
    public void shouldBeAbleToGetDeviceExtra_Single() {
        Push push = Push.getInstance();
        push.putExtra("phone", "11111111111");
        String extra = push.getExtra("phone");
        assertThat(extra, equalTo("11111111111"));
    }

    @Test
    public void shouldBeAbleToGetDeviceExtra_Single_O1() {
        Push push = Push.getInstance();
        String extra = push.getExtra("account", "11111111111");
        assertThat(extra, equalTo("11111111111"));
    }

    @Test
    public void shouldBeAbleToGetDeviceExtra_Single_Float() {
        Push push = Push.getInstance();
        push.putExtra("float", 29.09F);
        Float extra = push.getExtra("float", 0F);
        assertThat(extra, equalTo(29.09F));
    }

    @Test
    public void shouldBeAbleToGetDeviceExtra_Single_Long() {
        Push push = Push.getInstance();
        push.putExtra("long", 2909L);
        Long extra = push.getExtra("long", 0L);
        assertThat(extra, equalTo(2909L));
    }

    @Test
    public void shouldBeAbleToGetDeviceExtra_Single_Integer() {
        Push push = Push.getInstance();
        push.putExtra("int", 2909);
        Integer extra = push.getExtra("int", 0);
        assertThat(extra, equalTo(2909));
    }

    @Test
    public void shouldBeAbleToGetDeviceExtra_Single_Boolean() {
        Push push = Push.getInstance();
        push.putExtra("boolean", true);
        Boolean extra = push.getExtra("boolean", false);
        assertThat(extra, equalTo(true));
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

    @Test
    public void shouldBeAbleToGetDeviceInfo_Single() {
        Push push = Push.getInstance();
        push.putInfo("phone", "11111111111");
        String info = push.getInfo("phone");
        assertThat(info, equalTo("11111111111"));
    }

    @Test
    public void shouldBeAbleToGetDeviceInfo_Single_01() {
        Push push = Push.getInstance();
        String info = push.getInfo("guid", "11111111111");
        assertThat(info, equalTo("11111111111"));
    }

    @After
    public void cleanup() {
        Push.getInstance().clear();
        context = null;
    }
}
