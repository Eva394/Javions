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

public final class AircraftTableControllerTest extends Application {


    @Override
    public void start(Stage primaryStage) throws
                                          Exception {
        // … à compléter (voir TestBaseMapController)
        Path tileCache = Path.of( "tile-cache" );
        TileManager tileManager = new TileManager( tileCache, "tile.openstreetmap.org" );
        MapParameters mp = new MapParameters( 17, 17_389_327, 11_867_430 );
//        BaseMapController bmc = new BaseMapController( tileManager, mp );

        // Création de la base de données
        URL dbUrl = getClass().getResource( "/aircraft.zip" );
        assert dbUrl != null;
        String f = Path.of( dbUrl.toURI() )
                       .toString();
        var db = new AircraftDatabase( f );

        AircraftStateManager asm = new AircraftStateManager( db );

        AircraftDatabase aircraftDatabase = new AircraftDatabase( "C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\aircraft" + ".zip" );
        /*AircraftDatabase aircraftDatabase = new AircraftDatabase(
                "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\aircraft" + ".zip" );

         */
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
//        AircraftController ac = new AircraftController( mp, asm.states(), sap );
        AircraftTableController atc = new AircraftTableController( asm.states(), sap );
        var root = new StackPane( atc.pane() );
        primaryStage.setScene( new Scene( root ) );
        primaryStage.show();

        var mi = readAllMessages( "C:\\Users\\nagyu\\IdeaProjects\\Javions\\Javions\\resources\\messages_20230318_0915.bin" ).iterator();

        //var mi = readAllMessages( "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - "
                               //   + "BA2\\PROJET\\Javions\\resources\\messages_20230318_0915.bin" ).iterator();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for ( int i = 0 ; i < 10 ; i += 1 ) {
                        if ( mi.hasNext() ) {
                            Message m = MessageParser.parse( mi.next() );
                            if ( m != null ) {
                                asm.updateWithMessage( m );
                            }
                        }
                    }
                    asm.purge();
                }
                catch ( IOException e ) {
                    throw new UncheckedIOException( e );
                }
            }
        }.start();
    }


    public static void main(String[] args) {
        launch( args );
    }


    static List<RawMessage> readAllMessages(String fileName) throws
                                                             IOException {
        List<RawMessage> list = new ArrayList<>();

        int index = 0;
        byte[] bytes = new byte[RawMessage.LENGTH];
        try ( DataInputStream s = new DataInputStream( new BufferedInputStream( new FileInputStream( fileName ) ) ) ) {
            while ( index < 1e5 + 1e4 + 1e3 + 1e2 + 1e1 + 1e0 ) {
                index++;
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes( bytes, 0, bytes.length );
                assert bytesRead == RawMessage.LENGTH;

                ByteString message = new ByteString( bytes );
                assert message != null;

                list.add( RawMessage.of( timeStampNs, bytes ) );
            }
        }

        return list;
    }
}