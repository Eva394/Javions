package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

class CprDecoderTest {

    @Test
    void justRun() {
        CprDecoder.decodePosition( 111600., 94445., 108865., 77558., 0 );
    }
}