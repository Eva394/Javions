package ch.epfl.javions;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IcaoAddressTest {

    @Test
    void IcaoAddressConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B1814J"));
        assertDoesNotThrow(() -> new IcaoAddress("4B1814"));
    }

}