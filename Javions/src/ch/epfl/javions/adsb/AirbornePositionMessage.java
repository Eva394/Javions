package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          21/03/2023
 */


import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represents an airborne position ADS-B message
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


    private static final int ALT_START = 36;
    private static final int ALT_SIZE = 12;
    private static final int Q_START = 40;
    private static final int Q_SIZE = 1;
    private static final int MSB1_START = 5;
    private static final int MSB1_SIZE = 7;
    private static final int LSB1_START = 0;
    private static final int LSB1_SIZE = 4;
    private static final int BASE_ALTITUDE_1 = -1000;
    private static final int BASE_ALTITUDE_0 = -1300;
    private static final int ME_SIZE = 12;
    private static final int PARITY_START = 34;
    private static final int PARITY_SIZE = 1;
    private static final int LONGITUDE_START = 0;
    private static final int LONGITUDE_SIZE = 17;
    private static final int LATITUDE_START = 17;
    private static final int LATITUDE_SIZE = 17;
    private static final int MSB0_START = 3;
    private static final int MSB0_SIZE = 9;
    private static final int LSB0_START = 0;
    private static final int LSB0_SIZE = 3;


    /**
     * Constructor. Builds an instance of <code>AirbornePositionMessage</code>
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
        Objects.requireNonNull( icaoAddress );
        checkArguments( timeStampNs, parity, x, y );
    }


    /**
     * Builds an instance of <code>AirbornePositionMessage</code> from a <code>RawMessage</code>
     * @param rawMessage the ADS-B message
     * @return an instance of <code>AirbornePositionMessage</code> as given by the <code>RawMessage</code>, null if the
     * altitude is invalid
     * @author Eva Mangano 345375
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {

        long payload = rawMessage.payload();
        double alt = computeAltitude( payload );
        if ( Double.isNaN( alt ) ) {
            return null;
        }

        long timeStampsNs = rawMessage.timeStampNs();

        IcaoAddress icaoAddress = rawMessage.icaoAddress();

        int parity = Bits.extractUInt( payload, PARITY_START, PARITY_SIZE );

        double longitude = Bits.extractUInt( payload, LONGITUDE_START, LONGITUDE_SIZE );
        longitude = normalize( longitude );

        double latitude = Bits.extractUInt( payload, LATITUDE_START, LATITUDE_SIZE );
        latitude = normalize( latitude );

        return new AirbornePositionMessage( timeStampsNs, icaoAddress, alt, parity, longitude, latitude );
    }


    private static void checkArguments(long timeStampNs, int parity, double x, double y) {
        Preconditions.checkArgument(
                timeStampNs >= 0 && ( parity == 0 || parity == 1 ) && x >= 0 && x < 1 && y >= 0 && y < 1 );
    }


    private static double computeAltitude(long rawMessagePayload) {
        double alt;
        int attributeALT = Bits.extractUInt( rawMessagePayload, ALT_START, ALT_SIZE );

        int q = Bits.extractUInt( rawMessagePayload, Q_START, Q_SIZE );

        if ( q == 1 ) {
            alt = computeQ1Altitude( attributeALT );
        }
        else {
            int untangled = untangle( attributeALT );
            int msBits = computeMSBorLSB( untangled, MSB0_START, MSB0_SIZE );
            int lsBits = computeMSBorLSB( untangled, LSB0_START, LSB0_SIZE );

            switch ( lsBits ) {
                case 0, 5, 6 -> {
                    return Double.NaN;
                }
                case 7 -> {
                    lsBits = 5;
                }
            }

            if ( msBits % 2 != 0 ) {
                lsBits = mirrorBits( lsBits );
            }

            alt = BASE_ALTITUDE_0 + lsBits * 100L + msBits * 500L;
        }

        return Units.convertFrom( alt, Units.Length.FOOT );
    }


    private static double computeQ1Altitude(int untangledAlt) {
        double alt;
        int msBits = Bits.extractUInt( untangledAlt, MSB1_START, MSB1_SIZE );
        int lsBits = Bits.extractUInt( untangledAlt, LSB1_START, LSB1_SIZE );
        int multipleOf25 = lsBits | ( msBits << LSB1_SIZE );

        alt = BASE_ALTITUDE_1 + multipleOf25 * 25L;

        return alt;
    }

    private static int untangle(int attributeALT) {
        int untangled = 0;
        int[] order = {2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};
        for (int i = 0; i < order.length; i++) {
            untangled |= (Bits.extractUInt(attributeALT, order[i], 1) << (ME_SIZE - 2 - i));
        }
        return untangled;
    }

    /*

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

     */


    private static int computeMSBorLSB(int attributeALT, int start, int size) {
        int bits = Bits.extractUInt( attributeALT, start, size );
        bits = decodeGray( bits, size );
        return bits;
    }


    private static int decodeGray(int value, int n) {
        int binaryValue = value;

        for ( int i = 1 ; i < n ; i++ ) {
            binaryValue = binaryValue ^ ( value >> i );
        }

        return binaryValue;
    }


    private static int mirrorBits(int bits) {
        return 6 - bits;
    }


    private static double normalize(double value) {
        return value * Math.scalb( 1d, -17 );
    }
}
