package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.math.BigInteger;

public record AircraftIdentificationMessage(long timeStampNs,IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {


    public AircraftIdentificationMessage() {
        Preconditions.checkArgument(timeStampNs >= 0);

        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException();
        }

    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        /*
        if (rawMessage.typeCode() != 1 && rawMessage.typeCode() != 2 && rawMessage.typeCode() != 3 && rawMessage.typeCode() != 4) {
            return null;
        }

         */

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

        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }
}

        /*
        char[] callSign = new char[8];

        for (int i=0; i<8; i++){
            int callSign = (int) rawMessage.get(42 - (6 * i), 6);
        }

        char[] C1 = new char[6];
        rawMessage.toString().getChars(42,47,C1,0);

        String CA = rawMessage.toString().substring(48, 50);
        String C1 = rawMessage.toString().substring(42, 47);
        String C2 = rawMessage.toString().substring(36, 41);
        String C3 = rawMessage.toString().substring(30, 35);
        String C4 = rawMessage.toString().substring(24, 29);
        String C5 = rawMessage.toString().substring(18, 23);
        String C6 = rawMessage.toString().substring(12, 17);
        String C7 = rawMessage.toString().substring(6, 11);
        String C8 = rawMessage.toString().substring(0, 5);



         */



