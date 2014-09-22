package truckerboys.otto.settings;

import android.app.Activity;
import android.os.Bundle;

import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
