package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;


public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final String ELEMENTS = "-ABCDEFGHIJKLMNOPQRSTUVWXYZ----- ---------------0123456789------";


    public AircraftIdentificationMessage {
        Preconditions.checkArgument( timeStampNs >= 0 );

        if ( icaoAddress == null || callSign == null ) {
            throw new NullPointerException();
        }
    }


    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        int typeCode = rawMessage.typeCode();
        int CA = Bits.extractUInt( rawMessage.payload(), 48, 3 );
        int temp = 0xE - typeCode;
        int category = ( temp << 4 ) | CA;


        StringBuilder cS = new StringBuilder( 6 );

        for ( int i = 0 ; i < 8 ; i++ ) {

            int character = Bits.extractUInt( rawMessage.payload(), 6 * i, 6 );
            char c = ELEMENTS.charAt( character );
            if ( !( c == ' ' ) /*&& cS.isEmpty() */) {
                cS.append( c );
            }
        }

        String callSignStr = cS.reverse().toString();

        if ( callSignStr.contains( "-" ) ) {
            return null;
        }


        CallSign callSign = new CallSign( callSignStr );

        return new AircraftIdentificationMessage(
                rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                category,
                callSign);
    }
}




