package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Simon Petersson on 2014-10-21.
 *
 * Event that specifies if the Map Camera should follow the current position marker.
 */
public class FollowMarkerEvent extends Event {
    private boolean followMarker;
    public FollowMarkerEvent(boolean followMarker) {
        this.followMarker = followMarker;
    }

    public boolean getFollowMarker(){
        return followMarker;
    }
}
