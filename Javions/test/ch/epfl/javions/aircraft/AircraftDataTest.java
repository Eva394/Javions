package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDataTest {

    @Test
    void AircraftDataTestConstraints() {

        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");
        String model = "";
        AircraftDescription description = new AircraftDescription("L2J");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("M");


        assertThrows(NullPointerException.class, () -> new AircraftData(null,typeDesignator,model,description,wakeTurbulenceCategory));
        assertDoesNotThrow(() -> new AircraftData(registration,typeDesignator,model,description,wakeTurbulenceCategory));
    }





}