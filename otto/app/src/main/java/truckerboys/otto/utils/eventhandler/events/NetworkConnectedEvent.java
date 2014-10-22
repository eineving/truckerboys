package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Simon Petersson on 2014-10-21.
 *
 * Event that is fired when the GPS/Network changes conectivity status.
 */
public class NetworkConnectedEvent extends Event {

    private boolean connected;

    public NetworkConnectedEvent(boolean connected) {
        super();
        this.connected = connected;
    }

    /**
     * Returns whether or not the device is currently connected to the GPS/Network.
     * @return True if the device has GPS/Network connectvitiy.
     */
    public boolean isConnected(){
        return connected;
    }
}
