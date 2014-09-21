package truckerboys.otto.mapAPI;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.planner.positions.Location;

/**
 * Interface to make project work with multiple mapAPIs
 */
public interface IMap {

    /**
     * Get the most fitting countries for the input
     *
     * @param input Complete or incomplete String of a country input
     * @return A list of what countries the mapAPI suggest for your input
     */
    public List<String> getSuggestedCountry(String input);

    /**
     * Get the most fitting city for the input
     *
     * @param input Complete or incomplete String of a city input
     * @return A list of what cities the mapAPI suggest for your input
     */
    public List<String> getSuggestedCity(String input);

    /**
     * Get the most fitting zipcode for the input
     *
     * @param input Complete or incomplete String of a zipcode input
     * @return A list of what zipcodes the mapAPI suggest for your input
     */
    public List<String> getSuggestedZipCode(String input);

    /**
     * Get the most fitting street for the input
     *
     * @param input Complete or incomplete String of a street input
     * @return A list of what streets the mapAPI suggest for your input
     */
    public List<String> getSuggestedStreet(String input);

    /**
     * Get the most fitting street address for the input
     *
     * @param input Complete or incomplete String of a street address input
     * @return A list of what street addresses the mapAPI suggest for your input
     */
    public List<String> getSuggestedStreetAddress(String input);

    /**
     * Set the final destination
     */
    public void setFinalDestination(String country, String city, String zipCode, String street, String streetNumber);

    /**
     * Set the final destination
     */
    public void setFinalDestination(double longitude, double latitude);

    /**
     * Set the final destination
     */
    public void setFinalDestination(Location location);

    /**
     * Add an a destination to go to before the final destination
     */
    public void addCheckPointDestination(String country, String city, String zipCode, String street, String streetNumber);

    /**
     * Add an a destination to go to before the final destination
     */
    public void addCheckPointDestination(double longitude, double latitude);

    /**
     * Add an a destination to go to before the final destination
     */
    public void addCheckPointDestination(Location location);

    /**
     * The mapAPI calculates a new route
     */
    public void calculateRoute();

    /**
     * Get all gas stations that are on or close to the planned route
     *
     * @return a list of gas station close to the planned route
     * @required that a final destination is set
     */
    public List<Location> getGasStationsAlongPlannedRoute();

    /**
     * Get all possible rest locations that are on or close to the planned route
     *
     * @return a list of rest locations close to the planned route
     * @required that a final destination is set
     */
    public List<Location> getRestLocationsAlongPlannedRoute();

    /**
     * Deletes final destination and all checkpoints
     */
    public void resetRoute();

    /**
     * Get Estimated Time of Arrival to specified location
     *
     * @param location that Estimated Time of Arrival is needed upon
     * @return Estimated Time of Arrival to target location
     */
    public Duration getETA(Location location);

    /**
     * Get the time it will take to drive to the final destination
     *
     * @return time it will take to drive to the final destination
     */
    public Duration getETAFinalDestination();
}