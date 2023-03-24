package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CprDecoderTest {

    @Test
    void test() {
        GeoPos geoPos = CprDecoder.decodePosition( .3, .3, .3, .3, 1 );
        System.out.println( geoPos );
        System.out.println( geoPos.latitudeT32() );
        System.out.println( geoPos.longitudeT32() );
    }


    @Test
    void decodePosition() {
        double x0 = Math.scalb( 111600d, -17 );
        double y0 = Math.scalb( 94445d, -17 );
        double x1 = Math.scalb( 108865d, -17 );
        double y1 = Math.scalb( 77558d, -17 );
        GeoPos p = CprDecoder.decodePosition( x0, y0, x1, y1, 0 );
        System.out.println( p );
        System.out.println( p.longitudeT32() );
        System.out.println( p.latitudeT32() );
        //assertEquals( ( 89192898 ), p.longitudeT32() );
        //assertEquals( ( 552659081 ), p.latitudeT32() );
        GeoPos pos = CprDecoder.decodePosition( 0.62, 0.42, 0.6200000000000000001, 0.4200000000000000001, 0 );
        System.out.println( pos );
        GeoPos pos1 = CprDecoder.decodePosition( 0.3, 0.3, 0.3, 0.3, 1 );
        GeoPos pos2 = CprDecoder.decodePosition( 0.3, 0.3, 0.3, 0.3, 0 );
        System.out.println( pos1 );
        System.out.println( pos2 );
        assertEquals( ( Units.convert( pos1.longitude(), Units.Angle.RADIAN, Units.Angle.DEGREE ) ),
                      1.862068958580494 );
        //assertEquals( ( Units.convert( pos1.latitude(), Units.Angle.RADIAN, Units.Angle.DEGREE ) ),
        //              1.8305084947496653 );
        assertEquals( ( Units.convert( pos2.latitude(), Units.Angle.RADIAN, Units.Angle.DEGREE ) ),
                      1.7999999597668648 );

        //assertEquals( ( Units.convert( pos2.longitude(), Units.Angle.RADIAN, Units.Angle.DEGREE ) ),
        //              1.8305084947496653 );
        //assertEquals( ( Units.convert( pos2.longitude(), Units.Angle.RADIAN, Units.Angle.DEGREE ) ),
        //              1.8305084947496653 );
    }
}