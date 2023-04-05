package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          25/03/2023
 */


import ch.epfl.javions.GeoPos;

import java.util.Objects;

/**
 * Accumulator of a single aircraft state
 * @author Eva Mangano 345375
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private static final int EVEN_MESSAGE = 0;
    private static final int ODD_MESSAGE = 1;
    private static final long MAX_TIMESTAMP_DIFF = (long)1e10;
    private T stateSetter;
    private AirbornePositionMessage evenMessage;
    private AirbornePositionMessage oddMessage;


    /**
     * Constructor. Builds an instance of <code>AircraftStateAccumulator</code>
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


    /**
     * Returns the modifiable state of the instance
     * @return the state setter of the instance
     * @author Eva Mangano 345375
     */
    public T stateSetter() {
        return stateSetter;
    }


    /**
     * Updates the state of the aircraft depending on the given <code>Message</code>
     * @param message message
     * @author Eva Mangano 345375
     */
    public void update(Message message) {

        stateSetter.setLastMessageTimeStampNs( message.timeStampNs() );

        switch ( message ) {
            case AirbornePositionMessage positionMessage -> {
                updatePositionMessage( message, positionMessage );
            }
            case AircraftIdentificationMessage identificationMessage -> {
                updateIdentificationMessage( identificationMessage );
            }
            case AirborneVelocityMessage velocityMessage -> {
                updateVelocityMessage( velocityMessage );
            }
            default -> {
                throw new Error();
            }
        }
    }


    private void updateVelocityMessage(AirborneVelocityMessage velocityMessage) {
        stateSetter.setVelocity( velocityMessage.speed() );
        stateSetter.setTrackOrHeading( velocityMessage.trackOrHeading() );
    }


    private void updateIdentificationMessage(AircraftIdentificationMessage identificationMessage) {
        stateSetter.setCategory( identificationMessage.category() );
        stateSetter.setCallSign( identificationMessage.callSign() );
    }


    private void updatePositionMessage(Message message, AirbornePositionMessage positionMessage) {
        storeMessage( positionMessage );
        stateSetter.setAltitude( positionMessage.altitude() );
        boolean isEven = isEven( positionMessage );
        GeoPos position = null;

        if ( isEven && oddMessage != null
             && ( message.timeStampNs() - oddMessage.timeStampNs() ) <= MAX_TIMESTAMP_DIFF ) {
            position = CprDecoder.decodePosition( positionMessage.x(), positionMessage.y(), oddMessage.x(),
                                                  oddMessage.y(), EVEN_MESSAGE );
        }
        else if ( !isEven && evenMessage != null
                  && ( message.timeStampNs() - evenMessage.timeStampNs() ) <= MAX_TIMESTAMP_DIFF ) {
            position = CprDecoder.decodePosition( evenMessage.x(), evenMessage.y(), positionMessage.x(),
                                                  positionMessage.y(), ODD_MESSAGE );
        }
        
        if ( Objects.nonNull( position ) ) {
            stateSetter.setPosition( position );
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
}
