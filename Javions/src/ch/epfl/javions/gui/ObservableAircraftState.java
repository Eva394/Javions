package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          17/04/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
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

    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private final ObservableList<AirbonePos> trajectory;
    private final ObservableList<AirbonePos> unmodifiableTrajectory;
    private final LongProperty lastMessageTimeStampNs;
    private final IntegerProperty category;
    private final ObjectProperty<GeoPos> position;
    private final DoubleProperty altitude;
    private final DoubleProperty velocity;
    private final DoubleProperty trackOrHeading;
    private long lastTrajectoryUpdateTimeStampNs;


    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        Preconditions.checkArgument( icaoAddress != null );
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
        trajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList( trajectory );
        lastMessageTimeStampNs = new SimpleLongProperty( -1L );
        category = new SimpleIntegerProperty( -1 );
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty( -1. );
        velocity = new SimpleDoubleProperty( -1. );
        trackOrHeading = new SimpleDoubleProperty( -1. );
    }


    public ObservableList<AirbonePos> getUnmodifiableTrajectory() {
        return unmodifiableTrajectory;
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


    @Override
    public void setPosition(GeoPos pos) {
        updateTrajectory( new SimpleObjectProperty<>( pos ), altitude );
        this.position.set( pos );
    }


    public double getAltitude() {
        return altitude.get();
    }


    public void setAltitude(double alt) {
        updateTrajectory( position, new SimpleDoubleProperty( alt ) );
        this.altitude.set( alt );
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


    private void updateTrajectory(ObjectProperty<GeoPos> position, DoubleProperty altitude) {
        if ( this.position.get() != trajectory.get( trajectory.size() - 1 )
                                              .position() || trajectory.isEmpty() ) {
            trajectory.add( new AirbonePos( new GeoPos( (int)Units.convertTo( position.get()
                                                                                      .longitude(), Units.Angle.T32 ),
                                                        (int)Units.convertTo( position.get()
                                                                                      .latitude(), Units.Angle.T32 ) ),
                                            altitude ) );
        }
        if ( lastTrajectoryUpdateTimeStampNs == lastMessageTimeStampNs.get() ) {
            trajectory.set( trajectory.size() - 1, new AirbonePos( new GeoPos( (int)Units.convertTo( position.get()
                                                                                                             .longitude(),
                                                                                                     Units.Angle.T32 ),
                                                                               (int)Units.convertTo( position.get()
                                                                                                             .latitude(),
                                                                                                     Units.Angle.T32 ) ),
                                                                   altitude ) );
        }
    }


    private record AirbonePos(GeoPos position, DoubleProperty altitude) {
    }
}
