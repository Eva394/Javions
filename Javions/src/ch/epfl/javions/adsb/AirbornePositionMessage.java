package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          21/03/2023
 */


import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x,
                                      double y) implements Message {

    public AirbornePositionMessage {
        if ( icaoAddress == null ) {
            throw new NullPointerException();
        }
        Preconditions.checkArgument(
                timeStampNs >= 0 && ( parity == 0 || parity == 1 ) && x >= 0 && x < 1 && y >= 0 && y < 1 );
    }




    public static AirbornePositionMessage of(RawMessage rawMessage) {

        int ALT = Bits.extractUInt(rawMessage.payload(),36,12);

        int q = Bits.extractUInt(rawMessage.payload(),40,1);

        if (q==1){
            int left = Bits.extractUInt(ALT, 5,7);
            int right = Bits.extractUInt(ALT,0,4);
            int a = right | (left << 4) ;
            int alt = -1000 + a*25;

        }
        else{


        }

        return null;
    }
}
