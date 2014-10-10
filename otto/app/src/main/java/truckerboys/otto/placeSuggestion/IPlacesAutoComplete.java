package truckerboys.otto.placeSuggestion;

import java.util.List;

import truckerboys.otto.utils.positions.MapLocation;

public interface IPlacesAutoComplete {
    /**
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @return suggested addresses
     */
    public List<String> getSuggestedAddresses(String input);

    /**
     *
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @param currentLocation location to focus the searches from
     * @return suggested addresses
     */
    public List<String> getSuggestedAddresses(String input, MapLocation currentLocation);
}
