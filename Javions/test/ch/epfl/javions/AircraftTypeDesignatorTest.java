package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftTypeDesignatorTest {

    @Test
    void AircraftTypeDesignatorTestConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A20NA"));
        assertDoesNotThrow(() -> new AircraftTypeDesignator("A20N"));
        assertDoesNotThrow(() -> new AircraftTypeDesignator(""));
    }

}