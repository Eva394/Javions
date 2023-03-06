package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CallSignTest {

    @Test
    void AircraftTypeDesignatorTestConstraints() {
        assertThrows( IllegalArgumentException.class, () -> new CallSign( "0000000000" ) );
        assertDoesNotThrow( () -> new CallSign( "DD883346" ) );
        assertDoesNotThrow( () -> new CallSign( "" ) );
    }


    void callSignConstructorThrowsWithInvalidCallSign() {
        assertThrows( IllegalArgumentException.class, () -> {
            new CallSign( "callsign" );
        } );
    }


    @Test
    void callSignConstructorAcceptsEmptyCallSign() {
        assertDoesNotThrow( () -> {
            new CallSign( "" );
        } );
    }


    @Test
    void callSignConstructorAcceptsValidCallSign() {
        assertDoesNotThrow( () -> {
            new CallSign( "AFR39BR" );
        } );
    }
}