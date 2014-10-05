package truckerboys.otto.directionsAPI;

import org.joda.time.Duration;

import truckerboys.otto.utils.positions.MapLocation;

/**
 * Interface to make project work with multiple mapAPIs
 */
public interface IDirections {
    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @param preferences      route requirements
     * @param checkpoint       locations that the route needs to go to before the final destination
     * @return a new route
     * @throws Exception
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, RoutePreferences preferences, MapLocation... checkpoint) throws Exception;

    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @param preferences      route requirements
     * @return a new route
     * @throws Exception
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, RoutePreferences preferences) throws Exception;

    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @param checkpoint       locations that the route needs to go to before the final destination
     * @return a new route
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, MapLocation... checkpoint) throws Exception;

    /**
     * Creates a new route
     *
     * @param currentPosition position of the device
     * @param finalDestination the location that will end the route
     * @return a new route
     */
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination) throws Exception;

    /**
     * Get Estimated Time of Arrival to specified location without checkpoints
     *
     * @param currentPosition current position of the device
     * @param finalDestination that Estimated Time of Arrival is needed upon
     * @return Estimated Time of Arrival to target location without checkpoints
     */
    public Duration getETA(MapLocation currentPosition, MapLocation finalDestination) throws Exception;
}