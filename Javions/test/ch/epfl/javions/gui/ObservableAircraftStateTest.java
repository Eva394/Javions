package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

class ObservableAircraftStateTest {
    public static void main(String[] args) {
        try ( DataInputStream s = new DataInputStream( new BufferedInputStream( new FileInputStream(
                "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\messages_20230318_0915.bin" ) ) ) ) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            while ( true ) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes( bytes, 0, bytes.length );
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString( bytes );
                System.out.printf( "%13d: %s\n", timeStampNs, message );
            }
        }
        catch ( IOException e ) {
            //throw new RuntimeException( e );
        }
    }
}