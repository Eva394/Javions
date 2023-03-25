package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          25/03/2023
 */


import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {

    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        return null;
    }
}
