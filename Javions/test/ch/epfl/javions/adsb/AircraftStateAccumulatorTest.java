package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import org.junit.jupiter.api.Test;

class AircraftStateAccumulatorTest {

    @Test
    void testAircraftStateAccumulatorStoresLastOddAndEvenPositionMessage() {
        AircraftStateAccumulator accumulator = new AircraftStateAccumulator<>( new AircraftStateSetter() {
            @Override
            public void setLastMessageTimeStampNs(long timeStampNs) {

            }


            @Override
            public void setCategory(int category) {

            }


            @Override
            public void setCallSign(CallSign callSign) {

            }


            @Override
            public void setPosition(GeoPos position) {

            }


            @Override
            public void setAltitude(double altitude) {

            }


            @Override
            public void setVelocity(double velocity) {

            }


            @Override
            public void setTrackOrHeading(double trackOrHeading) {

            }
        } )
    }
}