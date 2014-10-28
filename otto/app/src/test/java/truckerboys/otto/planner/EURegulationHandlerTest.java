package truckerboys.otto.planner;

import junit.framework.TestCase;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionHistory;
import truckerboys.otto.driver.SessionType;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class EURegulationHandlerTest extends TestCase {
    EURegulationHandler handler = new EURegulationHandler();

    @Test
    public void testGetThisSessionTL() throws Exception {
        SessionHistory s = new SessionHistory();
        //Empty history, bas case.
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(270)));


        //15 minutes driven.
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardMinutes(15))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(255)));

        //15 minutes driven and then a 15 min rest.
        s.addSession(new Session(SessionType.RESTING, new Instant(1).plus(Duration.standardMinutes(15)), new Instant(1).plus(Duration.standardMinutes(30))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(255)));

        //15 minutes driven, 15 min rest, 30 driven
        s.addSession(new Session(SessionType.DRIVING, new Instant(2).plus(Duration.standardMinutes(30)), new Instant(2).plus(Duration.standardMinutes(60))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(225)));

        //15 minutes driven, 15 min rest, 30 driven, 30 min rest. (Split daily rest)
        s.addSession(new Session(SessionType.RESTING, new Instant(3).plus(Duration.standardMinutes(60)), new Instant(3).plus(Duration.standardMinutes(90))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(270)));


        //15 minutes driven and then a finished standard rest.
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardMinutes(15))));
        s.addSession(new Session(SessionType.RESTING, new Instant(1).plus(Duration.standardMinutes(15)), new Instant(1).plus(Duration.standardMinutes(60))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(270)));

        //Invalid split daily rest (rests in wrong order)
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardMinutes(15))));
        s.addSession(new Session(SessionType.RESTING, new Instant(1).plus(Duration.standardMinutes(15)), new Instant(1).plus(Duration.standardMinutes(45))));
        s.addSession(new Session(SessionType.DRIVING, new Instant(2).plus(Duration.standardMinutes(45)), new Instant(2).plus(Duration.standardMinutes(105))));
        s.addSession(new Session(SessionType.RESTING, new Instant(3).plus(Duration.standardMinutes(105)), new Instant(3).plus(Duration.standardMinutes(120))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(195)));

        //if you take 30 min more break you should be allowed to drive 270min again.
        s.addSession(new Session(SessionType.RESTING, new Instant(4).plus(Duration.standardMinutes(120)), new Instant(4).plus(Duration.standardMinutes(150))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(270)));

        //Work doesn't count as rest nor as driving time.
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardMinutes(15))));
        s.addSession(new Session(SessionType.WORKING, new Instant(1).plus(Duration.standardMinutes(15)), new Instant(1).plus(Duration.standardMinutes(75))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(255)));

        //Working combined with driving and resting.
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardMinutes(15))));
        s.addSession(new Session(SessionType.WORKING, new Instant(1).plus(Duration.standardMinutes(15)), new Instant(1).plus(Duration.standardMinutes(75))));
        s.addSession(new Session(SessionType.RESTING, new Instant(2).plus(Duration.standardMinutes(75)), new Instant(2).plus(Duration.standardMinutes(120))));
        assertTrue(handler.getThisSessionTL(s).getTimeLeft().isEqual(Duration.standardMinutes(270)));

    }

    @Test
    public void testGetThisDayTL() throws Exception {
        SessionHistory s = new SessionHistory();
        //Empty history, bas case.
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(9)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(1)));

        //5h driven.
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(5))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(4)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(1)));


        //5h driven, standard daily rest.
        s.addSession(new Session(SessionType.RESTING, new Instant(1).plus(Duration.standardHours(5)), new Instant(1).plus(Duration.standardHours(16))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(9)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(1)));

        //9h driven
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(9))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(0)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(1)));

        //9h 30min driven
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(9).plus(Duration.standardMinutes(30)))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(0)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardMinutes(30)));


        //Drive for 10h two days in the same week
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(10))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(0)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        s.addSession(new Session(SessionType.RESTING, new Instant(1).plus(Duration.standardHours(10)), new Instant(1).plus(Duration.standardHours(21))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(9)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(1)));

        s.addSession(new Session(SessionType.DRIVING, new Instant(2).plus(Duration.standardHours(21)), new Instant(2).plus(Duration.standardHours(31))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(0)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        s.addSession(new Session(SessionType.RESTING, new Instant(3).plus(Duration.standardHours(31)), new Instant(3).plus(Duration.standardHours(42))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(9)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        //Take a weekly rest
        s.addSession(new Session(SessionType.RESTING, new Instant(4).plus(Duration.standardHours(42)), new Instant(4).plus(Duration.standardHours(92))));
        assertTrue(handler.getThisDayTL(s).getTimeLeft().isEqual(Duration.standardHours(9)) &&
                handler.getThisDayTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(1)));



    }

    @Test
    public void testGetThisWeekTL() throws Exception {
        SessionHistory s = new SessionHistory();
        //Empty history, bas case.
        assertTrue(handler.getThisWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(56))&&
                handler.getThisWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        //Drive 6h
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(6))));
        assertTrue(handler.getThisWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(50))&&
                handler.getThisWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        //Drive 56h then take a weekly rest.
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(56))));
        s.addSession(new Session(SessionType.RESTING, new Instant(1).plus(Duration.standardHours(56)), new Instant(1).plus(Duration.standardHours(106))));
        assertTrue(handler.getThisWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(34))&&
                handler.getThisWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));


    }

    @Test
    public void testGetNextWeekTL() throws Exception {
        SessionHistory s = new SessionHistory();
        //Empty history, bas case.
        assertTrue(handler.getNextWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(56))&&
                handler.getNextWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        //Drive 35h
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(35))));
        assertTrue(handler.getNextWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(55))&&
                handler.getNextWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));
    }

    @Test
    public void testGetThisTwoWeekTL() throws Exception {
        SessionHistory s = new SessionHistory();
        //Empty history, bas case.
        assertTrue(handler.getThisTwoWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(90))&&
                handler.getThisTwoWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        //Drive 7h
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(7))));
        assertTrue(handler.getThisTwoWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(83))&&
                handler.getThisTwoWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

        //Drive 35h
        s = new SessionHistory();
        s.addSession(new Session(SessionType.DRIVING, new Instant(0), new Instant(0).plus(Duration.standardHours(35))));
        assertTrue(handler.getThisTwoWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(55))&&
                handler.getThisTwoWeekTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));
    }

    @Test
    public void testGetNextTwoWeekTL() throws Exception {
        SessionHistory s = new SessionHistory();
        //Empty history, bas case.
        assertTrue(handler.getNextTwoWeekTL(s).getTimeLeft().isEqual(Duration.standardHours(90))&&
                handler.getThisSessionTL(s).getExtendedTimeLeft().isEqual(Duration.standardHours(0)));

    }

    @Test
    public void testGetTimeLeftOnBreak() throws Exception {
        SessionHistory s = new SessionHistory();
        s.addSession(new Session(SessionType.RESTING, new Instant(0), new Instant(0).plus(Duration.standardHours(1))));
        //Empty history, bas case.
        assertTrue(handler.getTimeLeftOnBreak(s).getTimeLeft().isEqual(Duration.ZERO));
    }
}