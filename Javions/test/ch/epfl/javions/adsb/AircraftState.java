package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class AircraftState implements AircraftStateSetter {


    public static void main(String[] args) throws IOException {
        String f = "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\samples_20230304_1442.bin";
        //        RawMessage rawMessage = RawMessage.of( 100, HexFormat.of()
        //                                                             .parseHex( "8D485020994409940838175B284F" ) );
        //        IcaoAddress expectedAddress = rawMessage.icaoAddress();
        IcaoAddress expectedAddress = new IcaoAddress( "4D2228" );
        try ( InputStream s = new FileInputStream( f ) ) {
            AdsbDemodulator d = new AdsbDemodulator( s );
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a = new AircraftStateAccumulator<>( new AircraftState() );
            while ( ( m = d.nextMessage() ) != null ) {
                if ( !m.icaoAddress()
                       .equals( expectedAddress ) ) {
                    continue;
                }

                Message pm = MessageParser.parse( m );
                if ( pm != null ) {
                    a.update( pm );
                    System.out.println( pm );
                }
            }
        }
    }


    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {

    }


    @Override
    public void setCategory(int category) {

    }


    @Override
    public void setCallSign(CallSign callSign) {
        //System.out.println( "indicatif : " + callSign );
    }


    @Override
    public void setPosition(GeoPos position) {
        //System.out.println( "position : " + position );
    }


    @Override
    public void setAltitude(double altitude) {

    }


    @Override
    public void setVelocity(double velocity) {

    }


    @Override
    public void setTrackOrHeading(double trackOrHeading) {

    }
}