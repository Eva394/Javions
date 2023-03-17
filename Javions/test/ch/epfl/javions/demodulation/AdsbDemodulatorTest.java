package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AdsbDemodulatorTest {


    private static final int BATCH_SIZE = 1 << 16;
    private static final int BATCH_SIZE_BYTES = bytesForPowerSamples( BATCH_SIZE );
    private static final int STANDARD_WINDOW_SIZE = 1200;
    private static final int BIAS = 1 << 11;
    private final int batchSize = 1 << 16;
    byte[] data = new byte[1024];
    InputStream stream = new ByteArrayInputStream( data );


    private static int bytesForPowerSamples(int powerSamplesCount) {
        return powerSamplesCount * 2 * Short.BYTES;
    }


    private static byte[] bytesForZeroSamples(int batchesCount) {
        var bytes = new byte[BATCH_SIZE_BYTES * batchesCount];

        var msbBias = BIAS >> Byte.SIZE;
        var lsbBias = BIAS & ( ( 1 << Byte.SIZE ) - 1 );
        for ( var i = 0 ; i < bytes.length ; i += 2 ) {
            bytes[i] = (byte)lsbBias;
            bytes[i + 1] = (byte)msbBias;
        }
        return bytes;
    }


    public static void main(String[] args) throws IOException {
        String f = "samples_20230304_1442.bin";
        try ( InputStream s = new FileInputStream( f ) ) {
            AdsbDemodulator d = new AdsbDemodulator( s );
            RawMessage m;
            while ( ( m = d.nextMessage() ) != null ) {
                System.out.println( m );
            }
        }
    }


    @Test
    void testContructorThrowsIOExceptionOnFlowError() {
        assertThrows( IOException.class, () -> new PowerWindow( stream, STANDARD_WINDOW_SIZE ) );
    }
}