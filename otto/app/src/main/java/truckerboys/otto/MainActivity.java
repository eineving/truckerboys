package truckerboys.otto;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import truckerboys.otto.home.ActiveSessionDialogFragment;

/**
 * Created by Simon Petersson on 14/10/2014.
 *
 * The launch activity, this handles making sure that we have access to the GPS before launching
 * the application, since it will not run without the GPS enabled. (There is no need for the
 * application without GPS enabled, since it relies heavily on that.)
 */
public class MainActivity extends Activity implements LocationListener{

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);

        if(isConnectedToGPS()){
            launchOTTOActivity();
        } else {
            launchNoConnectionActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isConnectedToGPS()){
            launchOTTOActivity();
        } else {
            launchNoConnectionActivity();
        }
    }

    private boolean isConnectedToGPS(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        //If Connection has been established, close NoConnetionActivity.
        NoConnectionActivity.getNoConnectionActivity().finish();
        //Then launch OTTOActivity
        launchOTTOActivity();
    }

    @Override
    public void onProviderDisabled(String s) {
        //If provider isn't availible, launch NoConnecitonActivity.
        launchNoConnectionActivity();
    }

    /**
     * Simple method for launching the NoConnectionActivity.
     */
    private void launchNoConnectionActivity(){
        Intent noConnectionIntent = new Intent(this, NoConnectionActivity.class);
        startActivity(noConnectionIntent);
    }

    /**
     * SImple method for launching the OTTOActivity.
     */
    private void launchOTTOActivity(){
        Intent OTTOActivity = new Intent(this, OTTOActivity.class);
        startActivity(OTTOActivity);
    }
}
