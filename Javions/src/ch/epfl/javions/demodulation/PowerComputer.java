package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;


public final class PowerComputer {

    private final InputStream stream;
    private final short[] sample;
    private final SamplesDecoder samplesDecoder;
    private int batchSize;
    private int index = 0;


    public PowerComputer(InputStream stream, int batchSize) {

        Preconditions.checkArgument(batchSize > 0 && batchSize % 8 == 0);


        this.samplesDecoder = new SamplesDecoder(stream, batchSize*2);
        this.stream = stream;
        this.sample = new short[8];
        this.batchSize = batchSize;

    }

    public int readBatch(int[] batch) throws IOException{

        Preconditions.checkArgument(batch.length == batchSize);

        short[] newSamples = new short[batchSize*2];

        int numberOfNewSamples = samplesDecoder.readBatch(newSamples);

        for (int i = 0; i < batchSize ; i +=2){

            short a = newSamples[i];
            short b = newSamples[i+1];

            sample[index] = a;
            sample[index+1] = b;

            int inphase = sample[0] - sample[2] + sample[4] - sample[6];
            int quadrature = sample[1] - sample[3] + sample[5] - sample[7];

            index = index + 2;

            if (index == 8){
                index = 0;
            }

            batch[i] = inphase * inphase + quadrature * quadrature;
        }

        return numberOfNewSamples/2;
    }

}
