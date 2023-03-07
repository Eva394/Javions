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
            batch[i / 8] = inphase * inphase + quadrature * quadrature;
        }

        return byteReads;


    }



}

/*public final class PowerComputer {

    private final int batchSize;
    private final SamplesDecoder decoder;


    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize > 0) && (batchSize % 8 == 0));


        this.decoder = new SamplesDecoder(stream, batchSize);
        this.batchSize = batchSize;
        byte[] sample = new byte[batchSize * 2];
    }


    public int readBatch(int[] batch) throws IOException{
        Preconditions.checkArgument(batch.length = batchSize);

        short[] samples = new short[batchSize*2];
        short powerSamples = decoder.readBatch(samples);


        for (int i = 0; i < batchSize ; i +=8){
            int inphase = samples[i + 2] - samples[i + 4] + samples[i + 6] - samples[i];
            int quadrature = samples[i + 1] - samples[i + 3] + samples[i + 5] - samples[i + 7];
            batch[i / 8] = inphase * inphase + quadrature * quadrature;
        }

        return powerSamples;



    }
}
*/