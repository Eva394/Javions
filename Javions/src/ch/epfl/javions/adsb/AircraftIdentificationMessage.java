package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Aircraft Indentification Message
 * @author Nagyung KIM (339628)
 */

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final String ELEMENTS = "-ABCDEFGHIJKLMNOPQRSTUVWXYZ----- ---------------0123456789------";

    /**
     *
     * @param timeStampNs horodatage in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param category the shipper's aircraft category
     * @param callSign the sender's code
     */

    public AircraftIdentificationMessage {
        Preconditions.checkArgument( timeStampNs >= 0 );

        if ( icaoAddress == null || callSign == null ) {
            throw new NullPointerException();
        }
    }

    /**
     *
     * @param rawMessage the ADS-B message
     * @return the identification message corresponding to the given raw message,
     * or null if at least one of the characters of the code it contains is invalid
     */

    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        int typeCode = rawMessage.typeCode();


        int CA = Bits.extractUInt( rawMessage.payload(), 48, 3 );
        int temp = 0xE - typeCode;
        int category = ( temp << 4 ) | CA;


        StringBuilder cS = new StringBuilder( 6 );

        for ( int i = 0 ; i < 8 ; i++ ) {

            int character = Bits.extractUInt( rawMessage.payload(), 6 * i, 6 );
            char c = ELEMENTS.charAt( character );
            if ( !( c == ' ' ) ) {
                cS.append( c );
            }

        }

        String callSignStr = cS.reverse()
                               .toString();

        if ( callSignStr.contains( "-" ) ) {
            return null;
        }

        CallSign callSign = new CallSign( callSignStr );

        return new AircraftIdentificationMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), category,
                                                  callSign );
    }
}

