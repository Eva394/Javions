package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          17/04/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.List;

/**
 * Represents the state of an aircraft, which is observable
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    private List<AirbonePos> positions; //TODO idk??? + ot has to be immuable
    private long lastMessageTimeStampNs;
    private int category;
    private CallSign callSign;
    private double position;
    private double altitude;
    private double velocity;
    private double trackOrHeading;


    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        //AircraftRegistration aircraftRegistration, AircraftTypeDesignator aircraftTypeDesignator,AircraftDescription aircraftDescription,
        //TODO idk what to do here ???
    }


    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs;
    }


    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {

    }


    public int getCategory() {
        return category;
    }


    @Override
    public void setCategory(int category) {

    }


    public CallSign getCallSign() {
        return callSign;
    }


    @Override
    public void setCallSign(CallSign callSign) {

    }


    public double getPosition() {
        return position;
    }


    @Override
    public void setPosition(GeoPos position) {

    }


    public List<AirbonePos> getPositions() {
        return positions;
    }


    public double getAltitude() {
        return altitude;
    }


    @Override
    public void setAltitude(double altitude) {

    }


    public double getVelocity() {
        return velocity;
    }


    @Override
    public void setVelocity(double velocity) {

    }


    public double getTrackOrHeading() {
        return trackOrHeading;
    }


    @Override
    public void setTrackOrHeading(double trackOrHeading) {

    }


    private record AirbonePos(double[] position, double altitude) {
    }
}
