package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {


    private final long timeStampNs;
    private InputStream stream;
    private int index =0;
    private RawMessage rawMessage;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.samples = new byte[1200];
        this.timeStampNs = timeStampNs;
        this.rawMessage = new RawMessage(timeStampNs,samples);
    }

    public RawMessage nextMessage() throws IOException{
        for ( int i = 0 ; i <= 1200 ; i ++ ) {

            if (i == 1200){
                return null;
            }

            return new RawMessage(timeStampNs, samples[i]);

        }


    }
}
