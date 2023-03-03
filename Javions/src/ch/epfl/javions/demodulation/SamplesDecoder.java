package ch.epfl.javions.demodulation;
/*
 *  Author :        Mangano Eva
 *  Date :          03/03/2023
 */


import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eva Mangano 345375 Represents a sample decoder, which transforms the bytes from the AirSpy into signed 12-bit
 * samples
 */
public final class SamplesDecoder {

    private byte[] sample;
    private InputStream stream;


    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument( batchSize > 0 );
        if ( stream == null ) {
            throw new NullPointerException();
        }

        this.sample = new byte[batchSize];
        this.stream = stream;
    }


    public int readBatch(short[] batch) throws IOException {
        stream.readNBytes( sample, 0, 12 );
    }
}
