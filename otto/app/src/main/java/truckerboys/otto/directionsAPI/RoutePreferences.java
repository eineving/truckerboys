package truckerboys.otto.directionsAPI;

/**
 * Created by Daniel on 2014-09-24.
 */
public class RoutePreferences {
    private boolean optimizeCheckpoints;
    private boolean avoidTolls, avoidHighways, avoidFerries;


    public RoutePreferences(boolean optimizeCheckpoints, boolean avoidTolls, boolean avoidHighways, boolean avoidFerries) {
        this.optimizeCheckpoints = optimizeCheckpoints;
        this.avoidTolls = avoidTolls;
        this.avoidHighways = avoidHighways;
        this.avoidFerries = avoidFerries;
    }
}
