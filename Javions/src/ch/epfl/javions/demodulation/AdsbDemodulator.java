package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a demodulator of ADS-B messages
 */
public final class AdsbDemodulator {

    private static final int WINDOW_SIZE = 1200;
    private static final int TO_NANOSECONDS = 100;
    private final PowerWindow powerWindow;
    private long timestampNs;


    /**
     * Constructor. Builds an instance of AdsbDemodulator
     * @param samplesStream input stream
     * @throws IOException if there is an input/output error
     * @author Eva Mangano 345375
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.powerWindow = new PowerWindow( samplesStream, WINDOW_SIZE );
    }


    private static boolean windowContainsPreamble(int previousSpikesSum, int nextSpikesSum, int currentValleySum,
                                                  int currentSpikesSum) {
        return ( previousSpikesSum < currentSpikesSum ) && ( currentSpikesSum > nextSpikesSum ) && ( currentSpikesSum
                                                                                                     >= 2
                                                                                                        * currentValleySum );
    }


    /**
     * Finds the next message in the stream
     * @return the message
     * @throws IOException if there is an input/output error
     * @author Eva Mangano 345375
     */
    public RawMessage nextMessage() throws IOException {

        byte[] bytes = new byte[RawMessage.LENGTH];

        int[] spikesIndexes = new int[]{0, 10, 35, 45};
        int[] valleyIndexes = new int[]{5, 15, 20, 25, 30, 40};

        int previousSpikesSum = 0;

        while ( powerWindow.isFull() ) {
            int nextSpikesSum = 0;
            int currentValleySum = 0;
            int currentSpikesSum = 0;

            for ( int spikesIndex : spikesIndexes ) {
                currentSpikesSum += powerWindow.get( spikesIndex );
                nextSpikesSum += powerWindow.get( spikesIndex + 1 );
            }

            for ( int valleyIndex : valleyIndexes ) {
                currentValleySum += powerWindow.get( valleyIndex );
            }

            if ( windowContainsPreamble( previousSpikesSum, nextSpikesSum, currentValleySum, currentSpikesSum ) ) {
                timestampNs = powerWindow.position() * TO_NANOSECONDS;

                for ( int i = 0 ; i < RawMessage.LENGTH ; i++ ) {
                    computeByte( bytes, i );
                }
                if ( RawMessage.size( bytes[0] ) == RawMessage.LENGTH ) {
                    RawMessage rawMessage = RawMessage.of( timestampNs, bytes );
                    if ( rawMessage != null ) {
                        powerWindow.advanceBy( WINDOW_SIZE );
                        return rawMessage;
                    }
                }
            }

            previousSpikesSum = currentSpikesSum;
            powerWindow.advance();
        }

        return null;
    }


    private void computeByte(byte[] bytes, int index) {
        int previousByte = 0;
        int currentByte;

        for ( int bit = 0 ; bit < Byte.SIZE ; bit++ ) {
            currentByte = getBit( index * Byte.SIZE + bit );
            previousByte = ( ( previousByte << 1 ) | currentByte );
        }

        bytes[index] = (byte)previousByte;
    }


    private int getBit(int i) {
        return ( powerWindow.get( 80 + 10 * i ) < powerWindow.get( 85 + 10 * i ) ? 0 : 1 );
    }
}