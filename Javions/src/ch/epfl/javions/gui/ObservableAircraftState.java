package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          17/04/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents the state of an aircraft, which is observable
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private final ObservableList<AirbornePos> trajectory;
    private final ObservableList<AirbornePos> unmodifiableTrajectory;
    private final LongProperty lastMessageTimeStampNs;
    private final IntegerProperty category;
    private final ObjectProperty<CallSign> callSign;
    private final ObjectProperty<GeoPos> position;
    private final DoubleProperty altitude;
    private final DoubleProperty velocity;
    private final DoubleProperty trackOrHeading;
    private long lastTrajectoryUpdateTimeStampNs;


    /**
     * Constructor. Builds an instance of <code>ObservableAircraftState</code>
     *
     * @param icaoAddress  the ICAO address of the aircraft
     * @param aircraftData the fixed data of the aircraft
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        Preconditions.checkArgument( icaoAddress != null );

        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;

        trajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList( trajectory );
        lastMessageTimeStampNs = new SimpleLongProperty( -1L );
        category = new SimpleIntegerProperty( -1 );
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty( Double.NaN );
        velocity = new SimpleDoubleProperty( Double.NaN );
        trackOrHeading = new SimpleDoubleProperty( Double.NaN );
    }


    /**
     * Getter for an unmodifiable wrapper list on top of the list <code>trajectory</code>
     * (<code>unmodifiableTrajectory</code>)
     *
     * @return an unmodifiable wrapper list on top of observable list of trajectories <code>trajectory</code>
     */
    public ObservableList<AirbornePos> getUnmodifiableTrajectory() {
        return unmodifiableTrajectory;
    }


    /**
     * Getter for the horodatage of the last message (<code>lastMessageTimeStampNs</code>)
     *
     * @return the horodatage of the last message in nanoseconds
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }


    /**
     * Setter for the horodatage of the last message (<code>lastMessageTimeStampNs</code>)
     *
     * @param lastMessageTimeStampNs new horodatage in nanoseconds
     */
    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set( lastMessageTimeStampNs );
    }


    /**
     * Getter for the category of the aircraft (<code>category</code>)
     *
     * @return the category of the aircraft
     */
    public int getCategory() {
        return category.get();
    }


    /**
     * Setter for the category of the aircraft (<code>category</code>)
     *
     * @param category new category
     */
    public void setCategory(int category) {
        this.category.set( category );
    }


    /**
     * Getter for the ICAO address of the aircraft (<code>icaoAddress</code>)
     *
     * @return the icao address of the aircraft
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }


    /**
     * Getter for the fixed data of the aircraft (<code>aircraftData</code>)
     *
     * @return the fixed data of the aircraft
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }


    /**
     * Getter for the call sign of the aircraft (<code>callSign</code>)
     *
     * @return the call sign of the aircraft
     */
    public CallSign getCallSign() {
        return callSign.get();
    }


    /**
     * Setter for the call sign of the aircraft (<code>callSign</code>)
     *
     * @param callSign new call sign
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set( callSign );
    }


    /**
     * Getter for the position of the aircraft (<code>position</code>)
     *
     * @return the position of the aircraft (<code>position</code>)
     */
    public GeoPos getPosition() {
        return position.get();
    }


    /**
     * Setter for the position of the aircraft (<code>position</code>). Updates the trajectory
     *
     * @param pos new position
     */
    @Override
    public void setPosition(GeoPos pos) {
        position.set( pos );
        updateTrajectoryWithPosition();
    }


    /**
     * Getter for the altitude of the aircraft (<code>altitude</code>)
     *
     * @return the altitude of the aircraft (<code>altitude</code>)
     */
    public double getAltitude() {
        return altitude.get();
    }


    /**
     * Setter for the altitude of the aircraft (<code>altitude</code>). Updates the trajectory
     *
     * @param alt new altitude
     */
    public void setAltitude(double alt) {
        this.altitude.set( alt );
        updateTrajectoryWithAltitude();
    }


    /**
     * Getter for the velocity of the aircraft (<code>velocity</code>)
     *
     * @return the velocity of the aircraft
     */
    public double getVelocity() {
        return velocity.get();
    }


    /**
     * Setter for the velocity of the aircraft (<code>velocity</code>)
     *
     * @param velocity new velocity
     */
    public void setVelocity(double velocity) {
        this.velocity.set( velocity );
    }


    /**
     * Getter for the track or heading of the aircraft (<code>trackOrHeading</code>)
     *
     * @return the track or heading of the aircraft
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }


    /**
     * Setter for the track or heading of the aircraft (<code>trackOrHeading</code>)
     *
     * @param trackOrHeading new track or heading
     */
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set( trackOrHeading );
    }


    /**
     * Read-only getter for the horodatage ({@linkplain ObservableAircraftState#getLastMessageTimeStampNs()})
     *
     * @return the horodatage of the last message property
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }


    /**
     * Read-only getter for the category ({@linkplain ObservableAircraftState#getCategory()})
     *
     * @return the category property
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }


    /**
     * Read-only getter for the position ({@linkplain ObservableAircraftState#getPosition()})
     *
     * @return the position property
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }


    /**
     * Read-only getter for the altitude ({@linkplain ObservableAircraftState#getAltitude()})
     *
     * @return the altitude property
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }


    /**
     * Read-only getter for the velocity ({@linkplain ObservableAircraftState#getVelocity()})
     *
     * @return the velocity property
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }


    /**
     * Read-only getter for the track or heading ({@linkplain ObservableAircraftState#getTrackOrHeading()})
     *
     * @return the track or heading property
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }


    /**
     * Read-only getter for the call sign ({@linkplain ObservableAircraftState#callSign })
     *
     * @return the callsign
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    //    private void updateTrajectory() {
    //        GeoPos geoPos = position.get();
    //        double alt = altitude.get();
    //
    //        AirbornePos airbornePos = new AirbornePos( geoPos, alt );
    //
    //        if ( !Double.isNaN( altitude.get() ) ) {
    //            trajectory.add( airbornePos );
    //        }
    //        if ( !Objects.isNull( geoPos ) && !Double.isNaN( geoPos.longitude() ) && !Double.isNaN( geoPos.latitude() ) ) {
    //            if ( trajectory.isEmpty() ) {
    //                trajectory.add( airbornePos );
    //            }
    //            else if ( lastTrajectoryUpdateTimeStampNs == lastMessageTimeStampNs.get() ) {
    //                trajectory.set( trajectory.size() - 1, airbornePos );
    //            }
    //        }
    //    }


    private void updateTrajectoryWithPosition() {
        if ( !Double.isNaN( altitude.get() ) ) {
            trajectory.add( new AirbornePos( position.get(), altitude.get() ) );
        }
    }


    private void updateTrajectoryWithAltitude() {
        if ( position.get() != null ) {
            if ( trajectory.isEmpty() ) {
                trajectory.add( new AirbornePos( position.get(), altitude.get() ) );
            }
            else if ( lastTrajectoryUpdateTimeStampNs == lastMessageTimeStampNs.get() ) {
                trajectory.set( trajectory.size() - 1, new AirbornePos( position.get(), altitude.get() ) );
            }
        }
    }


    /**
     * Represents the airborne position of the aircraft (longitude, latitude and altitude)
     *
     * @param position longitude and latitude of the aircraft
     * @param altitude altitude of the aircraft
     */
    public record AirbornePos(GeoPos position, double altitude) {

    }
}

