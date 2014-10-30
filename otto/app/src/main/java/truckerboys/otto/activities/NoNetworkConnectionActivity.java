package truckerboys.otto.activities;

import android.app.Activity;
import android.os.Bundle;

import truckerboys.otto.R;

/**
 * Created by Simon Petersson on 2014-10-14.
 *
 * This Activity is shown when the app has no connection to the Internet or loses to connection when
 * the app is running.
 */
public class NoNetworkConnectionActivity extends Activity{

    private static NoNetworkConnectionActivity noNetworkConnectionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network_connection);

        //Make singleton of this activity, making it possible to close it from another activity.
        noNetworkConnectionActivity = this;
    }

    @Override
    public void onBackPressed() {
        ; // Do nothing, make sure the user doesn't go back to OTTOActivity
    }

    @Override
    public void finish() {
        super.finish();
        noNetworkConnectionActivity = null;
    }

    public static NoNetworkConnectionActivity getNoNetworkConnectionActivity() {
        return noNetworkConnectionActivity;
    }
}
