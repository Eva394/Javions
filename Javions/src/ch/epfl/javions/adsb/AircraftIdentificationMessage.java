package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * represents an Aircraft Identification Message
 *
 * @author Nagyung KIM (339628)
 */

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final int MSB_BASE = 0xE;
    private static final int CALLSIGN_CHAR_SIZE = 6;
    private static final String ELEMENTS = "-ABCDEFGHIJKLMNOPQRSTUVWXYZ----- ---------------0123456789------";
    private static final int PARTIAL_CATEGORY_START = 48;
    private static final int PARTIAL_CATEGORY_SIZE = 3;


    /**
     * Constructor. Builds an instance of <code>AircraftIdentificationMessage</code>
     *
     * @param timeStampNs horodatage in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param category    the shipper's aircraft category
     * @param callSign    the sender's code
     * @throws IllegalArgumentException if the horodatage is negative
     * @throws NullPointerException     if the ICAO address or the call sign are null
     */
    public AircraftIdentificationMessage {
        Preconditions.checkArgument( timeStampNs >= 0 );
        Objects.requireNonNull( icaoAddress );
        Objects.requireNonNull( callSign );
    }


    /**
     * Builds an instance of <code>AircraftIdentificationMessage</code> from the raw message
     *
     * @param rawMessage the ADS-B message
     * @return an instance of <code>AircraftIdentificationMessage</code> corresponding to the given raw message, or null
     * if at least one of the characters of the code it contains is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int typeCode = rawMessage.typeCode();

        int partialCategory = Bits.extractUInt( payload, PARTIAL_CATEGORY_START, PARTIAL_CATEGORY_SIZE );
        int temp = MSB_BASE - typeCode;
        int category = ( temp << Byte.SIZE / 2 ) | partialCategory;

        StringBuilder cS = new StringBuilder( CALLSIGN_CHAR_SIZE );

        buildCallSignStr( payload, cS );

        String callSignStr = cS.reverse()
                               .toString();

        if ( callSignStr.contains( "-" ) ) {
            return null;
        }

        CallSign callSign = new CallSign( callSignStr );

        return new AircraftIdentificationMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), category,
                                                  callSign );
    }


    private static void buildCallSignStr(long payload, StringBuilder cS) {
        for ( int i = 0 ; i < Byte.SIZE ; i++ ) {
            int character = Bits.extractUInt( payload, CALLSIGN_CHAR_SIZE * i, CALLSIGN_CHAR_SIZE );
            char c = ELEMENTS.charAt( character );
            if ( !( c == ' ' ) ) {
                cS.append( c );
            }
        }
    }
}

