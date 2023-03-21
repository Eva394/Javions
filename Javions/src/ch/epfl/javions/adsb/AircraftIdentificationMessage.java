package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage(long timeStampNs,IcaoAddress icaoAddress, int category, CallSign callSign) implements Message{

    public AircraftIdentificationMessage(){
        Preconditions.checkArgument(timeStampNs >=0);

        if (icaoAddress = 0 || callSign = 0) {
            throw new NullPointerException();
        }

    }

    public AircraftIdentificationMessage of(RawMessage rawMessage){

    }


}
