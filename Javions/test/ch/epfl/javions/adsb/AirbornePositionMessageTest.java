package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirbornePositionMessageTest {

    @Test
    void testDecodeGreyReturnsCorrectValue() {

        int numberLength = 3;

        int[] actuals = new int[]{0b000, 0b001, 0b011, 0b010, 0b110, 0b111, 0b101, 0b100};

        for ( int i = 0 ; i < 8 ; i++ ) {
            int expected = i;
            int actual = AirbornePositionMessage.decodeGrey( actuals[i], numberLength );
            assertEquals( expected, actual );
        }
    }
}