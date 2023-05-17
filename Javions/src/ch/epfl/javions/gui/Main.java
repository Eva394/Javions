package ch.epfl.javions.gui;


import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Main extends Application {

    private static final double ONE_SECOND_IN_NANOSECONDS = 1e9;
    private static final int INITIAL_ZOOM_LEVEL = 8;
    private static final int INITIAL_X = 33_530;
    private static final int INITIAL_Y = 23_070;
    private static final String SCENE_TITLE = "Javions";
    private static final int MIN_SCENE_WIDTH = 800;
    private static final int MIN_SCENE_HEIGHT = 600;
    private static final String SERVER_NAME = "tile.openstreetmap.org";


    public static void main(String[] args) {
        Application.launch( args );
    }


    private static List<RawMessage> readAllMessages(String fileName) throws IOException {
        List<RawMessage> list = new ArrayList<>();

        int index = 0;
        byte[] bytes = new byte[RawMessage.LENGTH];
        try ( DataInputStream s = new DataInputStream( new BufferedInputStream( new FileInputStream( fileName ) ) ) ) {
            AdsbDemodulator adsbDemodulator = new AdsbDemodulator( s );
            RawMessage message;

            while ( ( message = adsbDemodulator.nextMessage() ) != null ) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes( bytes, 0, bytes.length );
                assert bytesRead == RawMessage.LENGTH;

                ByteString messageByteString = new ByteString( bytes );
                assert messageByteString != null;

                list.add( RawMessage.of( timeStampNs, bytes ) );
            }
        }
        return list;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Creation of the map's view
        Path tileCache = Path.of( "tile-cache" );
        TileManager tileManager = new TileManager( tileCache, SERVER_NAME );
        MapParameters mapParameters = new MapParameters( INITIAL_ZOOM_LEVEL, INITIAL_X, INITIAL_Y );
        BaseMapController map = new BaseMapController( tileManager, mapParameters );

        // Creation of the aircraft's view
        URL databaseUrl = getClass().getResource( "/aircraft.zip" );
        assert databaseUrl != null;
        String pathString = Path.of( databaseUrl.toURI() )
                                .toString();
        AircraftDatabase aircraftDatabase = new AircraftDatabase( pathString );
        AircraftStateManager aircraftStateManager = new AircraftStateManager( aircraftDatabase );
        ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty = new SimpleObjectProperty<>();
        AircraftController aircraftController = new AircraftController( mapParameters, aircraftStateManager.states(),
                                                                        selectedAircraftStateProperty );

        // Creation of the table view
        AircraftTableController table = new AircraftTableController( aircraftStateManager.states(),
                                                                     selectedAircraftStateProperty );

        // Creation of the status line view
        StatusLineController statusLine = new StatusLineController();
        statusLine.aircraftCountProperty()
                  .bind( Bindings.size( aircraftStateManager.states() ) );

        // Creation of the leaf panes
        StackPane mapAndAircraftPane = new StackPane( map.pane(), aircraftController.pane() );
        BorderPane tableAndStatusLinePane = new BorderPane();
        tableAndStatusLinePane.setCenter( table.pane() );
        tableAndStatusLinePane.setTop( statusLine.pane() );

        // Creation of the principal pane
        SplitPane pane = new SplitPane( mapAndAircraftPane, tableAndStatusLinePane );
        pane.setOrientation( Orientation.VERTICAL );

        // Creation of the scenes
        Scene scene = new Scene( pane );
        primaryStage.setScene( scene );
        primaryStage.setTitle( SCENE_TITLE );
        primaryStage.setMinWidth( MIN_SCENE_WIDTH );
        primaryStage.setMinHeight( MIN_SCENE_HEIGHT );
        primaryStage.show();

        ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
        Iterator<Message> queueIterator = queue.iterator();

        Thread obtainingMessagesThread = new Thread( () -> {
            System.out.println( "in lambda" );
            while ( true ) {
                fillQueue( queue );
            }
        } );
        obtainingMessagesThread.setDaemon( true );
        obtainingMessagesThread.start();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                long lastPurgeTimeStampNs = 0;
                while ( !queue.isEmpty() ) {
                    Message message = queueIterator.next();
                    aircraftStateManager.updateWithMessage( message );

                    if ( message.timeStampNs() - lastPurgeTimeStampNs >= ONE_SECOND_IN_NANOSECONDS ) {
                        aircraftStateManager.purge();
                        lastPurgeTimeStampNs = now;
                    }
                }
            }
        }.start();
    }


    private void fillQueue(ConcurrentLinkedQueue<Message> queue) {
        String fileName;
        Message message;

        // the messages come from a file
        if ( ( fileName = getParameters().getRaw()
                                         .get( 0 ) ) != null ) {
            while ( true ) {
                List<RawMessage> rawMessages = readAllMessages( fileName );
                for ( RawMessage rawMessage : rawMessages ) {
                    if ( ( message = MessageParser.parse( rawMessage ) ) != null ) {
                        queue.add( message );
                    }
                }
            }
        }
        // the messages come from the radio
        else {
            AdsbDemodulator adsbDemodulator = new AdsbDemodulator( System.in );
            RawMessage rawMessage;
            while ( ( rawMessage = adsbDemodulator.nextMessage() ) != null ) {
                if ( ( message = MessageParser.parse( rawMessage ) ) != null ) {
                    queue.add( message );
                }
            }
        }
    }
}