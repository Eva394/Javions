package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.InputStream;

public final class PowerComputer {


    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument( ( batchSize > 0 ) && ( batchSize % 8 == 0 ) );
    }


    public readBatch(short[] batch) {

    }


    public int getBytesRead() {
        return samplesDecoder.getBytesRead;
        //TODO i think my way of doing this is not the right one
        //TODO when everything is pulled get the right name for the
        // instance of SamplesDecoder
    }
}
