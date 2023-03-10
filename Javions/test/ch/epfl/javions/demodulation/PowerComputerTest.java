package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PowerComputerTest {

    private final int batchSize = 1 << 16;
    private final int numberOfSamples = 1201;
    private PowerComputer powerComputer;


    @BeforeEach
    public void setUp() throws FileNotFoundException {
        //InputStream inputStream = new FileInputStream(new File("C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin"));
        DataInputStream stream = new DataInputStream( new BufferedInputStream(
                new FileInputStream( new File( "C:\\Users\\Eva Mangano\\Downloads\\samples.bin" ) ) ) );
        powerComputer = new PowerComputer( stream, batchSize );
    }


    @Test
    public void testReadBatchThrowsIllegalArgumentExceptionWhenBatchSizeIsInvalid() {
        int[] batch = new int[batchSize + 1];
        assertThrows( IllegalArgumentException.class, () -> powerComputer.readBatch( batch ) );
    }


    @Test
    public void testReadbatchReturnsCorrectValue() throws IOException {

        int[] actual = new int[batchSize];
        int[] expected = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};

        powerComputer.readBatch( actual );

        for ( int i = 0 ; i < expected.length ; i++ ) {
            assertEquals( expected[i], actual[i] );
        }
    }


    @Test
    @Disabled
    public void testReadBatchReturnsCorrectNumberOfPowerSamples() throws IOException {
        int[] batch = new int[batchSize];
        int totalSamples = 0;
        while ( totalSamples < numberOfSamples ) {
            int numSamples = powerComputer.readBatch( batch );
            totalSamples += numSamples;
        }
        assertEquals( numberOfSamples, totalSamples );
    }


    @Test
    @Disabled
    public void testReadBatchThrowsIOExceptionWhenEndOfFileReached() throws IOException {
        int[] batch = new int[batchSize];
        int totalSamples = 0;
        while ( totalSamples < numberOfSamples ) {
            int numSamples = powerComputer.readBatch( batch );
            totalSamples += numSamples;
        }
        assertThrows( IOException.class, () -> powerComputer.readBatch( batch ) );
    }
}