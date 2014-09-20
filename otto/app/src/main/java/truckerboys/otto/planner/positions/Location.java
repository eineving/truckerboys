package truckerboys.otto.planner.positions;

/**
 * Created by Daniel on 2014-09-18.
 */
public abstract class Location {
    private double longitude;
    private double latitude;

    //Address
    private String country;
    private String city;
    private String zipCode;
    private String street;
    private String streetNumber;

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }
}
