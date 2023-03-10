package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {

    byte[] data = new byte[1024];
    InputStream stream = new ByteArrayInputStream( data );
    private final int batchSize = 1 << 16;


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

        PowerWindow Window = new PowerWindow( stream, 16 );
        assertEquals( 16, Window.size() );
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

    @Test
    public void testAdvance() throws IOException {

/*

        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        /*for (int i=0; i<bytes.length; i++){
            System.out.println(bytes[i] + " ");
        }

        for (int i=0; i<bytes.length; i++){
            int data = stream.read();
            System.out.print(data + " ");
        }
        */

        DataInputStream stream = new DataInputStream( new BufferedInputStream(
                new FileInputStream( new File( "C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin" ) ) ) );

        System.out.println(stream);
        PowerWindow window = new PowerWindow(stream, 8);

        window.advance();
        window.advance();
        window.advance();

        assertEquals(3, window.position());

        assertEquals(745, window.get(0));

        assertEquals(98, window.get(1));

        window.advance();

        assertEquals(4, window.position());

        assertEquals(98, window.get(0));
    }

    /*
    @Test
    public void testPowerWindowGet(){
        assertThrows( IndexOutOfBoundsException.class, () -> new get());
    }

     */


    @Test
    void testIsFullReturnsTrueWhenWindowIsFull() {
        PowerWindow powerWindow = null;
        try {
            powerWindow = new PowerWindow(stream, batchSize);

            int i = 0 ;

        while (powerWindow.isFull()) {
            powerWindow.advance();
            System.out.println(powerWindow.position());
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            System.out.println(powerWindow.position());

        }

    }
}