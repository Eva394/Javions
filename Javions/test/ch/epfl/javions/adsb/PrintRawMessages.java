package ch.epfl.javions.adsb;
/*
 *  Author :        Mangano Eva
 *  Date :          16/03/2023
 */


import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class PrintRawMessages {

    public static void main(String[] args) throws IOException {
        String f = "samples_20230304_1442.bin";
        try ( InputStream s = new FileInputStream( f ) ) {
            AdsbDemodulator d = new AdsbDemodulator( s );
            RawMessage m;
            while ( ( m = d.nextMessage() ) != null ) {
                System.out.println( m );
            }
        }
    }
}