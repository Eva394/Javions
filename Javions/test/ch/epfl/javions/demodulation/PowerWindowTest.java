package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

class PowerWindowTest {

    private final int batchSize = 1 << 16;
    private PowerWindow powerWindow;


    @BeforeEach
    void setUp() throws IOException {
        DataInputStream stream = new DataInputStream( new BufferedInputStream(
                new FileInputStream( new File( "C:\\Users\\Eva Mangano\\Downloads\\samples.bin" ) ) ) );

        powerWindow = new PowerWindow( stream, batchSize );
    }


    @Test
    void PowerWindowConstructorThrowsIllegalArgumentExceptionForInvalidWindowSize() {

        int[] batch = new int[batchSize + 1];
        //assertThrows( IllegalArgumentException.class, () -> powerWindow. )
    }
}