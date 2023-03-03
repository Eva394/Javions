package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDatabaseTest {

    @Test
    void AircraftDatabaseWorksForValidInput() throws IOException {
        assertEquals(new AircraftData(new AircraftRegistration("HB-JDC"),
                new AircraftTypeDesignator("A20N"),
                "AIRBUS A-320neo",
                new AircraftDescription("L2J"),
                WakeTurbulenceCategory.of("M")),
                new AircraftDatabase("C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\aircraft.zip").get(new IcaoAddress("4B1814")));
    }

    @Test
    void AircraftDatabaseWorksForInValidInput() throws IOException {
        assertNull(new AircraftDatabase("C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\aircraft.zip").get(new IcaoAddress("4B9912")));
    }

    //TODO fileName = getClass().getResource("/aircraft.zip").getFile();
    //        fileName = URLDecoder.decode(fileName, UTF_8);
    //prof has asked us to use these in test


}