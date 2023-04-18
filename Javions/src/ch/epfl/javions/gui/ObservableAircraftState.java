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
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Observable;

/**
 * Represents the state of an aircraft, which is observable
 */
public final class ObservableAircraftState extends Observable implements AircraftStateSetter {

    private ObservableList<AirbonePos> trajectory;
    private ObservableList<AirbonePos> unmodifiableTrajectory;
    private LongProperty lastMessageTimeStampNs;
    private IntegerProperty category;
    private ObjectProperty<GeoPos> position;
    private DoubleProperty altitude;
    private DoubleProperty velocity;
    private DoubleProperty trackOrHeading;


    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        trajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList( trajectory );
        lastMessageTimeStampNs;
        category;
        position;
    }


    public ObservableList<AirbonePos> getUnmodifiableTrajectory() {
        return unmodifiableTrajectory;
    }


    public void setTrajectory(ObservableList<AirbonePos> trajectory) {
        this.trajectory = trajectory;
    }


    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }


    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set( lastMessageTimeStampNs );
    }


    public int getCategory() {
        return category.get();
    }


    public void setCategory(int category) {
        this.category.set( category );
    }


    @Override
    public void setCallSign(CallSign callSign) {

    }


    public GeoPos getPosition() {
        return position.get();
    }


    public void setPosition(GeoPos position) {
        this.position.set( position );
    }


    public double getAltitude() {
        return altitude.get();
    }


    public void setAltitude(double altitude) {
        this.altitude.set( altitude );
    }


    public double getVelocity() {
        return velocity.get();
    }


    public void setVelocity(double velocity) {
        this.velocity.set( velocity );
    }


    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }


    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set( trackOrHeading );
    }


    public ReadOnlyListProperty<AirbonePos> unmodifiableTrajectoryProperty() {
        return (ReadOnlyListProperty<AirbonePos>)unmodifiableTrajectory;
    }


    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }


    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }


    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }


    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }


    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }


    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }


    private record AirbonePos(GeoPos position, double altitude) {
    }
}
