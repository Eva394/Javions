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
        Preconditions.checkArgument( ( timeStampNs >= 0 ) || ( bytes.size() == LENGTH ) );
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

        int DFSize = 5;

        if ( Bits.extractUInt( byte0, 0, DFSize ) != 17 ) {
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
        return Bits.extractUInt( payload, 0, typeCodeSize );
    }


    /**
     * gives the downlink format of the instance of ADS-B message
     *
     * @return the downlink format of the instance
     * @author Eva Mangano 345375
     */
    public int downLinkFormat() {
        return Bits.extractUInt( bytes.byteAt( 0 ), 0, 1 );
    }


    /**
     * gives the ICAO address of the sender of the ADS-B message
     *
     * @return the ICAO address of the sender
     */
    public IcaoAddress icaoAddress() {
        ByteString byteString = new ByteString( bytes.getBytes() );
        long icaoAddressBytes = byteString.bytesInRange( 1, 4 );
        return new IcaoAddress( icaoAddressBytes.toString() ); //TODO how to make String???
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
        return Bits.extractUInt( payload(), 0, typeCodeSize );
    }
}
