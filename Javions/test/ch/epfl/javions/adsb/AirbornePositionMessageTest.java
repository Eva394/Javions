package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

class AirbornePositionMessageTest {

    @Test
    void testDecodeGrayReturnsCorrectValue() {
        //TODO test correclty going through AirbornePositionMessage.of()

        int numberLength = 3;

        int[] actuals = new int[]{0b000, 0b001, 0b011, 0b010, 0b110, 0b111, 0b101, 0b100};

        //        for ( int i = 0 ; i < 8 ; i++ ) {
        //            int expected = i;
        //            int actual = AirbornePositionMessage.decodeGray( actuals[i], numberLength );
        //            assertEquals( expected, actual );
        //        }
    }


    @Test
    void testOfReturnsCorrectAltitude() {
        //TODO this is a bullshit test i just print out the values lol we'll have to change that
        AirbornePositionMessage.of( new RawMessage( 100, new ByteString( HexFormat.of()
                                                                                  .parseHex(
                                                                                          "8D39203559B225F07550ADBE328F" ) ) ) );

        AirbornePositionMessage.of( new RawMessage( 100, new ByteString( new byte[14] ) ) );
    }
}