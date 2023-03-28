package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          21/03/2023
 */


import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents an airborne position ADS-B message
 *
 * @param timeStampNs horodatage, in nanoseconds
 * @param icaoAddress the ICAO address of the sender of the message
 * @param altitude    altitude of the aircraft at the moment of sending the message, in meters
 * @param parity      0 if the message is even, 1 if it's odd
 * @param x           local and normalized longitude at the moment of sending the message
 * @param y           local and normalized latitude at the moment of sending the message
 * @author Eva Mangano 345375
 */
public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x,
                                      double y) implements Message {


    /**
     * Base altitude for an odd message
     */
    public static final int BASE_ALTITUDE_1 = -1000;
    /**
     * Base altitude for an even message
     */
    public static final int BASE_ALTITUDE_0 = -1300;
    /**
     * Size of an ME attribute
     */
    private static final int ME_SIZE = 12;


    /**
     * Constructor. Builds an instance of <code>AirbornePositionMessage</code>
     *
     * @param timeStampNs horodatage, in nanoseconds
     * @param icaoAddress ICAO address of the sender of the message
     * @param altitude    altitude of the aircraft at the moment of sending the message, in meters
     * @param parity      0 if the message is even, 1 if it's odd
     * @param x           local and normalized longitude of the aircraft at the moment of sending the message
     * @param y           local and normalized latitude of the aircraft at the moment of sending the message
     * @throws NullPointerException     if the ICAO address is null
     * @throws IllegalArgumentException if the horodatage is negative, the parity is invalid or the longitude and
     *                                  latitude aren't normalized
     * @author Eva Mangano 345375
     */
    public AirbornePositionMessage {
        if ( icaoAddress == null ) {
            throw new NullPointerException();
        }
        Preconditions.checkArgument(
                timeStampNs >= 0 && ( parity == 0 || parity == 1 ) && x >= 0 && x < 1 && y >= 0 && y < 1 );
    }


    /**
     * Builds an instance of <code>AirbornePositionMessage</code> from a <code>RawMessage</code>
     *
     * @param rawMessage the ADS-B message
     * @return an instance of <code>AirbornePositionMessage</code> as given by the <code>RawMessage</code>, null if the
     * altitude is invalid
     * @author Eva Mangano 345375
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {

        double alt = computeAltitude( rawMessage );

        if ( Double.isNaN( alt ) ) {
            return null;
        }

        long timeStampsNs = rawMessage.timeStampNs();

        IcaoAddress icaoAddress = rawMessage.icaoAddress();

        int parity = Bits.extractUInt( rawMessage.payload(), 34, 1 );

        double longitude = Bits.extractUInt( rawMessage.payload(), 0, 17 );
        longitude = normalize( longitude );

        double latitude = Bits.extractUInt( rawMessage.payload(), 17, 17 );
        latitude = normalize( latitude );

        return new AirbornePositionMessage( timeStampsNs, icaoAddress, alt, parity, longitude, latitude );
    }


    private static double normalize(double value) {
        return value * Math.scalb( 1d, -17 );
    }


    private static double computeAltitude(RawMessage rawMessage) {

        double alt;
        int attributeALT = Bits.extractUInt( rawMessage.payload(), 36, 12 );
        /*System.out.println( rawMessage );
        System.out.println( rawMessage.payload() );
        System.out.println( attributeALT );

         */
        int q = Bits.extractUInt( rawMessage.payload(), 40, 1 );

        if ( q == 1 ) {
            int msBits = Bits.extractUInt( attributeALT, 5, 7 );
            int lsBits = Bits.extractUInt( attributeALT, 0, 4 );
            int multipleOf25 = lsBits | ( msBits << 4 );

            alt = BASE_ALTITUDE_1 + multipleOf25 * 25L;

            return Units.convertFrom( alt, Units.Length.FOOT );
        }

        int untangled = untangle( attributeALT );
        System.out.println( untangled );

        int msBits = Bits.extractUInt( untangled, 3, 9 );
        int lsBits = Bits.extractUInt( untangled, 0, 3 );

        msBits = decodeGray( msBits, 9 );
        lsBits = decodeGray( lsBits, 3 );

        if ( lsBits == 0 || lsBits == 5 || lsBits == 6 ) {
            return Double.NaN;
        }
        if ( lsBits == 7 ) {
            lsBits = 5;
        }
        if ( msBits % 2 != 0 ) {
            lsBits = mirrorBits( lsBits );
        }

        alt = BASE_ALTITUDE_0 + lsBits * 100L + msBits * 500L;

        return Units.convertFrom( alt, Units.Length.FOOT );
    }


    private static int mirrorBits(int bits) {
        return 6 - bits;
    }


    private static int decodeGray(int value, int n) {

        int binaryValue = value;

        for ( int i = 1 ; i < n ; i++ ) {
            binaryValue = binaryValue ^ ( value >> i );
        }

        return binaryValue;
    }


    private static int untangle(int attributeALT) {
        int[] tangled = new int[]{Bits.extractUInt( attributeALT, 2, 1 ), Bits.extractUInt( attributeALT, 0, 1 ),
                                  Bits.extractUInt( attributeALT, 10, 1 ), Bits.extractUInt( attributeALT, 8, 1 ),
                                  Bits.extractUInt( attributeALT, 6, 1 ), Bits.extractUInt( attributeALT, 5, 1 ),
                                  Bits.extractUInt( attributeALT, 3, 1 ), Bits.extractUInt( attributeALT, 1, 1 ),
                                  Bits.extractUInt( attributeALT, 11, 1 ), Bits.extractUInt( attributeALT, 9, 1 ),
                                  Bits.extractUInt( attributeALT, 7, 1 )};

        int untangled = 0;
        for ( int i = 0 ; i < ME_SIZE - 1 ; i += 1 ) {
            untangled = ( untangled ) | ( tangled[i] << ( ME_SIZE - 2 - i ) );
        }
        return untangled;
    }
}
