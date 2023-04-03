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
        //TODO modularize and choose a version
        long payload = rawMessage.payload();

        int subType = Bits.extractUInt( payload, 48, 3 );
        int stDependentBits = Bits.extractUInt( payload, 21, 22 );

        if ( subType == 1 || subType == 2 ) {

            int directionEW = Bits.extractUInt( stDependentBits, 21, 1 );
            int velocityEW = Bits.extractUInt( stDependentBits, 11, 10 ) - 1;
            int directionNS = Bits.extractUInt( stDependentBits, 10, 1 );
            int velocityNS = Bits.extractUInt( stDependentBits, 0, 10 ) - 1;

            if ( velocityNS == -1 || velocityEW == -1 ) {
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

        if ( subType == 3 || subType == 4 ) {
            int heading = Bits.extractUInt( stDependentBits, 11, 10 );
            int airSpeed = Bits.extractUInt( stDependentBits, 0, 10 ) - 1;

            if ( Bits.testBit( stDependentBits, 11 ) || airSpeed == 0 ) {
                return null;
            }

            double angle = Units.convertFrom( Math.scalb( (double)heading, -10 ), Units.Angle.TURN );
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
