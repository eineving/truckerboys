package truckerboys.otto.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-23.
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.settings_Preferences), this.MODE_PRIVATE);

        // Show SettingsFragment here
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
