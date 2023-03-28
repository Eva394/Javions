package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Units.Angle.DEGREE;
import static ch.epfl.javions.Units.Speed.KNOT;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double speed, double trackOrHeading) implements Message {

    public AirborneVelocityMessage {
        if (icaoAddress == null) {
            throw new NullPointerException();
        }

        Preconditions.checkArgument((timeStampNs > 0) && (speed > 0) && (trackOrHeading > 0));
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage) {

        int SB = Bits.extractUInt(rawMessage.payload(), 48, 3);
        int Dew = Bits.extractUInt(rawMessage.payload(), 21, 1);
        int Vew = Bits.extractUInt(rawMessage.payload(), 11, 10);
        int Dns = Bits.extractUInt(rawMessage.payload(), 10, 1);
        int Vns = Bits.extractUInt(rawMessage.payload(), 0, 10);

        int SH = Bits.extractUInt(rawMessage.payload(), 21, 1);
        int HDG = Bits.extractUInt(rawMessage.payload(), 11, 10);
        int T = Bits.extractUInt(rawMessage.payload(), 10, 1);
        int AS = Bits.extractUInt(rawMessage.payload(), 0, 10);


        if (SB == 1 || SB == 2) {
            if (Vns == 0 || Vew == 0) {
                return null;
            }

            double angle = Units.convertTo(Math.atan2(1 - Dns, 1 - Dew), DEGREE);
            double speed;

            if (SB == 1) {
                speed = Units.convertTo(Math.hypot(Vns + 1, Vew + 1), KNOT);
            } else {
                speed = Units.convertTo(4, KNOT);
            }

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);
        }

        if (SB == 3 || SB == 4) {
            if (AS == 0){
                return null;
            }

            double angle = (HDG * 1.0) / (1 << 10);
            double speed;

            if (SH == 1 && SB == 3) {
                speed = AS;
            } else if (SH == 1) {
                speed = Units.convertTo(4, KNOT);
            } else {
                return null;
            }

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);
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
