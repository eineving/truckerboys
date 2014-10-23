package truckerboys.otto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import truckerboys.otto.utils.eventhandler.EventBuss;
import truckerboys.otto.utils.eventhandler.EventType;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.GPSConnectedEvent;
import truckerboys.otto.utils.eventhandler.events.NetworkConnectedEvent;

/**
 * Created by Simon Petersson on 14/10/2014.
 *
 * The launch activity, this handles making sure that we have access to the GPS before launching
 * the application, since it will not run without the GPS enabled. (There is no need for the
 * application without GPS enabled, since it relies heavily on that.)
 */
public class MainActivity extends Activity implements IEventListener{

    private ConnectionListener connectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBuss.getInstance().subscribe(this, EventType.CONNECTION);

        connectionListener = new ConnectionListener(this);
        if(connectionListener.isConnectedToGPS() && connectionListener.isConnectedToNetwork()){
            launchOTTOActivity();
        } else if (!connectionListener.isConnectedToGPS()) {
            launchNoGPSConnectionActivity();
        } else if (!connectionListener.isConnectedToNetwork()) {
            launchNoNetworkConnectionActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(connectionListener.isConnectedToGPS() && connectionListener.isConnectedToNetwork()){
            launchOTTOActivity();
        } else if (!connectionListener.isConnectedToGPS()) {
            launchNoGPSConnectionActivity();
        } else if (!connectionListener.isConnectedToNetwork()) {
            launchNoNetworkConnectionActivity();
        }
    }

    /**
     * Checks what activity should be started 
     */
    private void launchNextActivity(){
        if(connectionListener.isConnectedToNetwork() && connectionListener.isConnectedToGPS()){
            if(NoGPSConnectionActivity.getNoGPSConnectionActivity() != null) {
                NoGPSConnectionActivity.getNoGPSConnectionActivity().finish();
            }
            launchOTTOActivity();
        } else if (!connectionListener.isConnectedToGPS()) {
            launchNoGPSConnectionActivity();
        } else if (!connectionListener.isConnectedToNetwork()) {
            launchNoNetworkConnectionActivity();
        }
    }

    /**
     * Simple method for launching the NoConnectionActivity.
     */
    private void launchNoGPSConnectionActivity(){
        Intent noGPSConnectionIntent = new Intent(this, NoGPSConnectionActivity.class);
        startActivity(noGPSConnectionIntent);
    }

    /**
     * Simple method for launching the NoConnectionActivity.
     */
    private void launchNoNetworkConnectionActivity(){
        Intent noNetworkConnectionIntent = new Intent(this, NoNetworkConnectionActivity.class);
        startActivity(noNetworkConnectionIntent);
    }

    /**
     * SImple method for launching the OTTOActivity.
     */
    private void launchOTTOActivity(){
        Intent OTTOActivity = new Intent(this, OTTOActivity.class);
        startActivity(OTTOActivity);
    }

    @Override
    public void performEvent(Event event) {
        if(event.isType(NetworkConnectedEvent.class) || event.isType(GPSConnectedEvent.class)){
            launchNextActivity();
        }
    }
}
