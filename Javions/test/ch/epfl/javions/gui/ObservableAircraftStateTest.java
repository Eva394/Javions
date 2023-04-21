package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Set;

class ObservableAircraftStateTest {
    public static void main(String[] args) {
        try ( DataInputStream s = new DataInputStream( new BufferedInputStream( new FileInputStream(
                "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\messages_20230318_0915.bin" ) ) ) ) {

            byte[] bytes = new byte[RawMessage.LENGTH];
            AircraftDatabase aircraftDatabase = new AircraftDatabase(
                    "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\aircraft"
                    + ".zip" );
            AircraftStateManager stateManager = new AircraftStateManager( aircraftDatabase );
            int index = 0;
            //            Set<ObservableAircraftState> states = new HashSet<>();
            System.out.println(
                    "OACI    Indicatif Immat.     Modèle                               Longitude              Latitude "
                    + "            Alt.  " + "                Vit" + ".\n"
                    + "―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――" );
            while ( index < 1e2 ) {
                index++;
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes( bytes, 0, bytes.length );
                assert bytesRead == RawMessage.LENGTH;
                assert bytes != null;

                ByteString message = new ByteString( bytes );
                assert message != null;

                RawMessage rawMessage = RawMessage.of( timeStampNs, bytes );
                assert rawMessage != null;

                Message theMessage = MessageParser.parse( rawMessage );
                assert theMessage != null;

                //                ObservableAircraftState newState = new ObservableAircraftState( theMessage.icaoAddress(),
                //                                                                                aircraftDatabase.get(
                //                                                                                        theMessage.icaoAddress() ) );
                //                if ( newState.getPosition() != null ) {
                //                    states.add( newState );
                //                }

                Set<ObservableAircraftState> states = stateManager.states();
                stateManager.updateWithMessage( theMessage );
                for ( ObservableAircraftState state : states ) {
                    System.out.printf( "%s %17s %35s %24s %21s %20s %20s\n", state.getIcaoAddress()
                                                                                  .string()
                            /*callsign*/, state.getAircraftData()
                                               .registration()
                                               .string(), state.getAircraftData()
                                                               .model(), Units.convertTo( state.getPosition()
                                                                                               .longitude(),
                                                                                          Units.Angle.DEGREE ),
                                       Units.convertTo( state.getPosition()
                                                             .latitude(), Units.Angle.DEGREE ), state.getAltitude(),
                                       Units.convert( state.getVelocity(), Units.Speed.,
                                                      Units.Speed.KILOMETER_PER_HOUR ) );
                }
            }
        }
        catch ( IOException e ) {
        }
    }


    private static class AddressComparator implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1, ObservableAircraftState o2) {
            String s1 = o1.getIcaoAddress()
                          .string();
            String s2 = o2.getIcaoAddress()
                          .string();
            return s1.compareTo( s2 );
        }
    }
}