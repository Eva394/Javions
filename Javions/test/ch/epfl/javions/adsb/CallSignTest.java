package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallSignTest {

    @Test
    void AircraftTypeDesignatorTestConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new CallSign("0000000000"));
        assertDoesNotThrow(() -> new CallSign("DD883346"));
        assertDoesNotThrow(() -> new CallSign(""));
    }

}