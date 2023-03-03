package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationTest {

    @Test
    void AircraftRegistrationConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("HB*AHHJC"));
        assertDoesNotThrow(() -> new AircraftRegistration("HB-JDC"));
    }

}