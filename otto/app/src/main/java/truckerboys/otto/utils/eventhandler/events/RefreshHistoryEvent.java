package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by Mikael Malmqvist on 2014-10-06.
 */
public class RefreshHistoryEvent extends Event {

    String place1, place2, place3;

    public RefreshHistoryEvent(String place1, String place2, String place3) {
        super(EventType.STATISTICS);
        this.place1 = place1;
        this.place2 = place2;
        this.place3 = place3;
    }

    public String getPlace1() {
        return place1;
    }

    public String getPlace2() {
        return place2;
    }

    public String getPlace3() {
        return place3;
    }
}
