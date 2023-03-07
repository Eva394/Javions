package ch.epfl.javions.demodulation;
/*
 *  Author :        Mangano Eva
 *  Date :          03/03/2023
 */


import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a sample decoder, which transforms the bytes from the AirSpy into signed 12-bit samples
 *
 * @author Eva Mangano 345375
 */
public final class SamplesDecoder {

    private static final int BIAIS = 1 << 11;
    private byte[] sample;
    private InputStream stream;
    private int batchSize;


    /**
     * Constructor. Builds an instance of SamplesDecoder.
     *
     * @param stream
     * @param batchSize
     * @author Eva Mangano 345375
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument( batchSize > 0 );
        if ( stream == null ) {
            throw new NullPointerException();
        }

        this.stream = stream;
        this.sample = new byte[batchSize * 2];
        this.batchSize = batchSize;
    }


    public void readBatch(short[] batch) throws IOException {
        if ( batch.length != batchSize ) {
            throw new IllegalArgumentException();
        }
        stream.readNBytes( sample, 0, batchSize );

        for ( int i = 0 ; i < batch.length / 2 ; i++ ) {
            for ( int j = 0 ; j < batchSize ; j++ ) {
                int a = Byte.toUnsignedInt( sample[j] );
                int b = Byte.toUnsignedInt( sample[j + 1] );
                batch[i] = (short)( ( ( b << Byte.SIZE ) | a ) - BIAIS );
            }
        }
    }
}
