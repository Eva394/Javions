package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CprDecoderTest {

    /*
    @Test
    void justRun() {
        CprDecoder.decodePosition( 111600., 94445., 108865., 77558., 0 );
    }

     */

    @Test
    public void testInvalidMostRecentValue() {
        double x0 = 1.0;
        double y0 = 2.0;
        double x1 = 3.0;
        double y1 = 4.0;
        int mostRecent = 2; // invalid value
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        });
    }
}

