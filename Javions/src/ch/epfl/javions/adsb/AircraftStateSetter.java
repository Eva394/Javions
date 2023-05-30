package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * Interface for the state of an aircraft
 *
 * @author Eva Mangano 345375
 */
public interface AircraftStateSetter {
    
    /**
     * changes the horodatage of the last message received to <code>timeStampNs</code>
     *
     * @param timeStampNs new horodatage
     * @author Eva Mangano 345375
     */
    public abstract void setLastMessageTimeStampNs( long timeStampNs );
    
    
    /**
     * changes the category of an aircraft to <code>category</code>
     *
     * @param category new category
     * @author Eva Mangano 345375
     */
    public abstract void setCategory( int category );
    
    
    /**
     * changes the call sign of the aircraft to <code>callSign</code>
     *
     * @param callSign new call sign
     * @author Eva Mangano 345375
     */
    public abstract void setCallSign( CallSign callSign );
    
    
    /**
     * changes the position of the aircraft to <code>position</code>
     *
     * @param position new position
     * @author Eva Mangano 345375
     */
    public abstract void setPosition( GeoPos position );
    
    
    /**
     * changes the altitude of the aircraft to <code>altitude</code>
     *
     * @param altitude new altitude
     * @author Eva Mangano 345375
     */
    public abstract void setAltitude( double altitude );
    
    
    /**
     * changes the velocity of the aircraft to <code>velocity</code>
     *
     * @param velocity new velocity
     * @author Eva Mangano 345375
     */
    public abstract void setVelocity( double velocity );
    
    
    /**
     * changes the track or heading of the aircraft to <code>trackOrHeading</code>
     *
     * @param trackOrHeading new track or heading
     * @author Eva Mangano 345375
     */
    public abstract void setTrackOrHeading( double trackOrHeading );
}
