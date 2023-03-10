package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {

    byte[] data = new byte[1024];
    InputStream stream = new ByteArrayInputStream(data);


    @Test
    public void testPowerWindowConstructorWithIllegalWindowSize() throws IOException {

        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 0));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 1 << 16+1));
        assertDoesNotThrow(() -> new PowerWindow(stream,1 << 16 ));
    }

    @Test
    public void testPowerWindowSize() throws IOException {

        PowerWindow powerWindow = new PowerWindow(stream, 100);
        assertEquals(100, powerWindow.size());
    }

    @Test
    public void testPowerWindowPosition(){

    }
}