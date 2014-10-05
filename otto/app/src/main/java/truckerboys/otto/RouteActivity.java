package truckerboys.otto;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.SharedPreferences;
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

import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewDestination;
import utils.PlacesAutoCompleteAdapter;
import utils.SuggestionProvider;

/**
 * Created by Mikael Malmqvist on 2014-10-02.
 * Activity for when selecting a new route.
 */
public class RouteActivity extends Activity implements IEventListener{
    private AutoCompleteTextView search;

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

        // Sets ui components
        resultsBox = (LinearLayout) findViewById(R.id.results_box);
        historyBox = (LinearLayout) findViewById(R.id.history_box);
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


        // Sets the adapter to our PlacesAutoCompleteAdapter which uses the TripPlanner class
        // for getting the results
        search.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));

        // When the user selects an item from the drop-down menu
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if((result != null) && (resultsBox != null) && (historyBox != null)) {
                    result.setText(search.getText());
                    resultsBox.setLayoutParams(new LinearLayout.LayoutParams(resultsBox.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
                    resultsBox.setY(historyBox.getY() - resultsBox.getHeight() - 20);
                    resultsBox.setX(historyBox.getX());
                }


            }
        });


        // When the user clicks the destination selected
        history1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Call Google Places API to get the destinations longitude and latitude
                // TODO: send location for planning
                System.out.println("*********DESTINATION: " + history1Text.getText() + "***********");

                EventTruck.getInstance().newEvent(new NewDestination());
            }
        });

        // When the user clicks the destination selected
        history2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Call Google Places API to get the destinations longitude and latitude
                // TODO: send location for planning
                System.out.println("*********DESTINATION: " + history2Text.getText() + "***********");

                EventTruck.getInstance().newEvent(new NewDestination());
            }
        });

        // When the user clicks the destination selected
        history3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Call Google Places API to get the destinations longitude and latitude
                // TODO: send location for planning
                System.out.println("*********DESTINATION: " + history3Text.getText() + "***********");

                EventTruck.getInstance().newEvent(new NewDestination());
            }
        });

        // When the user clicks the destination selected
        resultsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Call Google Places API to get the destinations longitude and latitude
                // TODO: send location for planning
                System.out.println("*********DESTINATION: " + result.getText() + "***********");

                EventTruck.getInstance().newEvent(new NewDestination());
            }
        });

        loadHistory();
    }

    /**
     * Method for loading latest destinations.
     */
    public void loadHistory() {
        SharedPreferences history = getSharedPreferences(HISTORY,0);
    }

    @Override
    public void performEvent(Event event) {
        if(event.isType(NewDestination.class)) {

            // Sends user back to MainActivity after have chosen the destination
            finish();

        }
    }
}
