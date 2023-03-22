package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirbornePositionMessageTest {

    @Test
    void testDecodeGrayReturnsCorrectValue() {

        int numberLength = 3;

        int[] actuals = new int[]{0b000, 0b001, 0b011, 0b010, 0b110, 0b111, 0b101, 0b100};

        for ( int i = 0 ; i < 8 ; i++ ) {
            int expected = i;
            int actual = AirbornePositionMessage.decodeGray( actuals[i], numberLength );
            assertEquals( expected, actual );
        }
    }


    @Test
    void testOfReturnsCorrectAltitude() {
        //TODO this is a bullshit test i just print out the values lol we'll have to change that
        AirbornePositionMessage.of( new RawMessage( 100, new ByteString( new byte[14] ) ) );
    }
}