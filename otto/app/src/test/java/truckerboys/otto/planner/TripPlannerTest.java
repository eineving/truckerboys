package truckerboys.otto.planner;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import truckerboys.otto.directionsAPI.GoogleDirections;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionType;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.GooglePlaces;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RouteLocation;



@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class TripPlannerTest extends TestCase {
    //Bergvagen 11 Molnlycke
    private MapLocation currentLocation = new MapLocation(new LatLng(57.6535522, 12.1244496));

    private final MapLocation malmo = new MapLocation(new LatLng(55.5708457, 13.0180405));
    private final MapLocation kiruna = new MapLocation(new LatLng(67.853702, 20.2564299));
    private final MapLocation stockholm = new MapLocation(new LatLng(59.3261419, 17.9875456));

    private User user = new User(Robolectric.application);
    private TripPlanner tripPlanner;

    private PlannedRoute activeRoute;

    private EURegulationHandler euRegulationHandler = new EURegulationHandler();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        tripPlanner = new TripPlanner(euRegulationHandler, new GoogleDirections(), new GooglePlaces(), user);

        Session session = new Session(SessionType.WORKING, Instant.now());
        session.end();

        user.getHistory().addSession(session);
    }

    @Test
    public void testUpdateRoute() throws Exception {
        ArrayList<MapLocation> checkpoints = new ArrayList<MapLocation>();
        checkpoints.add(malmo);
        checkpoints.add(stockholm);
        tripPlanner.setNewRoute(currentLocation, kiruna, checkpoints);
        activeRoute = tripPlanner.getRoute();
        Duration firstETA = activeRoute.getEta();

        //E6 south of Kungsbacka
        tripPlanner.updateRoute(new MapLocation(new LatLng(57.5539342,12.061378)));

        assertTrue(tripPlanner.getRoute().getEta().isShorterThan(firstETA));

    }

    @Test
    public void testGetRoute() throws Exception {
        ArrayList<MapLocation> checkpoints = new ArrayList<MapLocation>();
        checkpoints.add(malmo);
        checkpoints.add(stockholm);
        tripPlanner.setNewRoute(currentLocation, kiruna, checkpoints);
        assertNotNull(tripPlanner.getRoute());
    }

    @Test
    public void testSetChoosenStop() throws Exception {
        ArrayList<MapLocation> checkpoints = new ArrayList<MapLocation>();
        checkpoints.add(malmo);
        checkpoints.add(stockholm);
        tripPlanner.setNewRoute(currentLocation, kiruna, checkpoints);
        activeRoute = tripPlanner.getRoute();

        if(activeRoute.getAlternativeStops().size() > 0) {
            RouteLocation chosen = activeRoute.getAlternativeStops().get(0);
            tripPlanner.setChoosenStop(chosen);
            assertTrue(tripPlanner.getRoute().getRecommendedStop().equalCoordinates(chosen));
        }
    }

    @Test
    public void testSetNewRoute() throws Exception {
        ArrayList<MapLocation> checkpoints = new ArrayList<MapLocation>();
        checkpoints.add(malmo);
        checkpoints.add(stockholm);
        tripPlanner.setNewRoute(currentLocation, kiruna, checkpoints);
        activeRoute = tripPlanner.getRoute();

        //First checkpoint should not have an ETA over 4,5 hours
        assertFalse(activeRoute.getCheckpoints().get(0).getEta().isLongerThan(Duration.standardMinutes(270)));

        //These are out of reach within one session
        assertFalse(activeRoute.getRecommendedStop().equalCoordinates(kiruna));
        assertFalse(activeRoute.getRecommendedStop().equalCoordinates(stockholm));

        assertFalse(activeRoute.getEta().isEqual(activeRoute.getCheckpoints().get(0).getEta()));

        for(RouteLocation stop : activeRoute.getAlternativeStops()){
            assertTrue(stop.getEta().isShorterThan(euRegulationHandler.getThisSessionTL(user.getHistory()).getExtendedTimeLeft()));
        }

        //Malmo is within reach
        tripPlanner.setNewRoute(currentLocation, malmo, null);
        activeRoute=tripPlanner.getRoute();

        assertTrue(activeRoute.getEta().isEqual(activeRoute.getCheckpoints().get(0).getEta()));
    }

    @Test
    public void testPassedCheckpoint() throws Exception{
        ArrayList<MapLocation> checkpoints = new ArrayList<MapLocation>();
        checkpoints.add(malmo);
        checkpoints.add(stockholm);
        tripPlanner.setNewRoute(currentLocation, kiruna, checkpoints);
        activeRoute = tripPlanner.getRoute();

        tripPlanner.passedCheckpoint(malmo);

        for(MapLocation temp : tripPlanner.getRoute().getCheckpoints()){
            assertFalse(temp.equalCoordinates(malmo));
        }
    }
}