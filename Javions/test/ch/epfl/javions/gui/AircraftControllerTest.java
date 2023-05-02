package ch.epfl.javions.gui;

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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public final class TestAircraftController extends Application {
    public static void main(String[] args) {
        launch( args );
    }


    @Override
    public void start(Stage primaryStage) throws
                                          Exception {
        // … à compléter (voir TestBaseMapController)
        BaseMapController bmc = …;

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
                                                             IOException { /* … à faire */ }
}