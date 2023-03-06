package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {

    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument((batchSize > 0) && (batchSize % 8 == 0));
    }

    public int readBatch(int[] Batch) throws IOException {



    }
}
