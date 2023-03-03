package ch.epfl.javions.aircraft;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WakeTurbulenceCategoryTest {

    @Test
    void WakeTurbulenceCategoryTest() {
        assertEquals(WakeTurbulenceCategory.LIGHT, WakeTurbulenceCategory.of("L"));
        assertEquals(WakeTurbulenceCategory.MEDIUM, WakeTurbulenceCategory.of("M"));
        assertEquals(WakeTurbulenceCategory.HEAVY, WakeTurbulenceCategory.of("H"));
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of("Mtsha"));
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of(""));
    }


}