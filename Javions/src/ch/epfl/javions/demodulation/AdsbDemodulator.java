package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

//  package ch.epfl.javions.demodulation;
//
//  import ch.epfl.javions.ByteString; import ch.epfl.javions.adsb.RawMessage;
//  import java.io.IOException; import java.io.InputStream;
//  public final class AdsbDemodulator {
//  private final long timeStampNs; private InputStream stream; private byte[] samples; private int index = 0; private
//  RawMessage rawMessage;
//
//  public AdsbDemodulator(InputStream samplesStream) throws IOException { this.samples = new byte[1200];
//  this.timeStampNs = timeStampNs;
//  this.rawMessage = new RawMessage( timeStampNs, samples. ); }
//
//  public RawMessage nextMessage() throws IOException { for ( int i = 0 ; i <= 1200 ; i++ ) {
//  if ( i == 1200 ) { return null; }
//  byte a = samples[i]; } } }

//I couldn't get my head into the code so I started again from scratch to see if I understood what to do, but I got
// nowhere




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

    /*
     while ( !powerWindow.isFull() ) {
            powerWindow.advance();
        }
    long[] samples = new long[powerWindow.size()];

        for ( int i = 0 ; i < powerWindow.size() ; i++ ) {
            samples[i] = powerWindow.get( i );
        }

        byte[] messageBytes = new byte[RawMessage.LENGTH];

        for ( int i = 0 ; i < RawMessage.LENGTH ; i++ ) {
            messageBytes[i] = (byte)( powerWindow.get( i + 34 ) >> Byte.SIZE );
        }

        powerWindow.advanceBy( Byte.SIZE );

        return new RawMessage( samples, powerWindow.position() - powerWindow.size() );
     */


    public RawMessage nextMessage() throws IOException {

        byte[] bytes = new byte[WINDOW_SIZE / 10];

        boolean foundPreamble = false;

        int[] spikesIndexes = new int[]{0, 10, 35, 45};
        int[] valleyIndexes = new int[]{5, 15, 20, 25, 30, 40};

        int currentSpikesSum = 0;

        for ( int i = 0 ; i < spikesIndexes.length ; i++ ) {
            currentSpikesSum += powerWindow.get( spikesIndexes[i] );
        }
        int previousSpikesSum = currentSpikesSum;

        while ( powerWindow.isFull() && !foundPreamble ) {
            int nextSpikesSum = 0;
            int currentValleySum = 0;

            for ( int i = 0 ; i < spikesIndexes.length ; i++ ) {
                currentSpikesSum += powerWindow.get( spikesIndexes[i] );
                nextSpikesSum += powerWindow.get( spikesIndexes[i + 1] );
            }

            for ( int i = 0 ; i < valleyIndexes.length ; i++ ) {
                currentValleySum += powerWindow.get( valleyIndexes[i] );
            }

            if ( ( previousSpikesSum < currentSpikesSum ) && ( currentSpikesSum > nextSpikesSum ) && ( currentSpikesSum
                                                                                                       >= 2
                                                                                                          * currentValleySum ) ) {
                timestampNs = powerWindow.position() * 100;

                for ( int i = 0 ; i < WINDOW_SIZE / 10 ; i++ ) {
                    if ( powerWindow.get( 80 + 10 * i ) < powerWindow.get( 85 + 10 * i ) ) {
                        bytes[i] = 0;
                    }
                    else {
                        bytes[i] = 1;
                    }
                }

                if ( bytes.length == RawMessage.LENGTH ) {
                    foundPreamble = true;
                }
            }

            else {
                previousSpikesSum = currentSpikesSum;
                currentSpikesSum = nextSpikesSum;
                powerWindow.advance();
            }
        }

        return RawMessage.of( timestampNs, bytes );
    }
}