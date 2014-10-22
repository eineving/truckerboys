package truckerboys.otto.stats;

import java.util.ArrayList;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsModel{
    private long timeDrivenToday;
    private long timeDrivenTotal;
    private double timeToday, fuelToday, distanceByFuel;
    private double timeTotal, distanceTotal, fuelTotal, fuelByDistanceTotal;
    private int violations;

    ArrayList<String> sessionHistory = new ArrayList<String>();

    private String fuelUnit = "L";
    private String distanceUnit = "Km";

    public StatsModel() {

    }

    /**
     * Restores statistics
     * @param statsToday todays stats
     * @param statsTotal all-time stats
     * @param violations all-time violations
     */
    public void setStats(double[] statsToday, double[] statsTotal, int violations) {

        timeToday = statsToday[0];
        // distanceToday = statsToday[1];
        // fuelToday = statsToday[2];
        distanceByFuel = statsToday[3];

        timeTotal = statsTotal[0];
        timeDrivenTotal = (int)statsTotal[0];
        distanceTotal = statsTotal[1];
        fuelTotal = statsTotal[2];
        // fuelByDistanceTotal = statsTotal[3];

        this.violations = violations;
    }


    /**
     * Convert units based on system
     * @param system metric/imperial
     */
    public void setUnits(String system) {
        if(system.equals("imperial")) {
            fuelUnit = "Gallon";
            distanceUnit = "Miles";

            // fuelToday = fuelToday * 0.264172052;
            fuelTotal = fuelTotal * 0.264172052;

            // distanceToday = distanceToday * 0.621371192;
            distanceTotal = distanceTotal * 0.621371192;

            distanceByFuel = distanceByFuel / 0.264172052 * 0.621371192;
            // fuelByDistanceTotal = fuelByDistanceTotal * 0.264172052 / 0.621371192;

        } else {
            // TODO convert ints to Liters and Km
            fuelUnit = "L";
            distanceUnit = "Km";

            fuelTotal = fuelTotal / 0.264172052;
            // fuelToday = fuelToday / 0.264172052;


            // distanceToday = distanceToday / 0.621371192;
            distanceTotal = distanceTotal / 0.621371192;
            distanceByFuel = distanceByFuel * 0.264172052 / 0.621371192;
            // fuelByDistanceTotal = fuelByDistanceTotal / 0.264172052 * 0.621371192;
        }


    }

    /**
     * Updates the list of previous sessions.
     * @param sessionString data from previous sessions.
     */
    public void updateSessionHistory(String sessionString) {
        sessionHistory.add(sessionString);

    }

    public ArrayList<String> getSessionHistory() {
        return sessionHistory;
    }

    public double getTimeToday() {
        return timeToday;
    }

    public double getFuelToday() {
        return fuelToday;
    }

    public double getDistanceByFuel() {
        return distanceByFuel;
    }

    public double getFuelTotal() {
        return fuelTotal;
    }



    public double getDistanceTotal() {
        return distanceTotal;
    }

    public double getfuelByDistanceTotal() {
        return fuelByDistanceTotal;
    }

    public double getTimeTotal() {
        return timeTotal;
    }

    public int getViolations() {
        return violations;
    }

}
