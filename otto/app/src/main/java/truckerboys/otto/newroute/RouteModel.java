package truckerboys.otto.newroute;

import java.util.ArrayList;

/**
 * Created by root on 2014-10-06.
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
