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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class AircraftStateManager {

    private final static double ONE_MINUTE_IN_NANOSECONDS = 6 * ( 1e10 );
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> receivedStates;
    private final ObservableSet<ObservableAircraftState> knownStates;
    private final AircraftDatabase aircraftDatabase;
    private long lastUpdatedTimeStampNs;


    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.aircraftDatabase = aircraftDatabase;
        this.receivedStates = new HashMap<>();
        this.knownStates = FXCollections.emptyObservableSet();
        this.lastUpdatedTimeStampNs = 0L;
    }


    public ObservableSet<ObservableAircraftState> states() {
        return FXCollections.unmodifiableObservableSet( knownStates );
    }


    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress icaoAddress = message.icaoAddress();
        if ( receivedStates.containsKey( icaoAddress ) ) {
            AircraftStateAccumulator<ObservableAircraftState> stateAccumulator = receivedStates.get( icaoAddress );
            stateAccumulator.update( message );
            knownStates.add( new ObservableAircraftState( icaoAddress, aircraftDatabase.get( icaoAddress ) ) );
            //if smth doesn't work try adding this : receivedStates.replace( icaoAddress, stateAccumulator );
        }
        else {
            ObservableAircraftState aircraftState = new ObservableAircraftState( icaoAddress,
                                                                                 aircraftDatabase.get( icaoAddress ) );
            AircraftStateAccumulator<ObservableAircraftState> newStateAccumulator = new AircraftStateAccumulator<>(
                    aircraftState );
            receivedStates.put( icaoAddress, newStateAccumulator );
        }
        lastUpdatedTimeStampNs = message.timeStampNs();
    }


    public void purge() {
        knownStates.removeIf(
                state -> lastUpdatedTimeStampNs - ONE_MINUTE_IN_NANOSECONDS > state.getLastMessageTimeStampNs() );
    }
}
