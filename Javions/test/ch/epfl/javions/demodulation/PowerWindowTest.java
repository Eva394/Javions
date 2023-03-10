package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {

    byte[] data = new byte[1024];
    InputStream stream = new ByteArrayInputStream( data );


    @Test
    public void testPowerWindowConstructorWithIllegalWindowSize() throws IOException {

        assertThrows( IllegalArgumentException.class, () -> new PowerWindow( stream, 0 ) );
        assertThrows( IllegalArgumentException.class, () -> new PowerWindow( stream, 1 << 16 + 1 ) );
        assertDoesNotThrow( () -> new PowerWindow( stream, 1 << 16 ) );
    }


    @Test
    public void testPowerWindowSize() throws IOException {

        PowerWindow powerWindow = new PowerWindow( stream, data.length );
        assertEquals( data.length, powerWindow.size() );
    }


    @Test
    public void testPowerWindowPosition() {

    }
}