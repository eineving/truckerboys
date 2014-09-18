package truckerboys.otto.planner;

import truckerboys.otto.mapAPI.IMap;
import truckerboys.otto.planner.positions.Location;

public class TripPlanner {
    private IRegulationHandler regulationHandler;
    private IMap mapProvider;

    private Location finalDestination;

    public TripPlanner(IRegulationHandler regulationHandler, IMap mapProvider) {
        this.regulationHandler = regulationHandler;
        this.mapProvider = mapProvider;
    }

    public void addFinalDestination(Location finalDestination) {
        this.finalDestination = finalDestination;
    }

    /**
     * Calculates and sends the wanted route to the mapAPI
     */
    public void calculateNewRoute(){
        //TODO Eineving insert code here
    }

}
