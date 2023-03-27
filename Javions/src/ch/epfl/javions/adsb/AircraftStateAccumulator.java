package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          25/03/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

import java.util.Objects;

/**
 * Accumulator of a single aircraft states
 *
 * @author Eva Mangano 345375
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private static final double MAX_TIMESTAMP_DIFF = 1e-9;
    private T stateSetter;
    private Message evenMessage;
    private Message oddMessage;


    /**
     * Constructor. Builds an instance of <code>AircraftStateAccumulator</code>
     *
     * @param stateSetter the state setter
     * @throws NullPointerException if the state setter is null
     * @author Eva Mangano 345375
     */
    public AircraftStateAccumulator(T stateSetter) {
        Objects.requireNonNull( stateSetter );
        this.stateSetter = stateSetter;
    }


    private static boolean isEven(AirbornePositionMessage message) {
        return message.parity() == 0;
    }


    public T stateSetter() {
        return stateSetter;
    }


    /**
     * Updates the state of the aircraft depending on the given <code>Message</code>
     *
     * @param message message
     * @author Eva Mangano 345375
     */
    public void update(Message message) {

        stateSetter.setLastMessageTimeStampNs( message.timeStampNs() );

        switch ( message ) {
            case AirbornePositionMessage positionMessage -> {
                storeMessage( positionMessage );
                stateSetter.setAltitude( positionMessage.altitude() );

                if ( positionAvailable( positionMessage ) ) {
                    double longitude = Units.convert( positionMessage.x(), Units.Angle.TURN, Units.Angle.T32 );
                    double latitude = Units.convert( positionMessage.y(), Units.Angle.TURN, Units.Angle.T32 );
                    GeoPos position = new GeoPos( (int)Math.rint( longitude ), (int)Math.rint( latitude ) );
                    stateSetter.setPosition( position );
                }
            }
            case AircraftIdentificationMessage identificationMessage -> {
                stateSetter.setCategory( identificationMessage.category() );
                stateSetter.setCallSign( identificationMessage.callSign() );
            }
            case AirborneVelocityMessage velocityMessage -> {
                stateSetter.setVelocity( velocityMessage.speed() );
                stateSetter.setTrackOrHeading( velocityMessage.trackOrHeading() );
            }
            default -> {
                throw new Error();
            }
        }
    }


    private void storeMessage(AirbornePositionMessage message) {
        if ( isEven( message ) ) {
            evenMessage = message;
        }
        else {
            oddMessage = message;
        }
    }


    private boolean positionAvailable(AirbornePositionMessage message) {
        long timeStampsDiff = (long)MAX_TIMESTAMP_DIFF + 1;
        boolean isEven = isEven( message );

        if ( isEven && oddMessage != null ) {
            timeStampsDiff = message.timeStampNs() - oddMessage.timeStampNs();
        }
        else if ( !isEven && evenMessage != null ) {
            timeStampsDiff = message.timeStampNs() - evenMessage.timeStampNs();
        }

        return timeStampsDiff <= MAX_TIMESTAMP_DIFF;
    }
}
