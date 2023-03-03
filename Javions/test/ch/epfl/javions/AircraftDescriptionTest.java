package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDescriptionTest {

    @Test
    void AircraftDescriptionConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("C8E"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("D7E"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("A0A"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L2JA"));
        assertDoesNotThrow(() -> new AircraftDescription("L2J"));
        assertDoesNotThrow(() -> new AircraftDescription(""));
    }

}