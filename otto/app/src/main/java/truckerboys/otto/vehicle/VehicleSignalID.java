package truckerboys.otto.vehicle;

import android.swedspot.automotiveapi.AutomotiveSignalId;

/**
 * A list of signal IDs.
 * Created by Martin on 23/09/2014.
 */
public class VehicleSignalID {

    public static final int AMBIENT_AIR_TEMPERATURE = AutomotiveSignalId.FMS_AMBIENT_AIR_TEMPERATURE;
    public static final int DRIVER_1_CARD = AutomotiveSignalId.FMS_DRIVER_1_CARD;
    public static final int DRIVER_2_CARD = AutomotiveSignalId.FMS_DRIVER_2_CARD;
    public static final int DRIVER_1_DRIVER_2_ID = AutomotiveSignalId.FMS_DRIVER_1_DRIVER_2_IDENTIFICATION;
    public static final int DRIVER_1_WORKING_STATE = AutomotiveSignalId.FMS_DRIVER_1_WORKING_STATE;
    public static final int DRIVER_2_WORKING_STATE = AutomotiveSignalId.FMS_DRIVER_2_WORKING_STATE;

    public static final int DRIVER_1_TIME_REL_STATE = AutomotiveSignalId.FMS_DRIVER_1_TIME_REL_STATES;
    public static final int DRIVER_2_TIME_REL_STATE = AutomotiveSignalId.FMS_DRIVER_2_TIME_REL_STATES;

    public static final int FUEL_LEVEL_1 = AutomotiveSignalId.FMS_FUEL_LEVEL_1;
    public static final int FUEL_RATE = AutomotiveSignalId.FMS_FUEL_RATE;


    public static final int WHEEL_BASED_SPEED = AutomotiveSignalId.FMS_WHEEL_BASED_SPEED;

}
