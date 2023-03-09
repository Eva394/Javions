package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SamplesDecoderTest {

    private final int batchSize = 1 << 16;
    private final int numberOfSamples = 1201;
    private SamplesDecoder sampleDecoder;


    @BeforeEach
    public void setUp() throws FileNotFoundException {
        //InputStream stream = new FileInputStream(new File("C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin"));
        DataInputStream stream = new DataInputStream( new BufferedInputStream(
                new FileInputStream( new File( "C:\\Users\\Eva Mangano\\Downloads\\samples.bin" ) ) ) );
        sampleDecoder = new SamplesDecoder( stream, batchSize );
    }


    @Test
    public void testReadbatchReturnsCorrectValue() throws IOException {
        short[] actual = new short[batchSize];
        short[] expected = new short[]{-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        sampleDecoder.readBatch( actual );
        for ( int i = 0 ; i < expected.length ; i++ ) {
            assertEquals( expected[i], actual[i] );
        }
    }
}