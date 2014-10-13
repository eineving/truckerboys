package truckerboys.otto.newroute;

import java.util.ArrayList;

/**
 * Created by Mikael Malmqvist on 2014-10-06.
 * Class for holding some data for the RouteActivity.
 */
public class RouteModel {
    private ArrayList<String> checkpoints;

    public RouteModel () {
        checkpoints = new ArrayList<String>();
    }

    public ArrayList<String> getCheckpoints() {
        return checkpoints;
    }
}
