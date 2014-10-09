package truckerboys.otto.newroute;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewDestination;
import truckerboys.otto.utils.eventhandler.events.RefreshHistoryEvent;
import utils.PlacesAutoCompleteAdapter;
import utils.SuggestionProvider;

/**
 * Created by Mikael Malmqvist on 2014-10-02.
 * Activity for when selecting a new route.
 * This class can be seen as the view in the MVP pattern
 * for when selecting a new route.
 */
public class RouteActivity extends Activity implements IEventListener{
    private RoutePresenter routePresenter;
    private RouteModel routeModel = new RouteModel();

    private AutoCompleteTextView search;
    // Geocoder to use when sending a location with the eventTruck
    private Geocoder coder;
    private SharedPreferences history;

    private TextView result;
    private TextView history1Text;
    private TextView history2Text;
    private TextView history3Text;

    private LinearLayout resultsBox;
    private LinearLayout historyBox;
    private LinearLayout searchBox;
    private RelativeLayout history1;
    private RelativeLayout history2;
    private RelativeLayout history3;

    private ContentProvider suggestionProvider = new SuggestionProvider();

    public static final String HISTORY = "History_file";

    public RouteActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_new_route);
        EventTruck.getInstance().subscribe(this);
        coder = new Geocoder(this);
        history = getSharedPreferences(HISTORY, 0);
        routePresenter = new RoutePresenter();

        // Sets ui components
        historyBox = (LinearLayout) findViewById(R.id.history_box);
        resultsBox = (LinearLayout) findViewById(R.id.results_box);
        resultsBox.setX(historyBox.getX());
        searchBox = (LinearLayout) findViewById(R.id.search_box);
        history1 = (RelativeLayout) findViewById(R.id.history1);
        history2 = (RelativeLayout) findViewById(R.id.history2);
        history3 = (RelativeLayout) findViewById(R.id.history3);
        historyBox = (LinearLayout) findViewById(R.id.history_box);

        history1Text = (TextView) findViewById(R.id.history1_text);
        history2Text = (TextView) findViewById(R.id.history2_text);
        history3Text = (TextView) findViewById(R.id.history3_text);
        search = (AutoCompleteTextView) findViewById(R.id.search_text_view);
        result = (TextView) findViewById(R.id.result_text_view);


        // Loads destination history
        routePresenter.loadHistory(history);


        // Sets the adapter to our PlacesAutoCompleteAdapter which uses the TripPlanner class
        // for getting the results
        search.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));


        // Handles when user selects an item from the drop-down menu
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                result.setText(search.getText());

                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(resultsBox.getWidth(), 150);
                layout.setMargins(30, 30, 30, 0);

                resultsBox.setLayoutParams(layout);

            }
        });

        // Handles when user clicks "done" button on keyboard
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                // If "done" on keyboard is clicked set search result to most accurate item
                if(i == 66 && !search.getAdapter().isEmpty()) {
                    result.setText(search.getAdapter().getItem(0).toString());
                    search.setText(search.getAdapter().getItem(0).toString());
                    search.clearFocus();

                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(resultsBox.getWidth(), 150);
                    layout.setMargins(30, 30, 30, 0);

                    resultsBox.setLayoutParams(layout);
                }

                return true;
            }
        });

        // When the user clicks the destination selected
        history1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                routePresenter.sendLocation("" + history1Text.getText(), coder);
            }
        });

        // When the user clicks the destination selected
        history2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                routePresenter.sendLocation("" + history2Text.getText(), coder);

            }
        });

        // When the user clicks the destination selected
        history3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                routePresenter.sendLocation("" + history3Text.getText(), coder);

            }
        });

        // When the user clicks the destination selected
        resultsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result != null && coder != null && routePresenter != null) {
                    routePresenter.sendLocation("" + result.getText(), coder);
                    routePresenter.saveHistory(history, "" + result.getText());
                }
            }
        });

    }




    @Override
    public void performEvent(Event event) {

        // When a new destination is selected this activity is to be finished
        if(event.isType(NewDestination.class)) {

            // Sends user back to MainActivity after have chosen the destination
            finish();

        }

        if(event.isType(RefreshHistoryEvent.class)) {
            history1Text.setText(((RefreshHistoryEvent)event).getPlace1());
            history2Text.setText(((RefreshHistoryEvent)event).getPlace2());
            history3Text.setText(((RefreshHistoryEvent)event).getPlace3());
        }
    }
}
