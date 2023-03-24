package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;


public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final String ALPHABET = "-ABCDEFGHIJKLMNOPQRSTUVWXYZ----- ---------------0123456789------";


    public AircraftIdentificationMessage {
        Preconditions.checkArgument( timeStampNs >= 0 );

        if ( icaoAddress == null || callSign == null ) {
            throw new NullPointerException();
        }
    }


    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        int typeCode = rawMessage.typeCode();

        /*
        if (typeCode != 1 && typeCode != 2 && typeCode != 3 && typeCode != 4) {
            return null;
        }

         */

        int CA = Bits.extractUInt( rawMessage.payload(), 48, 3 );
        int temp = 0xE - typeCode;
        int category = ( temp << 4 ) | CA;

        /*
        int categoryDecimal = CA << 1 | temp;
        int categroy = Integer.
         */
        /*

        String strCategory = Integer.toHexString(temp).toUpperCase() + Integer.toHexString(CA).toUpperCase();
        int category = Integer.parseInt(strCategory, 16);

         */

        /*

        int callSign = Bits.extractUInt(rawMessage.payload(),0,48);

         */

        StringBuilder cS = new StringBuilder( 6 );

        for ( int i = 0 ; i < 8 ; i++ ) {

            int character = Bits.extractUInt( rawMessage.payload(), 6 * i, 6 );
            char c = ALPHABET.charAt( character );
            if ( !( c == ' ' ) ) {
                cS.append( c );
            }

            /*

            if (character == 0 || (character >= 27 && character <= 47)) {
                return null;
            }

             */
        }

        String callSignStr = cS.reverse()
                               .toString();

        if ( callSignStr.contains( "-" ) ) {
            return null;
        }

        CallSign callSign = new CallSign( callSignStr );

        return new AircraftIdentificationMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(), category,
                                                  callSign );
    }
}


        /*

        String strCA = rawMessage.toString().substring(48, 50);
        int intCA = Integer.parseInt(strCA,2);
        int typeCode = rawMessage.typeCode();
        int temp = 14 - typeCode;

        String strCategory = Integer.toHexString(temp).toUpperCase() + Integer.toHexString(intCA).toUpperCase();

        int category = Integer.parseInt(strCategory, 16);


        String callSignString = rawMessage.toString().substring(0, 47);
        CallSign callSign = new CallSign(callSignString);

        for (int i = 0; i < 8; i++) {

            String a = rawMessage.toString().substring(48 - (6 * i), 48 - (6 * i)+5 );
            int character = (int) Integer.parseInt(a, 2);

            if (character == 0 || (character >= 27 && character <= 47)) {
                return null;
            }

        }

         */


        /*
        char[] callSign = new char[8];

        for (int i=0; i<8; i++){
            int callSign = (int) rawMessage.get(42 - (6 * i), 6);
        }

        char[] C1 = new char[6];
        rawMessage.toString().getChars(42,47,C1,0);

        String CA = rawMessage.toString().substring(0, 2);
        String C1 = rawMessage.toString().substring(3, 8);
        String C2 = rawMessage.toString().substring(9, 14);
        String C3 = rawMessage.toString().substring(15, 20);
        String C4 = rawMessage.toString().substring(21, 26);
        String C5 = rawMessage.toString().substring(27, 32);
        String C6 = rawMessage.toString().substring(33, 38);
        String C7 = rawMessage.toString().substring(39, 44);
        String C8 = rawMessage.toString().substring(45, 50);



         */



