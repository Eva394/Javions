package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.GeoPos;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.IOException;
import java.io.InputStream;

class AircraftState implements AircraftStateSetter {


    public static void main(String[] args) throws IOException {

        //IcaoAddress expectedAddress = new IcaoAddress( "39D300" );
        //IcaoAddress expectedAddress = new IcaoAddress( "4D2228" );

        try ( InputStream s = AircraftState.class.getClassLoader()
                                                 .getResourceAsStream( "samples_20230304_1442.bin" ) ) {
            AdsbDemodulator d = new AdsbDemodulator( s );
            RawMessage m;
            RawMessage rm = new RawMessage( 0, ByteString.ofHexadecimalString( "8D485020994409940838175B284F" ) );
            System.out.println( AirborneVelocityMessage.of( rm ) );
            AircraftStateAccumulator<AircraftState> a = new AircraftStateAccumulator<>( new AircraftState() );
            //while ( ( m = d.nextMessage() ) != null ) {
            //                                if ( !m.icaoAddress()
            //                                       .equals( expectedAddress ) ) {
            //                                    continue;
            //                                }

            //if ( m.typeCode() == 19 ) {
            Message pm = MessageParser.parse( rm );
            if ( pm != null ) {
                a.update( pm );
                //System.out.println( pm );
            }
            //}
            //}
        }
    }


    static void messageB() {
        RawMessage rm = new RawMessage( 0, ByteString.ofHexadecimalString( "8D485020994409940838175B284F" ) );
        System.out.println( AirborneVelocityMessage.of( rm ) );
    }


    static void messageC() {
        RawMessage rm = new RawMessage( 0, ByteString.ofHexadecimalString( "8DA05F219C06B6AF189400CBC33F" ) );
        System.out.println( AirborneVelocityMessage.of( rm ) );
    }


    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {

    }


    @Override
    public void setCategory(int category) {

    }


    @Override
    public void setCallSign(CallSign callSign) {
    }


    @Override
    public void setPosition(GeoPos position) {
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