package truckerboys.otto.utils.eventhandler;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import truckerboys.otto.utils.eventhandler.events.EventType;
import truckerboys.otto.utils.eventhandler.events.TestEventEvent;
import truckerboys.otto.utils.eventhandler.events.Event;

/**
 * Created by Simon Petersson on 2014-10-29.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class EventBusTest extends TestCase{

    @Test
    public void testGetInstance(){
        //Test to make sure that we can get a new instance, if we've never used EventBus before.
        assertNotNull(EventBus.getInstance());

        //Test again to make sure it doesn't bugg when requesting the instance several times.
        assertNotNull(EventBus.getInstance());
    }

    /**
     * Tests if the EventBus fires events to it's subscribers when asked to.
     */
    @Test
    public void testNewEventAndSubscribe(){
        //Make sure we can fire events without any subscribers.
        EventBus.getInstance().newEvent(new TestEventEvent());

        //Add subscriber to the EventBus.
        EventBus.getInstance().subscribe(new IEventListener() {
            @Override
            public void performEvent(Event event) {
                assertTrue(event.isType(TestEventEvent.class));
            }
        }, EventType.TEST);

        //Fire another event, to make sure the subscriber can catch it.
        EventBus.getInstance().newEvent(new TestEventEvent());
    }
}
