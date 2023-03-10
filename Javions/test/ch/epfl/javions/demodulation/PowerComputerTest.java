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
                new FileInputStream( new File( "C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin" ) ) ) );
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
        powerComputer.readBatch( batch );

        for ( int i = 0 ; i < 10 ; i++ ) {
            System.out.println( batch[i] );
        }
    }


}