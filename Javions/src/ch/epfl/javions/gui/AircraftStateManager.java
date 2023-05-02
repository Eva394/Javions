package ch.epfl.javions.gui;


import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the states of the aircrafts. Keeps the states of all the aircrafts up tp date with the messages received
 */
public final class AircraftStateManager {

    private final static double ONE_MINUTE_IN_NANOSECONDS = 6 * ( 1e10 );
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> receivedStates;
    private final ObservableSet<ObservableAircraftState> knownStates;
    private final ObservableSet<ObservableAircraftState> unmodifiableKnownStates;
    private final AircraftDatabase aircraftDatabase;
    private long lastUpdatedTimeStampNs;


    /**
     * Constructor. Builds an instance of <code>AircraftStateManager</code>
     * @param aircraftDatabase database for mictronics data of the aircrafts
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.aircraftDatabase = aircraftDatabase;
        this.receivedStates = new HashMap<>();
        this.knownStates = FXCollections.observableSet();
        this.lastUpdatedTimeStampNs = 0L;
        this.unmodifiableKnownStates = FXCollections.unmodifiableObservableSet( knownStates );
    }


    /**
     * Getter for an unmodifiable wrapper list on top of the list <code>knownStates</code>
     * (<code>unmodifiableKnownStates</code>)
     * @return an unmodifiable wrapper list on top of observable list of known states <code>knownStates</code>
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableKnownStates;
    }


    /**
     * Updates the state of the aircraft from which we received the <code>message</code> to the new state contained in
     * the aforementioned message
     * @param message message received from an aircraft
     * @throws IOException if there is an input/output error
     */
    public void updateWithMessage(Message message) throws
                                                   IOException {

        lastUpdatedTimeStampNs = message.timeStampNs();
        IcaoAddress icaoAddress = message.icaoAddress();

        if ( receivedStates.containsKey( icaoAddress ) ) {
            AircraftStateAccumulator<ObservableAircraftState> stateAccumulator = receivedStates.get( icaoAddress );
            stateAccumulator.update( message );
            ObservableAircraftState aircraftState = stateAccumulator.stateSetter();
            if ( aircraftState.getPosition() != null ) {
                knownStates.add( aircraftState );
            }
        }
        else {
            ObservableAircraftState aircraftState = new ObservableAircraftState( icaoAddress,
                                                                                 aircraftDatabase.get( icaoAddress ) );
            AircraftStateAccumulator<ObservableAircraftState> newStateAccumulator = new AircraftStateAccumulator<>(
                    aircraftState );
            newStateAccumulator.update( message );
            receivedStates.put( icaoAddress, newStateAccumulator );
        }
    }


    /**
     * Deletes the states of the aircrafts from which no message has been received for one minute or more since the last
     * message. Deletes it form <code>knownStates</code>
     */
    public void purge() {
        knownStates.removeIf(
                state -> lastUpdatedTimeStampNs - ONE_MINUTE_IN_NANOSECONDS > state.getLastMessageTimeStampNs() );

        //TODO also remove from received state   (how???)
        //        for ( AircraftStateAccumulator<ObservableAircraftState> aircraftState : receivedStates.values() ) {
        //            receivedStates.remove(  )
        //        }
    }
}
