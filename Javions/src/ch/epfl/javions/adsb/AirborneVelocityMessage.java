package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Airborne Velocity Message
 *
 * @author Nagyung KIM (339628)
 */

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {

    public static final int DEW_START_INDEX = 21;
    public static final int VEW_START_INDEX = 11;
    public static final int DNS_START_INDEX = 10;
    public static final int VNS_START_INDEX = 0;
    public static final int HEADING_START_INDEX = 11;
    public static final int AIRSPEED_START_INDEX = 0;
    public static final int HEADING_BITLENGTH = 10;
    public static final int AIRSPEED_BITLENGTH = 10;
    public static final int ST_START_INDEX = 48;
    public static final int ST_BITLENGTH = 3;
    public static final int BITS_START_INDEX = 21;
    public static final int BITS_BITLENGTH = 22;
    public static final int DEW_BITLENGTH = 1;
    public static final int VEW_BITLENGTH = 10;
    public static final int DNS_BITLENGTH = 1;
    public static final int VNS_BITLENGTH = 10;
    public static final int GROUNDSPEED_SUBSONIC = 1;
    public static final int GROUNDSPEED_SUPERSONIC = 2;
    public static final int AIRSPEED_SUBSONIC = 3;
    public static final int AIRSPEED_SUPERSONIC = 4;

    /**
     *
     * @param timeStampNs horodatage in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param speed the speed of the object
     * @param trackOrHeading the direction of movement of the aircraft, in radians
     */

    public AirborneVelocityMessage {
        Objects.requireNonNull( icaoAddress );
        Preconditions.checkArgument( ( timeStampNs >= 0 ) && ( speed >= 0 ) && ( trackOrHeading >= 0 ) );
    }

    /**
     *
     * @param rawMessage the ADS-B message
     * @return speed message in flight
     */


    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        //TODO modularize and choose a version
        long payload = rawMessage.payload();

        int subType = Bits.extractUInt( payload, ST_START_INDEX, ST_BITLENGTH);
        int stDependentBits = Bits.extractUInt( payload, BITS_START_INDEX, BITS_BITLENGTH);


        if ( subType == GROUNDSPEED_SUBSONIC || subType == GROUNDSPEED_SUPERSONIC) {

            int directionEW = Bits.extractUInt( stDependentBits, DEW_START_INDEX, DEW_BITLENGTH);
            int velocityEW = Bits.extractUInt( stDependentBits, VEW_START_INDEX, VEW_BITLENGTH) - 1;
            int directionNS = Bits.extractUInt( stDependentBits, DNS_START_INDEX, DNS_BITLENGTH);
            int velocityNS = Bits.extractUInt( stDependentBits, VNS_START_INDEX, VNS_BITLENGTH) - 1;

            if ( velocityNS == 0 || velocityEW == 0 ) {
                return null;
            }
            if ( directionEW == 1 ) {
                velocityEW *= -1;
            }
            if ( directionNS == 1 ) {
                velocityNS *= -1;
            }

            double angle = Math.atan2( velocityEW, velocityNS );
            double speed = Math.hypot( velocityNS, velocityEW );

            if ( angle < 0 ) {
                angle += 2 * Math.PI;
            }

            if ( subType == 1 ) {
                speed = Units.convertFrom( speed, Units.Speed.KNOT );
            }
            else {
                speed = Units.convertFrom( speed * 4.0, Units.Speed.KNOT );
            }
            return new AirborneVelocityMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle );
        }

        if ( subType == AIRSPEED_SUBSONIC || subType == AIRSPEED_SUPERSONIC) {
            int heading = Bits.extractUInt( stDependentBits, HEADING_START_INDEX, HEADING_BITLENGTH);
            int airSpeed = Bits.extractUInt( stDependentBits, AIRSPEED_START_INDEX, AIRSPEED_BITLENGTH) - 1;

            Bits.testBit( rawMessage.payload(), 21 );

            if ( Bits.testBit( stDependentBits, 11 ) || airSpeed == 0 ) {
                return null;
            }

            double angle = Math.scalb( (double)heading, -10 );
            double speed;

            if ( subType == 3 ) {
                speed = Units.convertFrom( airSpeed, Units.Speed.KNOT );
            }
            else {
                speed = Units.convertFrom( ( airSpeed ) * 4.0, Units.Speed.KNOT );
            }
            return new AirborneVelocityMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle );
        }

        return null;
    }
}