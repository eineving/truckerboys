package truckerboys.otto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Simon Petersson on 2014-09-23.
 *
 * Temporary View to display debug information, feel free to
 * add any debug text that you feel is relevant. This will be removed when the final release
 * is done.
 */
public class DebugView extends Fragment {
    private View rootView;

    public DebugView() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_debug, container, false);

        return rootView;
    }
}
