package truckerboys.otto;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Simon Petersson on 2014-10-14.
 *
 * This Activity is shown when the app has no connection to the GPS or loses to connection when
 * the app is running.
 */
public class NoConnectionActivity extends Activity{

    private static NoConnectionActivity noConnectionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_conection);

        //Make singleton of this activity, making it possible to close it from another activity.
        noConnectionActivity = this;
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
    public void finish() {
        super.finish();
        noConnectionActivity = null;
    }

    public static NoConnectionActivity getNoConnectionActivity() {
        return noConnectionActivity;
    }
}
