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
        DataInputStream stream = new DataInputStream( new BufferedInputStream( new FileInputStream( new File(
                "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\samples.bin" ) ) ) );
        powerComputer = new PowerComputer( stream, batchSize );
    }


    @Test
    public void testReadBatchThrowsIllegalArgumentExceptionWhenBatchSizeIsInvalid() {
        int[] batch = new int[batchSize + 1];
        assertThrows( IllegalArgumentException.class, () -> powerComputer.readBatch( batch ) );
    }


    @Test
    public void testReadbatchReturnsCorrectValue() throws IOException {

        int[] batch = new int[batchSize];

        batch[0] = 3;
        batch[1] = 8;
        batch[2] = -9;
        batch[3] = -8;
        batch[4] = -5;
        batch[5] = -8;
        batch[6] = -12;
        batch[7] = -16;
        batch[8] = -23;
        batch[9] = -9;

        System.out.println( batch[0] );
        System.out.println( batch[1] );

        powerComputer.readBatch( batch );

        for ( int i = 0 ; i < 10 ; i++ ) {
            System.out.println( batch[i] );
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