package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {

    private int batchSize;
    private final SamplesDecoder decoder;


    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize > 0) && (batchSize % 8 == 0));


        this.decoder = new SamplesDecoder(stream, batchSize);
        this.batchSize = batchSize;
    }


    public readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length = batchSize);

        short[] samples = new short[batchSize*2];
        int powerSamples = decoder.readBatch(samples);


        for (int i = 0; i < batchSize ; i +=8){
            int inphase = samples[i + 2] - samples[i + 4] + samples[i + 6] - samples[i];
            int quadrature = samples[i + 1] - samples[i + 3] + samples[i + 5] - samples[i + 7];
            batch[i / 8] = (short) (inphase * inphase + quadrature * quadrature);
        }

        return powerSampels;

    }
}
