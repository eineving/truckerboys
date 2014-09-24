package truckerboys.otto;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Simon Petersson on 2014-09-24.
 * This test is just a template for other tests to use. The important parts are the
 * @Config(emulateSdk = 18) before the class definiton, this is NEEDED in order for gradle
 * to be able to compile the test.
 *
 * You also need to define: @RunWith(RobolectricTestRunner.class) in order for gradle to know
 * that this file is to be ran with Robolectric. Which is needed in order to test Android UI
 * elements.
 *
 * Other than this, you write JUnit tests just like you normally would.
 *
 * IMPORTANT: If you want to test android-specific stuff, read up on Robolectric. It adds
 * functionality in order to test android-elements.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class TestTemplate {

}
