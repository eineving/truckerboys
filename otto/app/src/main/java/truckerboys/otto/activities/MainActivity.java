package truckerboys.otto.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import truckerboys.otto.utils.ConnectionListener;
import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.EventType;
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
    private Class currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getInstance().subscribe(this, EventType.CONNECTION);

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
        System.out.println("Launch next activity:");
        System.out.println("GPS: " + connectionListener.isConnectedToGPS());
        System.out.println("Network: " + connectionListener.isConnectedToNetwork());
        System.out.println("currentActivity: " + currentActivity);

        System.out.println(OTTOActivity.class);
        if(connectionListener.isConnectedToNetwork() && connectionListener.isConnectedToGPS() && currentActivity != OTTOActivity.class){
            if(NoGPSConnectionActivity.getNoGPSConnectionActivity() != null) {
                NoGPSConnectionActivity.getNoGPSConnectionActivity().finish();
            }
            if(NoNetworkConnectionActivity.getNoNetworkConnectionActivity() != null){
                NoNetworkConnectionActivity.getNoNetworkConnectionActivity().finish();
            }
            launchOTTOActivity();
        } else if (!connectionListener.isConnectedToGPS() && currentActivity != NoGPSConnectionActivity.class) {
            launchNoGPSConnectionActivity();
        } else if (!connectionListener.isConnectedToNetwork() && currentActivity != NoNetworkConnectionActivity.class) {
            launchNoNetworkConnectionActivity();
        }
    }

    /**
     * Simple method for launching the NoConnectionActivity.
     */
    private void launchNoGPSConnectionActivity(){
        currentActivity = NoGPSConnectionActivity.class;
        Intent noGPSConnectionIntent = new Intent(this, NoGPSConnectionActivity.class);
        startActivity(noGPSConnectionIntent);
    }

    /**
     * Simple method for launching the NoConnectionActivity.
     */
    private void launchNoNetworkConnectionActivity(){
        currentActivity = NoNetworkConnectionActivity.class;
        Intent noNetworkConnectionIntent = new Intent(this, NoNetworkConnectionActivity.class);
        startActivity(noNetworkConnectionIntent);
    }

    /**
     * SImple method for launching the OTTOActivity.
     */
    private void launchOTTOActivity(){
        currentActivity = OTTOActivity.class;
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
