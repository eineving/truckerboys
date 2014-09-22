package truckerboys.otto.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import truckerboys.otto.R;
import truckerboys.otto.settings.SettingsView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomeView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    public void newActivity(View view){
        startActivity(new Intent(getActivity(), SettingsView.class));
    }
}
