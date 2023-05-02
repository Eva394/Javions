package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class AircraftControllerTest extends Application {
    public static void main(String[] args) {
        launch( args );
    }


    @Override
    public void start(Stage primaryStage) throws
                                          Exception {
        Path tileCache = Path.of( "tile-cache" );
        TileManager tm = new TileManager( tileCache, "tile.openstreetmap.org" );
        MapParameters mp = new MapParameters( 17, 17_389_327, 11_867_430 );
        BaseMapController bmc = new BaseMapController( tm, mp );

        // Création de la base de données
        URL dbUrl = getClass().getResource( "/aircraft.zip" );
        assert dbUrl != null;
        String f = Path.of( dbUrl.toURI() )
                       .toString();
        var db = new AircraftDatabase( f );

        AircraftStateManager asm = new AircraftStateManager( db );
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController( mp, asm.states(), sap );
        var root = new StackPane( bmc.pane(), ac.pane() );
        primaryStage.setScene( new Scene( root ) );
        primaryStage.show();

        var mi = readAllMessages( "messages_20230318_0915.bin" ).iterator();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for ( int i = 0 ; i < 10 ; i += 1 ) {
                        Message m = MessageParser.parse( mi.next() );
                        if ( m != null ) {
                            asm.updateWithMessage( m );
                        }
                    }
                }
                catch ( IOException e ) {
                    throw new UncheckedIOException( e );
                }
            }
        }.start();
    }


    static List<RawMessage> readAllMessages(String fileName) throws
                                                             IOException {

        byte[] bytes = new byte[RawMessage.LENGTH];
        AircraftDatabase aircraftDatabase = new AircraftDatabase( fileName );
        AircraftStateManager stateManager = new AircraftStateManager( aircraftDatabase );

        List<RawMessage> list = new ArrayList<>();

        try ( DataInputStream s = new DataInputStream( new BufferedInputStream( new FileInputStream(
                "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - "
                + "BA2\\PROJET\\Javions\\resources\\messages_20230318_0915.bin" ) ) ) ) {

            while ( true ) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes( bytes, 0, bytes.length );
                assert bytesRead == RawMessage.LENGTH;
                assert bytes != null;

                ByteString message = new ByteString( bytes );
                assert message != null;

                RawMessage rawMessage = RawMessage.of( timeStampNs, bytes );
                assert rawMessage != null;

                list.add( rawMessage );

                Message theMessage = MessageParser.parse( rawMessage );
                assert theMessage != null;

                stateManager.updateWithMessage( theMessage );
                stateManager.purge();
            }
        }
    }
}