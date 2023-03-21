package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage(long timeStampNs,IcaoAddress icaoAddress, int category, CallSign callSign) implements Message{

    public AircraftIdentificationMessage(){
        Preconditions.checkArgument(timeStampNs >=0);

        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException();
        }

    }

    public AircraftIdentificationMessage of(RawMessage rawMessage){


        for (int i = 0; i < 48; i +=6){

            int nb =

        }


    }


}
