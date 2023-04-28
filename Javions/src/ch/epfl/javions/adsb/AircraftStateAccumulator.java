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
    private final T stateSetter;
    private final AirbornePositionMessage[] lastOddAndEvenPositionMessages = new AirbornePositionMessage[]{null, null};


    /**
     * Constructor. Builds an instance of <code>AircraftStateAccumulator</code>
     * @param stateSetter the state setter
     * @throws NullPointerException if the state setter is null
     * @author Eva Mangano 345375
     */
    public AircraftStateAccumulator(T stateSetter) {
        this.stateSetter = Objects.requireNonNull( stateSetter );
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
                updatePositionMessage( positionMessage );
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


    /**
     * Returns the modifiable state of the instance
     * @return the state setter of the instance
     * @author Eva Mangano 345375
     */
    public T stateSetter() {
        return stateSetter;
    }


    private void updatePositionMessage(AirbornePositionMessage positionMessage) {
        lastOddAndEvenPositionMessages[positionMessage.parity()] = positionMessage;
        stateSetter.setAltitude( positionMessage.altitude() );
        boolean isEven = isEven( positionMessage );
        GeoPos position = null;

        if ( isEven && lastOddAndEvenPositionMessages[1] != null
             && ( positionMessage.timeStampNs() - lastOddAndEvenPositionMessages[1].timeStampNs() )
                <= MAX_TIMESTAMP_DIFF ) {
            position = CprDecoder.decodePosition( lastOddAndEvenPositionMessages[0].x(),
                                                  lastOddAndEvenPositionMessages[0].y(),
                                                  lastOddAndEvenPositionMessages[1].x(),
                                                  lastOddAndEvenPositionMessages[1].y(), EVEN_MESSAGE );
        }
        else if ( !isEven && lastOddAndEvenPositionMessages[0] != null
                  && ( positionMessage.timeStampNs() - lastOddAndEvenPositionMessages[0].timeStampNs() )
                     <= MAX_TIMESTAMP_DIFF ) {
            position = CprDecoder.decodePosition( lastOddAndEvenPositionMessages[0].x(),
                                                  lastOddAndEvenPositionMessages[0].y(), positionMessage.x(),
                                                  positionMessage.y(), ODD_MESSAGE );
        }
        if ( Objects.nonNull( position ) ) {
            stateSetter.setPosition( position );
        }
    }


    private GeoPos getPosition(Message message, AirbornePositionMessage positionMessage, int parity) {
        return lastOddAndEvenPositionMessages[parity] != null
               && ( message.timeStampNs() - lastOddAndEvenPositionMessages[parity].timeStampNs() ) <= MAX_TIMESTAMP_DIFF
               ? CprDecoder.decodePosition( positionMessage.x(), positionMessage.y(),
                                            lastOddAndEvenPositionMessages[parity].x(),
                                            lastOddAndEvenPositionMessages[parity].y(), parity )
               : null;
    }


    private void updateIdentificationMessage(AircraftIdentificationMessage identificationMessage) {
        stateSetter.setCategory( identificationMessage.category() );
        stateSetter.setCallSign( identificationMessage.callSign() );
    }


    private void updateVelocityMessage(AirborneVelocityMessage velocityMessage) {
        stateSetter.setVelocity( velocityMessage.speed() );
        stateSetter.setTrackOrHeading( velocityMessage.trackOrHeading() );
    }


    private boolean isEven(AirbornePositionMessage message) {
        return message.parity() == 0;
    }
}
