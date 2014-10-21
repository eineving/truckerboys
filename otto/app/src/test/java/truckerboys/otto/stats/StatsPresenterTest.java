package truckerboys.otto.stats;

import android.content.SharedPreferences;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionType;
import truckerboys.otto.driver.User;

/**
 * Created by Mikael Malmqvist on 2014-10-20.
 * Test class for statsPresenter
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class StatsPresenterTest extends TestCase{

    public static final String STATS = "Stats_file";
    private StatsPresenter statsPresenter;
    private StatsView view;
    private StatsModel model;
    private User user;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        user = new User(Robolectric.application);
        statsPresenter = new StatsPresenter(user);
        view = statsPresenter.getView();
        model = statsPresenter.getModel();

    }

    @Test
    public void testSetStats() {

        int timeToday = 35;
        int distanceByFuel = 75;
        int timeTotal = 70;
        int distanceTotal = 350;
        int fuelTotal = 87;

        double[] statsToday = {timeToday, 0, 0, distanceByFuel};
        double[] statsTotal = {timeTotal, distanceTotal, fuelTotal, 0};
        int violations = 2;

        model.setStats(statsToday, statsTotal, violations);

        assertTrue(model.getTimeToday() == timeToday
                && model.getDistanceByFuel() == distanceByFuel
                && model.getTimeTotal() == timeTotal
                && model.getDistanceTotal() == distanceTotal
                && model.getFuelTotal() == fuelTotal
                && model.getViolations() == violations);
    }

    /**
     * Method for setting dummy stats for testRestorePreferences.
     */
    public void setDummyStats(float distanceByFuel, float distanceTotal, float fuelTotal, int violations) {
        SharedPreferences.Editor statsEditor = Robolectric.application.getSharedPreferences(STATS, 0).edit();
        statsEditor.putFloat("distanceByFuel", distanceByFuel);
        statsEditor.putFloat("distanceTotal", distanceTotal);
        statsEditor.putFloat("fuelTotal", fuelTotal);
        statsEditor.putFloat("violation", violations);

        statsEditor.commit();

    }

    @Test
    public void testLoadTime() {

        // TODO Test loadTime()
        assertTrue(true);
    }

    @Test
    public void testLoadUserHistory() {

        Session session = new Session(SessionType.WORKING, Instant.now());
        session.end();

        user.getHistory().addSession(session);
        statsPresenter.loadUserHistory();

        String sessionString = new DateTime(session.getStartTime()).getYear()
                + "-" + new DateTime(session.getStartTime()).getMonthOfYear()
                + "-" + new DateTime(session.getStartTime()).getDayOfMonth()
                + ": " + session.getSessionType().toString()
                + " for " + session.getDuration().getStandardHours()
                + "h " + session.getDuration().getStandardMinutes() + "min";


        assertTrue(model.getSessionHistory().contains(sessionString));
    }

    @Test
    public void testSaveCurrentStats() {


        int distanceByFuel = 75;
        int distanceTotal = 350;
        int fuelTotal = 87;

        double[] statsToday = {0, 0, 0, distanceByFuel};
        double[] statsTotal = {0, distanceTotal, fuelTotal, 0};
        int violations = 2;

        statsPresenter.setStats(statsToday, statsTotal, violations);
        statsPresenter.saveCurrentStats(Robolectric.application.getSharedPreferences(STATS,0));

        SharedPreferences stats = Robolectric.application.getSharedPreferences(STATS, 0);
        double[] statsFromFileToday = {0, 0, 0, stats.getFloat("distanceByFuel", 0)};
        double[] statsFromFileTotal = {0, stats.getFloat("distanceTotal", 0), stats.getFloat("fuelTotal", 0), 0};


        boolean same = true;
        for(int i = 0; i < statsFromFileToday.length; i++) {
            if(statsFromFileToday[i] != statsToday[i] || statsFromFileTotal[i] != statsTotal[i]) {
                same = false;
            }
        }

        assertTrue(same);
    }

    @Test
    public void testRestorePreferences() {

        // Sets dummy stats
        float distanceByFuel = 55;
        float distanceTotal = 45;
        float fuelTotal = 35;
        int violaions = 25;

        setDummyStats(distanceByFuel, distanceTotal, fuelTotal, violaions);

        // Runs method to be tested
        statsPresenter.restorePreferences(Robolectric.application.getSharedPreferences(STATS, 0));


        // Compares values
        assertTrue(model.getDistanceByFuel() == distanceByFuel
                && model.getDistanceTotal() == distanceTotal
                && model.getFuelTotal() == fuelTotal
                && model.getViolations() == violaions);
    }
}
