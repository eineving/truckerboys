package truckerboys.otto;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by Mikael Malmqvist on 2014-10-02.
 * Activity for when selecting a new route.
 */
public class RouteActivity extends Activity {
    public static final String HISTORY = "History_file";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_new_route);

        loadHistory();
    }

    public void loadHistory() {
        SharedPreferences history = getSharedPreferences(HISTORY,0);
    }
}
