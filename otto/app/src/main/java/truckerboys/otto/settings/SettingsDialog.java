package truckerboys.otto.settings;

import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Mikael Malmqvist on 2014-10-02.
 * Class for displaying dialogs within settings menu.
 */
public class SettingsDialog extends DialogFragment {
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static SettingsDialog newInstance(int num) {
        SettingsDialog f = new SettingsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
}
