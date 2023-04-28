package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Collection of the fixed data of an aircraft
 * @param registration           registration of the aircraft
 * @param typeDesignator         type of the aircraft
 * @param model                  model of the aircraft
 * @param description            description of the aircraft
 * @param wakeTurbulenceCategory turbulence category of the wake of the aircraft
 * @author Eva Mangano 345375
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {


    /**
     * Constructor. Builds an instance of AircraftData and verifies if the arguments are non-null
     * @param registration           registration of the aircraft
     * @param typeDesignator         type of the aircraft
     * @param model                  model of the aircraft
     * @param description            description of the aircraft
     * @param wakeTurbulenceCategory turbulence category of the aircraft
     * @throws NullPointerException if one of the parameters is null
     * @author Eva Mangano 345375
     */
    public AircraftData {
        Objects.requireNonNull( registration );
        Objects.requireNonNull( typeDesignator );
        Objects.requireNonNull( model );
        Objects.requireNonNull( description );
        Objects.requireNonNull( wakeTurbulenceCategory );
    }
}
