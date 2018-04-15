package com.github.lykmapipo.push;

import android.content.Context;

import com.github.lykmapipo.push.api.Device;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com
 */

@Config(sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class UtilsTest {
    private Context context;

    @Before
    public void setup() {
        context = ShadowApplication.getInstance().getApplicationContext();
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceHasExternalMemory() {
        boolean hasExternalMemory = Utils.hasExternalMemory();
        assertThat(hasExternalMemory, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceTotalExternalMemory() {
        long totalExternalMemorySize = Utils.getTotalExternalMemorySize();
        assertThat(totalExternalMemorySize, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceAvailableExternalMemory() {
        long availableExternalMemorySize = Utils.getAvailableExternalMemorySize();
        assertThat(availableExternalMemorySize, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceTotalInternalMemory() {
        long totalInternalMemorySize = Utils.getTotalInternalMemorySize();
        assertThat(totalInternalMemorySize, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceAvailableInternalMemory() {
        long availableInternalMemorySize = Utils.getAvailableInternalMemorySize();
        assertThat(availableInternalMemorySize, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceTotalRAM() {
        long totalRAM = Utils.getTotalRAM(context);
        assertThat(totalRAM, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceIsConnectedOnInternet() {
        boolean isConnected = Utils.isConnected(context);
        assertThat(isConnected, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfDeviceWifiIsEnabled() {
        boolean isWifiEnabled = Utils.isWifiEnabled(context);
        assertThat(isWifiEnabled, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceNetworkType() {
        String networkType = Utils.getNetworkType(context);
        assertThat(networkType, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetMemoryInfo() {
        Map<String, String> memoryInfo = Utils.getMemoryInfo(context);

        //assert memory info
        assertThat(memoryInfo.get(Device.AVAILABLE_EXTERNAL_MEMORY_SIZE), is(notNullValue()));
        assertThat(memoryInfo.get(Device.AVAILABLE_INTERNAL_MEMORY_SIZE), is(notNullValue()));
        assertThat(memoryInfo.get(Device.HAS_EXTERNAL_MEMORY), is(notNullValue()));
        assertThat(memoryInfo.get(Device.TOTAL_EXTERNAL_MEMORY_SIZE), is(notNullValue()));
        assertThat(memoryInfo.get(Device.TOTAL_INTERNAL_MEMORY_SIZE), is(notNullValue()));
        assertThat(memoryInfo.get(Device.TOTAL_RAM), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceScreenDensity() {
        String screenDensity = Utils.getScreenDensity(context);
        assertThat(screenDensity, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceScreenHeight() {
        long screenHeight = Utils.getScreenHeight(context);
        assertThat(screenHeight, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceScreenWidth() {
        long screenWidth = Utils.getScreenWidth(context);
        assertThat(screenWidth, is(notNullValue()));
    }


    @Test
    public void shouldBeAbleToGetDisplayInfo() {
        Map<String, String> displayInfo = Utils.getDisplayInfo(context);

        //assert display info
        assertThat(displayInfo.get(Device.SCREEN_DENSITY), is(notNullValue()));
        assertThat(displayInfo.get(Device.SCREEN_HEIGHT), is(notNullValue()));
        assertThat(displayInfo.get(Device.SCREEN_WIDTH), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceCountryCode() {
        String countryCode = Utils.getCountryCode(context);
        assertThat(countryCode, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceCountryName() {
        String countryName = Utils.getCountryName(context);
        assertThat(countryName, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceLanguageCode() {
        String languageCode = Utils.getLanguageCode(context);
        assertThat(languageCode, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceLanguageName() {
        String languageName = Utils.getLanguageName(context);
        assertThat(languageName, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetDeviceTimezone() {
        String timezone = Utils.getTimezone();
        assertThat(timezone, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetLocaleInfo() {
        Map<String, String> localeInfo = Utils.getLocaleInfo(context);

        //assert display info
        assertThat(localeInfo.get(Device.COUNTRY_CODE), is(notNullValue()));
        assertThat(localeInfo.get(Device.COUNTRY_NAME), is(notNullValue()));
        assertThat(localeInfo.get(Device.LANGUAGE_CODE), is(notNullValue()));
        assertThat(localeInfo.get(Device.LANGUAGE_NAME), is(notNullValue()));
        assertThat(localeInfo.get(Device.TIMEZONE), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetPackageVersionName() {
        String versionName = Utils.getVersionName(context);
        assertThat(versionName, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetPackageVersionCode() {
        String versionCode = Utils.getVersionCode(context);
        assertThat(versionCode, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToGetPackageInfo() {
        Map<String, String> packageInfo = Utils.getPackageInfo(context);

        //assert package info
        assertThat(packageInfo.get(Device.VERSION_NAME), is(notNullValue()));
        assertThat(packageInfo.get(Device.VERSION_CODE), is(notNullValue()));
        assertThat(packageInfo.get(Device.PACKAGE), is(notNullValue()));
    }
}
