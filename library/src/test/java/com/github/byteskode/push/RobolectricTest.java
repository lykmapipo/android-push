package com.github.byteskode.push;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 8/10/16
 */

@RunWith(RobolectricTestRunner.class)
public class RobolectricTest {
    @Test
    public void testIt() {
        assertThat(1, equalTo(1));
    }
}
