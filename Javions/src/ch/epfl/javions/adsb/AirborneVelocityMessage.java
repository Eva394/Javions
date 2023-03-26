package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Units.Angle.DEGREE;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) implements Message {

    public AirborneVelocityMessage{

        if (icaoAddress == null){
            throw new NullPointerException();
        }

        Preconditions.checkArgument((timeStampNs>0) && (trackOrHeading>0));
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){

        int ST = Bits.extractUInt(rawMessage.payload(),48,3);
        int Dew = Bits.extractUInt(rawMessage.payload(),21,1);
        int Vew = Bits.extractUInt(rawMessage.payload(),11, 10);
        int Dns = Bits.extractUInt(rawMessage.payload(),10,1);
        int Vns = Bits.extractUInt(rawMessage.payload(),0,10);

        if ((!(ST == 1) && !(ST == 2) && !(ST == 3) && !(ST == 4)) || ((Vns==0) || (Vew ==0))){
            return null;
        }

        double angle = Math.atan2(1-Dns, 1- Dew);
        if(angle < 0) {
            angle += Math.PI * 2; // Ensure that angle is always positive
        }

        double angleDegree = Units.convertTo(Math.atan2(Dns-1, Dew-1),DEGREE);

        double speed = Math.sqrt((Vns-1)*(Vns-1) + (Vew-1)*(Vew-1));


        return new AirborneVelocityMessage(
                rawMessage.timeStampNs(),
                rawMessage.icaoAddress(), speed, angle);
    }
}
