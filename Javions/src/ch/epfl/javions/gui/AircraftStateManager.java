package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          18/04/2023
 */


import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashMap;
import java.util.Map;

public final class AircraftStateManager {

    private final static double ONE_MINUTE_IN_NANOSECONDS = 6 * ( 1e10 );
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> receivedStates;
    private final ObservableSet<ObservableAircraftState> knownStates;
    private final AircraftDatabase aircraftDatabase;


    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.aircraftDatabase = aircraftDatabase;
        this.receivedStates = new HashMap<>();
        this.knownStates = FXCollections.emptyObservableSet();
    }


    public ObservableSet<ObservableAircraftState> states() {
        return FXCollections.unmodifiableObservableSet( knownStates );
    }


    public void updateWithMessage(Message message) {
        IcaoAddress icaoAddress = message.icaoAddress();
        if ( receivedStates.containsKey( icaoAddress ) ) {
            AircraftStateAccumulator<ObservableAircraftState> stateAccumulator = receivedStates.get( icaoAddress );
            stateAccumulator.update( message );
            //TODO next line : idk how to get the AircraftDatabase other than this but the énoncé doesn't say
            // anything about get being static so...
            knownStates.add( new ObservableAircraftState( icaoAddress, AircraftDatabase.get( icaoAddress ) ) );
            //TODO if smth doesn't work try adding this : receivedStates.replace( icaoAddress, stateAccumulator );
        }
        else {
            //TODO what do i put in AircraftStateAccumulator<>()
            receivedStates.put( icaoAddress, new AircraftStateAccumulator<>() )
        }
    }
}
