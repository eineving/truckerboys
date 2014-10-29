package truckerboys.otto.utils.eventhandler.events;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Simon Petersson on 2014-10-29.
 *
 * This class tests the Event class.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class TestEvent extends TestCase{

    @Test
    public void testIsType(){
        //Create an event to compare with.
        Event tempEvent = new TestEventEvent();

        //Make sure it returns true when correct.
        assertTrue(tempEvent.isType(TestEventEvent.class));

        //Make sure it returns false when incorrect.
        assertFalse(tempEvent.isType(GPSUpdateEvent.class));
    }
}
