package utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import truckerboys.otto.directionsAPI.GoogleDirections;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.GooglePlaces;
import truckerboys.otto.planner.EURegulationHandler;
import truckerboys.otto.planner.TripPlanner;


/**
 * Modified adapter for auto completing words for places found by
 * google's places API. This class is to be applied to a AutoCompleteTextView
 * to display results based on google Places API.
 * Parts of this code was reused from an example by google under the
 * CC 3.0 license: http://creativecommons.org/licenses/by/3.0/.
 * Code example: https://developers.google.com/places/training/autocomplete-android
 * Credits to: Google.
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    private TripPlanner tripPlanner;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        // TODO: Use a new or reuse tripplanner?
        tripPlanner = new TripPlanner(new EURegulationHandler(), new GoogleDirections(), new GooglePlaces(), User.getInstance());

    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }


    /**
     * Method for filtering the search results from our wrapper class.
     * @return results from what the user types.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if(charSequence != null) {
                    // Retrieves autocomplete results from TripPlanner class
                    resultList = (ArrayList<String>)tripPlanner.getAddressSuggestion(charSequence.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;
            }

            // Notifies the AutoCompleteTextView that new results is found
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}