package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          12/03/2023
 */


import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 * Represents a raw ADS-B message (of which the ME hasn't been analysed yet)
 *
 * @param timeStampNs horodatage of the message (in nanoseconds)
 * @param bytes       bytes of the message
 * @author Eva Mangano 345275
 */

public record RawMessage(long timeStampNs, ByteString bytes) {

    /**
     * length of an ADS-B message
     */
    public static final int LENGTH = 14;
    /**
     * downlink format of an ADS-B message
     */
    public static final int DOWNLINK_FORMAT = 17;


    /**
     * Constructor. Builds an instance of RawMessage
     *
     * @param timeStampNs horodatage of the message (in nanoseconds)
     * @param bytes       bytes of the message
     * @author Eva Mangano 345375
     */
    public RawMessage {
        Preconditions.checkArgument( ( timeStampNs >= 0 ) && ( bytes.size() == LENGTH ) );
    }


    /**
     * returns an instance of RawMessage with the parameters passed
     *
     * @param timeStampNs horodatage of the message (in nanoseconds)
     * @param bytes       bytes of the message
     * @return an instance of RawMessage with the parameters passed
     * @author Eva Mangano 345375
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        Crc24 crc24 = new Crc24( Crc24.GENERATOR );

        if ( crc24.crc( bytes ) != 0 ) {
            return null;
        }

        return new RawMessage( timeStampNs, new ByteString( bytes ) );
    }


    /**
     * gives the size of an ADS-B message
     *
     * @param byte0 first byte of the ADS-B message (downlink format)
     * @return the length <code>LENGTH</code> of an ADS-B message if the downlink format is 17, 0 if not
     * @author Eva Mangano 345375
     */
    public static int size(byte byte0) {

        int dfSize = 5;

        if ( Bits.extractUInt( byte0, 0, dfSize ) != 17 ) {
            return 0;
        }

        return LENGTH;
    }


    /**
     * gives the type code of the ME attribute passed
     *
     * @param payload ME attribute
     * @return the type code of the ME attribute
     * @author Eva Mangano 345375
     */
    public static int typeCode(long payload) {
        int typeCodeSize = 5;
        int msbPosition = Long.SIZE - ( Byte.SIZE + typeCodeSize );

        return Bits.extractUInt( payload, msbPosition, typeCodeSize );
    }


    /**
     * gives the downlink format of the instance of ADS-B message
     *
     * @return the downlink format of the instance
     * @author Eva Mangano 345375
     */
    public int downLinkFormat() {
        int dlSize = 5;
        int dlPosition = Byte.SIZE - dlSize;

        return Bits.extractUInt( bytes.byteAt( 0 ), dlPosition, dlSize );
    }


    /**
     * gives the ICAO address of the sender of the ADS-B message
     *
     * @return the ICAO address of the sender
     */
    public IcaoAddress icaoAddress() {

        int numberOfHex = 6;
        long icaoAddressBytes = bytes.bytesInRange( 1, 4 );
        HexFormat hexFormat = HexFormat.of();

        return new IcaoAddress( hexFormat.withUpperCase()
                                         .toHexDigits( icaoAddressBytes, numberOfHex ) );
    }


    /**
     * gives the ME attribute of the ADS-B message
     *
     * @return the ME attribute
     */
    public long payload() {

        return bytes.bytesInRange( 4, 11 );
    }


    /**
     * gives the type code of the ADS-B message
     *
     * @return the type code
     */
    public int typeCode() {
        int typeCodeSize = 5;
        int msbPosition = Long.SIZE - ( Byte.SIZE + typeCodeSize );
        return Bits.extractUInt( payload(), msbPosition, typeCodeSize );
    }
}
