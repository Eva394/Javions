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
    private int bytesRead;


    /**
     * Constructor. Builds an instance of SamplesDecoder.
     *
     * @param stream    input stream containing the bytes from the AirSpy radio
     * @param batchSize size of the batches
     * @throws IllegalArgumentException if the size of the batches is not positive
     * @throws NullPointerException     if the flow is null
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


    /**
     * Reads and converts the batches of bytes from the stream to an array of samples of signed shorts. Stores it in the
     * array <code>batch</code>
     *
     * @param batch array to fill with the samples
     * @throws IOException              if there is an input/output error
     * @throws IllegalArgumentException if the size of <code>batch</code> is not equal to <code>batchSize</code>
     * @author Eva Mangano 345375
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument( batch.length == batchSize );

        bytesRead = stream.readNBytes( sample, 0, batchSize );

        for ( int i = 0 ; i < batch.length / 2 ; i++ ) {
            for ( int j = 0 ; j < batchSize ; j++ ) {
                int a = Byte.toUnsignedInt( sample[j] );
                int b = Byte.toUnsignedInt( sample[j + 1] );
                batch[i] = (short)( ( ( b << Byte.SIZE ) | a ) - BIAIS );
            }
        }

        return bytesRead;
    }
}

