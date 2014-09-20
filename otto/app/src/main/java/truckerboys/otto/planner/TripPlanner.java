package truckerboys.otto.planner;

import java.util.List;

import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.User;
import truckerboys.otto.mapAPI.IMap;
import truckerboys.otto.planner.positions.Location;

public class TripPlanner {
    private User user;
    private IRegulationHandler regulationHandler;
    private IMap mapProvider;

    private Location finalDestination;

    public TripPlanner(IRegulationHandler regulationHandler, IMap mapProvider, User user) {
        this.regulationHandler = regulationHandler;
        this.mapProvider = mapProvider;
        this.user=user;
    }

    public void calculateNewRouteTo(Location finalDestination){
        addFinalDestination(finalDestination);
        mapProvider.calculateRoute();
    }

    private void addFinalDestination(Location finalDestination) {
        this.finalDestination = finalDestination;
        mapProvider.setFinalDestination(finalDestination);
    }
}
