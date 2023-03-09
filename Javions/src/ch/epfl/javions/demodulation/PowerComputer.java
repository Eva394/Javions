package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Power Calculator
 *
 * @autor Nagyung Kim (339628)
 */

public final class PowerComputer {

    private final InputStream stream;
    private final short[] sample;
    private final SamplesDecoder samplesDecoder;
    private final int batchSize;
    private int index = 0;


    /**
     * Construct a power calculator using the given input stream and produce batch power samples of given size
     *
     * @param stream    input stream containing the bytes from the AirSpy radio
     * @param batchSize size of the batches
     */


    public PowerComputer(InputStream stream, int batchSize) {

        Preconditions.checkArgument(batchSize > 0 && batchSize % Byte.SIZE == 0);

        this.samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        this.stream = stream;
        this.sample = new short[Byte.SIZE];
        this.batchSize = batchSize;
    }


    /**
     * returns the number of power samples placed in the array
     *
     * @param batch array to fill with the power samples
     * @return the number of power samples placed in the array
     * @throws IOException              if input/output error
     * @throws IllegalArgumentException if the size of the array passed in argument is not equal to the size of a batch
     */

    public int readBatch(int[] batch) throws IOException {

        Preconditions.checkArgument(batch.length == batchSize);

        short[] newSamples = new short[batchSize * 2];

        int numberOfNewSamples = samplesDecoder.readBatch(newSamples);

        for (int i = 0; i < numberOfNewSamples; i += 2) {

            short even = newSamples[i];
            short odd = newSamples[i + 1];

            sample[index] = even;
            sample[index + 1] = odd;


            int inphase = sample[0] - sample[2] + sample[4] - sample[6];
            int quadrature = sample[1] - sample[3] + sample[5] - sample[7];

            index = index + 2;

            if (index == 8) {
                index = 0;
            }

            batch[i] = inphase * inphase + quadrature * quadrature;
        }

        return numberOfNewSamples/2;
    }
}
