package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {

    byte[] data = new byte[1024];
    InputStream stream = new ByteArrayInputStream( data );
    private final int batchSize = 1 << 16;


    @Test
    public void testPowerWindowConstructorWithIllegalWindowSize() throws IOException {

        assertThrows( IllegalArgumentException.class, () -> new PowerWindow( stream, 0 ) );
        assertThrows( IllegalArgumentException.class, () -> new PowerWindow( stream, 1 << 16 + 1 ) );
        assertDoesNotThrow( () -> new PowerWindow( stream, 1 << 16 ) );
    }


    @Test
    public void testPowerWindowSize() throws IOException {

        PowerWindow powerWindow = new PowerWindow( stream, 80 );
        assertEquals( 80, powerWindow.size() );
    }



    @Test
    public void testPowerWindowPosition() throws IOException {

        PowerWindow powerWindow = new PowerWindow(stream, 80);

        assertEquals(0, powerWindow.position());

        int offset = 10;
        powerWindow.advanceBy(offset);
        assertEquals(offset, powerWindow.position());

        offset = 50;
        powerWindow.advanceBy(offset);
        assertEquals(offset + 10, powerWindow.position());


    }
}