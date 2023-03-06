package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftRegistrationTest {

    @Test
    void AircraftRegistrationConstraints() {

        assertThrows( IllegalArgumentException.class, () -> new AircraftRegistration( "" ) );
        assertThrows( IllegalArgumentException.class, () -> new AircraftRegistration( "HB*AHHJC" ) );
        assertDoesNotThrow( () -> new AircraftRegistration( "HB-JDC" ) );
    }


    void aircraftRegistrationConstructorThrowsWithInvalidRegistration() {
        assertThrows( IllegalArgumentException.class, () -> {
            new AircraftRegistration( "abc" );
        } );
    }


    @Test
    void aircraftRegistrationConstructorThrowsWithEmptyRegistration() {
        assertThrows( IllegalArgumentException.class, () -> {
            new AircraftRegistration( "" );
        } );
    }


    @Test
    void aircraftRegistrationConstructorAcceptsValidRegistration() {
        assertDoesNotThrow( () -> {
            new AircraftRegistration( "F-HZUK" );
        } );
    }
}