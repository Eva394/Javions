package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {

    private SamplesDecoder sampleDecoder;
    private final int batchSize = 1 << 16;
    private final int numberOfSamples = 1201;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        //InputStream stream = new FileInputStream(new File("C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin"));
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File("C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\samples.bin"))));
        sampleDecoder = new SamplesDecoder(stream, batchSize);
    }


    @Test
    public void testReadbatchReturnsCorrectValue() throws IOException {
        short[] batch = new short [batchSize];
        sampleDecoder.readBatch(batch);
        for (int i = 0; i < 10; i++) {
            System.out.println(batch[i]);
        }
    }

}