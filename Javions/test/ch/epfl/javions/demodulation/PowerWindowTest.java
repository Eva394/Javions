package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {

    private final int batchSize = 1 << 16;
    byte[] data = new byte[1024];
    InputStream stream = new ByteArrayInputStream( data );


    @Test
    public void testPowerWindowConstructorWithIllegalWindowSize() throws IOException {

        assertThrows( IllegalArgumentException.class, () -> new PowerWindow( stream, 0 ) );
        assertThrows( IllegalArgumentException.class, () -> new PowerWindow( stream, 1 << 16 + 1 ) );
        assertDoesNotThrow( () -> new PowerWindow( stream, batchSize ) );
    }


    @Test
    public void testPowerWindowSize() throws IOException {

        PowerWindow powerWindow = new PowerWindow( stream, 80 );
        assertEquals( 80, powerWindow.size() );
    }


    @Test
    public void testPowerWindowPosition() throws IOException {

        PowerWindow powerWindow = new PowerWindow( stream, 80 );

        assertEquals( 0, powerWindow.position() );

        int offset = 10;
        powerWindow.advanceBy( offset );
        assertEquals( offset, powerWindow.position() );

        offset = 50;
        powerWindow.advanceBy( offset );
        assertEquals( offset + 10, powerWindow.position() );
    }

    //    void testIsFullReturnsTrueWhenWindowIsFull() {
    //        PowerWindow powerWindow = null;
    //        try {
    //            powerWindow = new PowerWindow( stream, 24 );
    //        }catch (  )
    //    }


    @Test
    public void testPowerWindowGet() throws IOException {
        PowerWindow powerWindow = new PowerWindow( stream, 80 );

        assertThrows( IndexOutOfBoundsException.class, () -> powerWindow.get( 80 ) );
    }

    //    void testIsFullReturnsTrueWhenWindowIsFull() {
    //        PowerWindow powerWindow = null;
    //        try {
    //            powerWindow = new PowerWindow(stream, batchSize);
    //
    //            int i = 0 ;
    //
    //        while (powerWindow.isFull()) {
    //            powerWindow.advance();
    //            System.out.println(powerWindow.position());
    //            }
    //
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //
    //        } finally {
    //            System.out.println(powerWindow.position());
    //
    //        }
    //
    //            int i = 0;
    //
    //            while ( powerWindow.isFull() ) {
    //                powerWindow.advance();
    //                System.out.println( powerWindow.position() );
    //            }
    //        }
    //        catch ( IOException e ) {
    //            e.printStackTrace();
    //        }
    //        finally {
    //            //System.out.println( powerWindow.position() );
    //        }
}