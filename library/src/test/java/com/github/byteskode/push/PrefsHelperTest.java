package com.github.byteskode.push;

import org.junit.Before;
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

	@Before
	public void setup(){
		context = ShadowApplication.getInstance().getApplicationContext();
	}


    @Test
    public void shouldCheckIfThereIsFCMToken() {
    	boolean hasFCMToken = PrefsHelper.hasFCMToken(context);
        assertThat(hasFCMToken, equalTo(false));
    }
}
