package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {

    public AirborneVelocityMessage {
        Objects.requireNonNull( icaoAddress );
        Preconditions.checkArgument( ( timeStampNs >= 0 ) && ( speed >= 0 ) && ( trackOrHeading >= 0 ) );
    }


    public static AirborneVelocityMessage of(RawMessage rawMessage) {

        int subType = Bits.extractUInt( rawMessage.payload(), 48, 3 );

        if ( subType == 1 || subType == 2 ) {
            int directionEW = Bits.extractUInt( rawMessage.payload(), 21, 1 );
            int velocityEW = Bits.extractUInt( rawMessage.payload(), 11, 10 );
            int directionNS = Bits.extractUInt( rawMessage.payload(), 10, 1 );
            int velocityNS = Bits.extractUInt( rawMessage.payload(), 0, 10 );

            if ( velocityNS == 0 || velocityEW == 0 ) {
                return null;
            }

            double angle = Math.atan2( directionNS, directionEW );
            double speed = Math.hypot( velocityNS - 1, velocityEW - 1 );

            if ( subType == 1 ) {
                speed = Units.convertFrom( speed, Units.Speed.KNOT );
            }
            else {
                speed = Units.convertFrom( speed * 4., Units.Speed.KNOT );
            }

            //TODO angle

            //            double angle = Units.convertTo( Math.atan2( 1 - directionNS, 1 - directionEW ), DEGREE );
            //            double speed;
            //
            //            if ( subType == 1 ) {
            //                speed = Units.convertTo( Math.hypot( velocityNS - 1, velocityEW - 1 ), KNOT );
            //            }
            //            else {
            //                speed = Units.convertTo( Math.hypot( velocityNS - 1, velocityEW - 1 ) / 4., KNOT );
            //            }
            //
            return new AirborneVelocityMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle );
        }

        if ( subType == 3 || subType == 4 ) {
            int headingAvailability = Bits.extractUInt( rawMessage.payload(), 21, 1 );
            int heading = Bits.extractUInt( rawMessage.payload(), 11, 10 );
            int airVelocity = Bits.extractUInt( rawMessage.payload(), 0, 10 );

            if ( airVelocity == 0 || heading != 1 ) {
                return null;
            }

            double angle = (double)heading / ( 1 << 10 );
            double speed;

            if ( subType == 3 ) {
                speed = Units.convertFrom( airVelocity - 1, Units.Speed.KNOT );
            }
            else {
                speed = Units.convertFrom( ( airVelocity - 1 ) * 4., Units.Speed.KNOT );
            }

            //            double angle = (double)( heading ) / ( 1 << 10 );
            //            double speed;
            //
            //            if ( headingAvailability == 1 && subType == 3 ) {
            //                speed = airVelocity;
            //            }
            //            else if ( headingAvailability == 1 ) {
            //                speed = Units.convertTo( 4, KNOT );
            //            }
            //            else {
            //                return null;
            //            }
            //
            return new AirborneVelocityMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle );
        }

        return null;
    }
}


        /*

        if ( !(SB==1) && !(SB==2) && !(SB==3) && !(SB==4) ){
            return null;
        }


        if (SB ==1 || SB==2){
            if (Vns==0 | Vew ==0){
                return null;
            }

            double angle = Units.convertTo(Math.atan2(1-Dns,1-Dew),DEGREE);
            double speed;


            if (SB ==1) {

                double speed = Units.convertTo(Math.hypot(Vns+1, Vew+1), KNOT);
            }

            else{
                double speed = Units.convertTo(4, KNOT);
            }
        }

        if (SB==3 || SB==4){

            double angle = (HDG * 1.0) / (1<<10);

            if (SH==1 && SB ==3){
                double speed = AS;

            }

            if (SH==1 && SB==4){
                double speed = Units.convertTo(4, KNOT);
            }
        }


        return new AirborneVelocityMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                speed,
                angle);

    }
*/
