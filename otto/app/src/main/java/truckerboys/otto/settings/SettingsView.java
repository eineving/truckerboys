package truckerboys.otto.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import truckerboys.otto.FragmentView;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsView extends FragmentView {
    private View rootView;

    public SettingsView(){
        super("Settings", R.layout.fragment_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        fillListView();


        return rootView;
    }

    /**
     * Fills the listview with items
     */
    public void fillListView() {

        // Creates array of items to fill the list with
        String[] myItems = {"Mute sounds","Keep display alive"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.list_item_switch, myItems);

        ListView listView = (ListView) rootView.findViewById(R.id.settingsListView);
        listView.setAdapter(adapter);

    }
}