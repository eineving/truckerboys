package truckerboys.otto.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import truckerboys.otto.R;

/**
 * Created by Simon Petersson on 2014-10-14.
 *
 * This Activity is shown when the app has no connection to the GPS or loses to connection when
 * the app is running.
 */
public class NoGPSConnectionActivity extends Activity{

    private Button turnOnGPS;

    private static NoGPSConnectionActivity noGPSConnectionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_gps_conection);

        //Make singleton of this activity, making it possible to close it from another activity.
        noGPSConnectionActivity = this;

        //Make it possible for the user to easily access the GPS settings.
        turnOnGPS = (Button) findViewById(R.id.activateGPSButton);
        turnOnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send the user to settings, asking him to turn on GPS.
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    @Override
    public void onBackPressed() {
        ; // Do nothing, make sure the user doesn't go back to OTTOActivity
    }

    @Override
    public void finish() {
        super.finish();
        noGPSConnectionActivity = null;
    }

    public static NoGPSConnectionActivity getNoGPSConnectionActivity() {
        return noGPSConnectionActivity;
    }
}
