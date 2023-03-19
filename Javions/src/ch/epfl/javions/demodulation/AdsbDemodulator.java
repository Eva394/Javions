package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;


public final class AdsbDemodulator {

    /**
     * the size of a window
     */
    public static final int WINDOW_SIZE = 1200;
    private final PowerWindow powerWindow;
    private long timestampNs = 0;


    /**
     * Constructor. Builds an instance of AdsbDemodulator
     *
     * @param samplesStream input stream
     * @throws IOException if there is an input/output error
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.powerWindow = new PowerWindow( samplesStream, WINDOW_SIZE );
    }


    public RawMessage nextMessage() throws IOException {

        byte[] bytes = new byte[RawMessage.LENGTH];

        int[] spikesIndexes = new int[]{0, 10, 35, 45};
        int[] valleyIndexes = new int[]{5, 15, 20, 25, 30, 40};

        int currentSpikesSum = 0;
        int previousSpikesSum = 0;

        while ( powerWindow.isFull() ) {

            int nextSpikesSum = 0;
            int currentValleySum = 0;
            currentSpikesSum = 0;

            for ( int i = 0 ; i < spikesIndexes.length ; i++ ) {
                currentSpikesSum += powerWindow.get( spikesIndexes[i] );
                nextSpikesSum += powerWindow.get( spikesIndexes[i] + 1 );
            }

            for ( int i = 0 ; i < valleyIndexes.length ; i++ ) {
                currentValleySum += powerWindow.get( valleyIndexes[i] );
            }

            if ( ( previousSpikesSum < currentSpikesSum ) && ( currentSpikesSum > nextSpikesSum ) && ( currentSpikesSum
                                                                                                       >= 2
                                                                                                          * currentValleySum ) ) {
                timestampNs = powerWindow.position() * 100;

                for ( int i = 0 ; i < RawMessage.LENGTH ; i++ ) {
                    computeByte( bytes, i );
                }
                if ( RawMessage.size( bytes[0] ) == RawMessage.LENGTH ) {
                    RawMessage a = RawMessage.of( timestampNs, bytes );
                    if ( a != null ) {
                        powerWindow.advanceBy( WINDOW_SIZE );
                        return a;
                    }
                }
            }

            previousSpikesSum = currentSpikesSum;
            currentSpikesSum = nextSpikesSum;
            powerWindow.advance();
        }

        return null;
    }


    private void computeByte(byte[] bytes, int i) {
        int previousByte = 0;
        int currentByte;

        for ( int bit = 0 ; bit < Byte.SIZE ; bit++ ) {
            currentByte = getBit( i * Byte.SIZE + bit );
            previousByte = ( ( previousByte << 1 ) | currentByte );
        }

        bytes[i] = (byte)previousByte;
    }


    private int getBit(int i) {
        return ( powerWindow.get( 80 + 10 * i ) < powerWindow.get( 85 + 10 * i ) ? 0 : 1 );
    }
}