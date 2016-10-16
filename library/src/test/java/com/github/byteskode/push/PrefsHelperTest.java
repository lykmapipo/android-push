package com.github.byteskode.push;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 */

@RunWith(RobolectricTestRunner.class)
public class PrefsHelperTest {
	private Context context;
	private String token = "5nDAHYWK3u521381n112";

	@Before
	public void setup(){
		context = ShadowApplication.getInstance().getApplicationContext();
		PrefsHelper.removeFCMToken(context);
	}


    @Test
    public void shouldCheckIfThereIsFCMToken() {
    	boolean hasFCMToken = PrefsHelper.hasFCMToken(context);
        assertThat(hasFCMToken, equalTo(false));
    }

    @Test
    public void shouldSaveFCMToken() {
    	PrefsHelper.saveFCMToken(context, token);
    	String fcmToken = PrefsHelper.getFCMToken(context);
    	System.out.print(PrefsHelper.getInstallationId(context));
        assertThat(fcmToken, equalTo(token));
    }

    @After
	public void cleanup(){
		PrefsHelper.removeFCMToken(context);
		context = null;
	}
}
