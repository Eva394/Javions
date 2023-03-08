package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;


public final class PowerComputer {

    private final InputStream stream;
    private final byte[] sample;
    private int batchSize;

    public PowerComputer(InputStream stream, int batchSize) {

        Preconditions.checkArgument(batchSize > 0 && batchSize % 8 == 0);

        this.stream = stream;
        this.sample = new byte[batchSize*2];
        this.batchSize = batchSize;

    }

    public int readBatch(int[] batch) throws IOException{

        Preconditions.checkArgument(batch.length == batchSize);

        int byteReads;
        byteReads = stream.readNBytes(sample, 0, batchSize);

        for (int i = 0; i < batchSize ; i +=8){

            int inphase = sample[i + 2] - sample[i + 4] + sample[i + 6] - sample[i];
            int quadrature = sample[i + 1] - sample[i + 3] + sample[i + 5] - sample[i + 7];
            batch[i] = inphase * inphase + quadrature * quadrature;
        }

        return byteReads;
    }

}
