package ch.epfl.javions.gui;


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

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Main class for Javions
 *
 * @author Eva Mangano 345375
 */
public final class Main extends Application {
	
	private static final int MIN_WIDTH = 800;
	private static final int MIN_HEIGHT = 600;
	private static final double ONE_SECOND_IN_NANOSECONDS = 1e9;
	private static final int INITIAL_ZOOM_LEVEL = 8;
	private static final int INITIAL_X = 33_530;
	private static final int INITIAL_Y = 23_070;
	private static final String SCENE_TITLE = "Javions";
	private static final int MIN_SCENE_WIDTH = MIN_WIDTH;
	private static final int MIN_SCENE_HEIGHT = MIN_HEIGHT;
	private static final String SERVER_NAME = "tile.openstreetmap.org";
	private static final String AIRCRAFT_RESOURCES = "/aircraft.zip";
	private static final String CACHE_DIRECTORY = "Javions/tile-cache";
	private static final double NS_TO_MS_FACTOR = 1e-6;
	
	@Override
	public void start( Stage primaryStage ) throws Exception {
		
		// Creation of the map's view
		Path tileCache = Path.of( CACHE_DIRECTORY );
		TileManager tileManager = new TileManager( tileCache, SERVER_NAME );
		MapParameters mapParameters = new MapParameters( INITIAL_ZOOM_LEVEL, INITIAL_X, INITIAL_Y );
		BaseMapController map = new BaseMapController( tileManager, mapParameters );
		
		// Creation of the aircraft's view
		URL databaseUrl = getClass().getResource( AIRCRAFT_RESOURCES );
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
		table.setOnDoubleClick( observableAircraftState -> {
			map.centerOn( observableAircraftState.getPosition() );
		} );
		
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
		
		Thread obtainingMessagesThread = new Thread( () -> {
			try {
				fillQueue( queue );
			}
			catch ( IOException e ) {
				throw new UncheckedIOException( e );
			}
		} );
		obtainingMessagesThread.setDaemon( true );
		obtainingMessagesThread.start();
		
		( new AnimationTimer() {
			private long lastPurgeTimeStampNs = 0L;
			
			@Override
			public void handle( long now ) {
				try {
					while ( !queue.isEmpty() ) {
						Message message = queue.remove();
						aircraftStateManager.updateWithMessage( message );
						statusLine.messageCountProperty()
						          .set( statusLine.messageCountProperty()
						                          .get() + 1L );
					}
					if ( now - lastPurgeTimeStampNs >= ONE_SECOND_IN_NANOSECONDS ) {
						aircraftStateManager.purge();
						lastPurgeTimeStampNs = now;
					}
					return;
				}
				catch ( IOException e ) {
					throw new UncheckedIOException( e );
				}
			}
		} ).start();
	}
	
	/**
	 * Launches the Javions application
	 *
	 * @param args name of the file where the messages are read or null if the samples come from the radio
	 */
	public static void main( String[] args ) {
		Application.launch( args );
	}
	
	private void fillQueue( ConcurrentLinkedQueue<Message> queue ) throws IOException {
		long lastMessageTimeStampNs = 0L;
		String fileName;
		List<String> parametersString;
		Message message;
		
		// the messages come from a file
		if ( !( parametersString = getParameters().getRaw() ).isEmpty() && ( fileName = parametersString.get( 0 ) ) != null ) {
			
			byte[] bytes = new byte[ RawMessage.LENGTH ];
			
			try ( DataInputStream s = new DataInputStream( new BufferedInputStream( new FileInputStream( fileName ) ) ) ) {
				long timeStampNs;
				int bytesRead;
				RawMessage rawMessage;
				
				while ( true ) {
					timeStampNs = s.readLong();
					bytesRead = s.readNBytes( bytes, 0, bytes.length );
					assert bytesRead == RawMessage.LENGTH;
					
					if ( ( rawMessage = RawMessage.of( timeStampNs, bytes ) ) != null ) {
						if ( ( message = MessageParser.parse( rawMessage ) ) != null ) {
							Thread.sleep( ( long ) ( ( message.timeStampNs() - lastMessageTimeStampNs ) * NS_TO_MS_FACTOR ) );
							queue.add( message );
							lastMessageTimeStampNs = message.timeStampNs();
						}
					}
					else {
						break;
					}
				}
			}
			catch ( IOException ignored ) {
			}
			catch ( InterruptedException e ) {
				throw new Error( e );
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
