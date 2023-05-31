package ch.epfl.javions.adsb;


import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Interface for the messages
 *
 * @author Eva Mangano 345375
 */
public interface Message {
	
	
	/**
	 * gives the horodatage of the ADS-B message
	 *
	 * @return returns the horodatage of the ADS-B message
	 * @author Eva Mangano 345375
	 */
	public abstract long timeStampNs();
	
	
	/**
	 * gives the ICAO address of the sender of the ADS-B message
	 *
	 * @return returns the ICAO address of the sender
	 * @author Eva Mangano 345375
	 */
	public abstract IcaoAddress icaoAddress();
}
