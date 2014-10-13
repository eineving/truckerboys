package truckerboys.otto.placeSuggestion;

import java.util.List;
import java.util.concurrent.ExecutionException;

import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;

public interface IPlacesAutoComplete {
    /**
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @return suggested addresses
     */
    public List<String> getSuggestedAddresses(String input) throws InvalidRequestException, NoConnectionException;

    /**
     *
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @param currentLocation location to focus the searches from
     * @return suggested addresses
     */
    public List<String> getSuggestedAddresses(String input, MapLocation currentLocation) throws InvalidRequestException, ExecutionException, InterruptedException, NoConnectionException;
}
