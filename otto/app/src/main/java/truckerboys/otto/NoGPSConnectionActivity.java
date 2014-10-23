package truckerboys.otto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

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
