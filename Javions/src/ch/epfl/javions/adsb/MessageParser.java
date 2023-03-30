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
     *
     * @param rawMessage the raw ADS-B message
     * @return an instance of <code>AircraftIdentificationMessage</code>, an <code>AirbornePositionMessage</code> or an
     * <code>AirborneVelocityMessage</code> depending on the type code, or null if the type code or the message is
     * invalid
     */
    public static Message parse(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();
        //TODO switch

        //        switch ( typeCode ) {
        //            case typeCode == 1 || typeCode == 2 || typeCode == 3 || typeCode == 4 ->
        //            return AircraftIdentificationMessage.of( rawMessage );
        //
        //        }

        if ( isIdentificationMessage( typeCode ) ) {
            return AircraftIdentificationMessage.of( rawMessage );
        }
        if ( isPositionMessage( typeCode ) ) {
            return AirbornePositionMessage.of( rawMessage );
        }
        if ( isVelocityMessage( typeCode ) ) {
            return AirborneVelocityMessage.of( rawMessage );
        }
        return null;
    }


    private static boolean isIdentificationMessage(int typeCode) {
        return typeCode == 1 || typeCode == 2 || typeCode == 3 || typeCode == 4;
    }


    private static boolean isVelocityMessage(int typeCode) {
        return typeCode == 19;
    }


    private static boolean isPositionMessage(int typeCode) {
        return ( 9 <= typeCode && typeCode <= 18 ) || ( 20 <= typeCode && typeCode <= 22 );
    }
}
