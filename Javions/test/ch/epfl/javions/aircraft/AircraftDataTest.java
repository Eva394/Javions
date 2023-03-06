package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftDataTest {

    @Test
    void AircraftDataTestConstraints() {

        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");
        String model = "";
        AircraftDescription description = new AircraftDescription( "L2J" );
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of( "M" );

        assertThrows( NullPointerException.class,
                      () -> new AircraftData( null, typeDesignator, model, description, wakeTurbulenceCategory ) );
        assertDoesNotThrow(
                () -> new AircraftData( registration, typeDesignator, model, description, wakeTurbulenceCategory ) );
    }


    void aircraftDataConstructorThrowsWithNullAttribute() {
        var registration = new AircraftRegistration( "HB-JAV" );
        var typeDesignator = new AircraftTypeDesignator( "B738" );
        var model = "Boeing 737-800";
        var description = new AircraftDescription( "L2J" );
        var wakeTurbulenceCategory = WakeTurbulenceCategory.LIGHT;
        assertThrows( NullPointerException.class, () -> {
            new AircraftData( null, typeDesignator, model, description, wakeTurbulenceCategory );
        } );
        assertThrows( NullPointerException.class, () -> {
            new AircraftData( registration, null, model, description, wakeTurbulenceCategory );
        } );
        assertThrows( NullPointerException.class, () -> {
            new AircraftData( registration, typeDesignator, null, description, wakeTurbulenceCategory );
        } );
        assertThrows( NullPointerException.class, () -> {
            new AircraftData( registration, typeDesignator, model, null, wakeTurbulenceCategory );
        } );
        assertThrows( NullPointerException.class, () -> {
            new AircraftData( registration, typeDesignator, model, description, null );
        } );
    }
}