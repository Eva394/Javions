package ch.epfl.javions.adsb;

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
public record RawMessage( long timeStampNs, ByteString bytes ) {
	
	/**
	 * length of an ADS-B message
	 */
	public static final int LENGTH = 14;
	private static final HexFormat HEX_FORMAT = HexFormat.of()
	                                                     .withUpperCase();
	private static final Crc24 CRC_24 = new Crc24( Crc24.GENERATOR );
	private static final int DOWNLINK_FORMAT = 17;
	private static final int DF_SIZE = 5;
	private static final int DF_START = 3;
	private static final int TYPE_CODE_SIZE = 5;
	private static final int MSB_POSITION = Long.SIZE - ( Byte.SIZE + TYPE_CODE_SIZE );
	private static final int DL_SIZE = 5;
	private static final int DL_POSITION = Byte.SIZE - DL_SIZE;
	private static final int NUMBER_OF_HEX_ICAO = 6;
	private static final int ICAO_START = 1;
	private static final int ICAO_END = 4;
	private static final int ME_START = 4;
	private static final int ME_END = 11;
	
	
	/**
	 * Constructor. Builds an instance of RawMessage
	 *
	 * @param timeStampNs horodatage of the message (in nanoseconds)
	 * @param bytes       bytes of the message
	 * @author Eva Mangano 345375
	 */
	public RawMessage {
		Preconditions.checkArgument( ( timeStampNs >= 0 ) );
		Preconditions.checkArgument( ( bytes.size() == LENGTH ) );
	}
	
	/**
	 * gives the downlink format of the instance of ADS-B message
	 *
	 * @return the downlink format of the instance
	 * @author Eva Mangano 345375
	 */
	public int downLinkFormat() {
		return Bits.extractUInt( bytes.byteAt( 0 ), DL_POSITION, DL_SIZE );
	}
	
	/**
	 * gives the ICAO address of the sender of the ADS-B message
	 *
	 * @return the ICAO address of the sender
	 * @author Eva Mangano 345375
	 */
	public IcaoAddress icaoAddress() {
		long icaoAddressBytes = bytes.bytesInRange( ICAO_START, ICAO_END );
		return new IcaoAddress( HEX_FORMAT.toHexDigits( icaoAddressBytes, NUMBER_OF_HEX_ICAO ) );
	}
	
	/**
	 * gives the ME attribute of the ADS-B message
	 *
	 * @return the ME attribute
	 * @author Eva Mangano 345375
	 */
	public long payload() {
		return bytes.bytesInRange( ME_START, ME_END );
	}
	
	/**
	 * gives the type code of the ADS-B message
	 *
	 * @return the type code
	 * @author Eva Mangano 345375
	 */
	public int typeCode() {
		return typeCode( payload() );
	}
	
	/**
	 * returns an instance of RawMessage with the parameters passed
	 *
	 * @param timeStampNs horodatage of the message (in nanoseconds)
	 * @param bytes       bytes of the message
	 * @return an instance of RawMessage with the parameters passed
	 * @author Eva Mangano 345375
	 */
	public static RawMessage of( long timeStampNs, byte[] bytes ) {
		
		return CRC_24.crc( bytes ) == 0
		       ? new RawMessage( timeStampNs, new ByteString( bytes ) )
		       : null;
	}
	
	/**
	 * gives the size of an ADS-B message
	 *
	 * @param byte0 first byte of the ADS-B message (downlink format)
	 * @return the length <code>LENGTH</code> of an ADS-B message if the downlink format is 17, 0 if not
	 * @author Eva Mangano 345375
	 */
	public static int size( byte byte0 ) {
		int downlinkFormat = Bits.extractUInt( byte0, DF_START, DF_SIZE );
		
		return downlinkFormat == DOWNLINK_FORMAT
		       ? LENGTH
		       : 0;
	}
	
	/**
	 * gives the type code of the ME attribute passed
	 *
	 * @param payload ME attribute
	 * @return the type code of the ME attribute
	 * @author Eva Mangano 345375
	 */
	public static int typeCode( long payload ) {
		return Bits.extractUInt( payload, MSB_POSITION, TYPE_CODE_SIZE );
	}
}
