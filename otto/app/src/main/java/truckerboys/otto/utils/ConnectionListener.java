package truckerboys.otto.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;

import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.GPSConnectedEvent;
import truckerboys.otto.utils.eventhandler.events.NetworkConnectedEvent;

/**
 * Created by Simon Petersson on 2014-10-21.
 *
 * This class is responsible to listen for Connectivity changes in the phone. It will fire
 * events when GPS and/or Network connectivity is established/lost.
 */
public class ConnectionListener extends BroadcastReceiver implements LocationListener{

    private LocationManager locationManager;
    private Context context;

    public ConnectionListener(){
        super();
    }

    public ConnectionListener(Context context) {
        this();

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
    }

    /**
     * Checks whether or not the device has GPS enabled.
     * @return True if GPS is available.
     */
    public boolean isConnectedToGPS(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks whether or not the device has Network connectivity.
     * @return True if Network is available.
     */
    public boolean isConnectedToNetwork(){
        ConnectivityManager temp = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return temp.getActiveNetworkInfo() != null && temp.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        //GPS is now conected.
        EventBus.getInstance().newEvent(new GPSConnectedEvent());
    }

    @Override
    public void onProviderDisabled(String s) {
        //GPS is no longer connected.
        EventBus.getInstance().newEvent(new GPSConnectedEvent());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()) {
                EventBus.getInstance().newEvent(new NetworkConnectedEvent(true));
            } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                EventBus.getInstance().newEvent(new NetworkConnectedEvent(false));
            }
        }
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}
