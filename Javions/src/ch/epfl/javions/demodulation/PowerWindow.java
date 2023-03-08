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
    private int position;


    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument( ( windowSize > 0 ) && ( windowSize <= ( 1 << 16 ) ) );
        this.stream = stream;
        this.powerComputer = new PowerComputer( stream, windowSize );
        this.windowSize = windowSize;
        this.position = 0;
    }


    public int getWindowSize() {
        return windowSize;
    }


    public int getPosition() {
        return position;
    }


    public boolean isFull() {
        return ( );
        //TODO idk if this is the right way and if it is
        // should i divide by 2 or 4 ?
    }


    public int get(int i) {
        if ( i < 0 || i > windowSize ) {
            throw new IndexOutOfBoundsException();
        }

        if ( i < windowSize ) {
            return tab1[i];
        }

        return tab2[i];
    }


    public void advance() throws IOException {
        //TODO what???
    }


    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument( offset >= 0 );
        //TODO what???
    }
}
