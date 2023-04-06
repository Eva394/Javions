package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          25/03/2023
 */



/**
 * Transforms the raw ADS-B messages into an <code>AircraftIdentificationMessage</code>, an
 * <code>AirbornePositionMessage</code> or an <code>AirborneVelocityMessage</code>
 */
public class MessageParser {

    private MessageParser() {

    }


    /**
     * Transforms the <code>RawMessage</code> into an <code>AircraftIdentificationMessage</code>, an
     * <code>AirbornePositionMessage</code> or an <code>AirborneVelocityMessage</code>
     * @param rawMessage the raw ADS-B message
     * @return an instance of <code>AircraftIdentificationMessage</code>, an <code>AirbornePositionMessage</code> or an
     * <code>AirborneVelocityMessage</code> depending on the type code, or null if the type code or the message is
     * invalid
     */
    public static Message parse(RawMessage rawMessage) {

        int typeCode = rawMessage.typeCode();
        switch ( typeCode ) {
            case 1, 2, 3, 4 -> {
                return AircraftIdentificationMessage.of( rawMessage );
            }
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> {
                return AirbornePositionMessage.of( rawMessage );
            }
            case 19 -> {
                return AirborneVelocityMessage.of( rawMessage );
            }
            default -> {
                return null;
            }
        }
    }
}
