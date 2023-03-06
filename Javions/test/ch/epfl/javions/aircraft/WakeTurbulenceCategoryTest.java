package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WakeTurbulenceCategoryTest {

    @Test
    void WakeTurbulenceCategoryTest() {
        assertEquals( WakeTurbulenceCategory.LIGHT, WakeTurbulenceCategory.of( "L" ) );
        assertEquals( WakeTurbulenceCategory.MEDIUM, WakeTurbulenceCategory.of( "M" ) );
        assertEquals( WakeTurbulenceCategory.HEAVY, WakeTurbulenceCategory.of( "H" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "Mtsha" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "" ) );
    }


    void wakeTurbulenceCategoryOfWorks() {
        assertEquals( WakeTurbulenceCategory.LIGHT, WakeTurbulenceCategory.of( "L" ) );
        assertEquals( WakeTurbulenceCategory.MEDIUM, WakeTurbulenceCategory.of( "M" ) );
        assertEquals( WakeTurbulenceCategory.HEAVY, WakeTurbulenceCategory.of( "H" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "X" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "l" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "m" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "h" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "" ) );
        assertEquals( WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of( "LIGHT" ) );
    }
}