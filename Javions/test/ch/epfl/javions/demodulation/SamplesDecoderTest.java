package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {

    private final int batchSize = 1 << 16;
    private final int numberOfSamples = 1201;
    private SamplesDecoder sampleDecoder;


    @BeforeEach
    public void setUp() throws FileNotFoundException {
        DataInputStream stream = new DataInputStream( new BufferedInputStream(
                new FileInputStream( new File( "C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin" ) ) ) );
        sampleDecoder = new SamplesDecoder( stream, batchSize );
    }


    @Test
    public void SamplesDecoderReadBatchReturnsCorrectValue() throws IOException {
        short[] actual = new short[batchSize];
        short[] expected = new short[]{-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        sampleDecoder.readBatch( actual );
        for ( int i = 1 ; i < expected.length ; i++ ) {
            assertEquals( expected[i], actual[i] );
        }
    }


    @Test
    public void testSamplesDecoderCreationWithValidBatchSize() {
        InputStream stream = new ByteArrayInputStream( new byte[100] );
        int batchSize = 100;
        SamplesDecoder decoder = new SamplesDecoder( stream, batchSize );
        assertNotNull( decoder );
    }


    @Test
    public void testSamplesDecoderCreationWithZeroBatchSize() {
        assertThrows( IllegalArgumentException.class, () -> {
            InputStream stream = new ByteArrayInputStream( new byte[100] );
            int batchSize = 0;
            SamplesDecoder decoder = new SamplesDecoder( stream, batchSize );
        } );
    }


    @Test
    public void testSamplesDecoderCreationWithNegativeBatchSize() {
        assertThrows( IllegalArgumentException.class, () -> {
            InputStream stream = new ByteArrayInputStream( new byte[100] );
            int batchSize = -1;
            SamplesDecoder decoder = new SamplesDecoder( stream, batchSize );
        } );
    }


    @Test
    public void testSamplesDecoderCreationWithNullInputStream() {
        assertThrows( NullPointerException.class, () -> {
            InputStream stream = null;
            int batchSize = 100;
            SamplesDecoder decoder = new SamplesDecoder( stream, batchSize );
        } );
    }
}