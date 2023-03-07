package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {


    private InputStream stream;
    private PowerComputer powerComputer;
    private byte[] tab1;
    private byte[] tab2;
    private int windowSize;


    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument( ( windowSize > 0 ) && ( windowSize <= ( 1 << 16 ) ) );
        this.stream = stream;
        this.powerComputer = new PowerComputer( stream, windowSize );
        this.windowSize = windowSize;
    }
}
