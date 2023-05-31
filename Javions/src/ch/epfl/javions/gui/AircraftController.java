package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Manages the view of the aircrafts
 *
 * @author Eva Mangano 345375
 */
public final class AircraftController {
	
	private static final String STRING_FOR_UNKNOWN_VALUE = "?";
	private static final int MIN_ZOOM_LEVEL_FOR_VISIBLE_LABELS = 11;
	private static final int LABEL_MARGIN = 4;
	private static final String AIRCRAFT_CSS = "aircraft.css";
	private static final int APPROX_HIGHEST_ALTITUDE = 12000;
	private static final double ROOT_POWER = 1.0 / 3;
	private final MapParameters mapParameters;
	private final ObservableSet<ObservableAircraftState> aircraftStates;
	private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
	private final Pane pane;
	
	
	/**
	 * Constructor. Builds an instance of <code>AircraftController</code>
	 *
	 * @param mapParameters         parameters for the map
	 * @param aircraftStates        set of aircraft states (<code>ObservableAircraftState</code>)
	 * @param selectedAircraftState state of the selected aircraft
	 * @throws NullPointerException if <code>mapParameters</code> is null
	 * @throws NullPointerException if <code>aircraftStates</code> is null
	 * @author Eva Mangano 345375
	 * @author Nagyung Kim (339628)
	 */
	public AircraftController( MapParameters mapParameters, ObservableSet<ObservableAircraftState> aircraftStates,
	                           ObjectProperty<ObservableAircraftState> selectedAircraftState ) {
		this.mapParameters = Objects.requireNonNull( mapParameters );
		this.aircraftStates = Objects.requireNonNull( aircraftStates );
		this.selectedAircraftState = selectedAircraftState;
		
		this.pane = new Pane();
		pane.setPickOnBounds( false );
		pane.getStylesheets()
		    .add( AIRCRAFT_CSS );
		
		this.aircraftStates.addListener( ( SetChangeListener<? super ObservableAircraftState> ) change -> {
			if ( change.wasAdded() ) {
				createAircraftGroup( change.getElementAdded() );
			}
			else {
				pane.getChildren()
				    .removeIf( group -> group.getId()
				                             .equals( change.getElementRemoved()
				                                            .getIcaoAddress()
				                                            .string() ) );
			}
		} );
	}
	
	/**
	 * Getter for the pane of the aircrafts' view
	 */
	public Pane pane() {
		return pane;
	}
	
	/**
	 * Maps the altitude to the corresponding value in the interval [0, 1]
	 *
	 * @param altitude altitude of the aircraft
	 * @return the value corresponding to the altitude
	 */
	public static double correspondingColor( double altitude ) {
		return Math.pow( altitude / APPROX_HIGHEST_ALTITUDE, ROOT_POWER );
	}
	
	private void createAircraftGroup( ObservableAircraftState addedAircraft ) {
		Group aircraftGroup = new Group();
		aircraftGroup.setId( addedAircraft.getIcaoAddress()
		                                  .string() );
		aircraftGroup.viewOrderProperty()
		             .bind( addedAircraft.altitudeProperty()
		                                 .negate() );
		aircraftGroup.setOnMouseClicked( event -> selectedAircraftState.set( addedAircraft ) );
		pane.getChildren()
		    .add( aircraftGroup );
		
		createTrajectoryGroup( aircraftGroup, addedAircraft );
		createIconAndLabelGroup( aircraftGroup, addedAircraft );
	}
	
	private void createIconAndLabelGroup( Group aircraftGroup, ObservableAircraftState addedAircraft ) {
		
		Group iconAndLabelGroup = new Group();
		
		aircraftGroup.getChildren()
		             .add( iconAndLabelGroup );
		
		bindPositionToLayout( addedAircraft, iconAndLabelGroup );
		
		AircraftData data = addedAircraft.getAircraftData();
		
		createLabelGroup( iconAndLabelGroup, addedAircraft );
		createIcon( iconAndLabelGroup, addedAircraft, data );
	}
	
	private void createLabelGroup( Group iconAndLabelGroup, ObservableAircraftState addedAircraft ) {
		Group labelGroup = new Group();
		labelGroup.getStyleClass()
		          .add( "label" );
		
		iconAndLabelGroup.getChildren()
		                 .add( labelGroup );
		
		Rectangle labelBase = new Rectangle();
		Text labelText = new Text();
		
		labelGroup.getChildren()
		          .add( labelBase );
		labelGroup.getChildren()
		          .add( labelText );
		
		labelBase.widthProperty()
		         .bind( labelText.layoutBoundsProperty()
		                         .map( text -> text.getWidth() + LABEL_MARGIN ) );
		labelBase.heightProperty()
		         .bind( labelText.layoutBoundsProperty()
		                         .map( text -> text.getHeight() + LABEL_MARGIN ) );
		labelGroup.visibleProperty()
		          .bind( Bindings.createBooleanBinding( () -> ( addedAircraft.equals( selectedAircraftState.get() ) ||
				                                                mapParameters.zoomProperty()
				                                                             .get()
						                                                >= MIN_ZOOM_LEVEL_FOR_VISIBLE_LABELS ),
		                                                selectedAircraftState, mapParameters.zoomProperty() ) );
		
		String aircraftID = addedAircraft.getAircraftData() != null && addedAircraft.getAircraftData()
		                                                                            .registration() != null
		                    ? addedAircraft.getAircraftData()
		                                   .registration()
		                                   .string()
		                    : ( addedAircraft.getCallSign() != null
		                        ? addedAircraft.getCallSign()
		                                       .string()
		                        : addedAircraft.getIcaoAddress()
		                                       .string() );
		
		labelText.textProperty()
		         .bind( Bindings.format( "%s\n%s km/h\u2002%s m", aircraftID, addedAircraft.velocityProperty()
		                                                                                   .map( velocity ->
				                                                                                         !Double.isNaN(
						                                                                                         velocity.doubleValue() )
				                                                                                         ? ( int ) Units.convertTo(
						                                                                                         velocity.intValue(),
						                                                                                         Units.Speed.KILOMETER_PER_HOUR )
				                                                                                         : STRING_FOR_UNKNOWN_VALUE ),
		                                 addedAircraft.altitudeProperty()
		                                              .map( altitude -> !Double.isNaN( altitude.doubleValue() )
		                                                                ? ( int ) Units.convertTo( altitude.doubleValue(),
		                                                                                           Units.Length.METER )
		                                                                : STRING_FOR_UNKNOWN_VALUE ) ) );
	}
	
	private void createIcon( Group iconAndLabelGroup, ObservableAircraftState addedAircraft, AircraftData data ) {
		SVGPath iconPath = new SVGPath();
		
		boolean dataIsNull = data == null;
		AircraftTypeDesignator typeDesignator = dataIsNull
		                                        ? new AircraftTypeDesignator( "" )
		                                        : data.typeDesignator();
		AircraftDescription description = dataIsNull
		                                  ? new AircraftDescription( "" )
		                                  : data.description();
		WakeTurbulenceCategory wakeTurbulenceCategory = dataIsNull
		                                                ? WakeTurbulenceCategory.UNKNOWN
		                                                : data.wakeTurbulenceCategory();
		
		AircraftIcon icon = AircraftIcon.iconFor( typeDesignator, description, addedAircraft.categoryProperty()
		                                                                                    .get(),
		                                          wakeTurbulenceCategory );
		
		iconPath.contentProperty()
		        .bind( addedAircraft.categoryProperty()
		                            .map( aircraft -> icon.svgPath() ) );
		
		iconPath.rotateProperty()
		        .bind( addedAircraft.trackOrHeadingProperty()
		                            .map( trackOrHeading -> icon.canRotate()
		                                                    ? ( Units.convertTo(
				                            addedAircraft.trackOrHeadingProperty()
				                                         .get(), Units.Angle.DEGREE ) )
		                                                    : 0 ) );
		
		iconPath.fillProperty()
		        .bind( addedAircraft.altitudeProperty()
		                            .map( altitude -> ColorRamp.PLASMA.at( correspondingColor( ( double ) altitude ) ) ) );
		
		iconPath.getStyleClass()
		        .add( "aircraft" );
		iconAndLabelGroup.getChildren()
		                 .add( iconPath );
	}
	
	private void createTrajectoryGroup( Group aircraftGroup, ObservableAircraftState addedAircraft ) {
		
		Group trajectoryGroup = new Group();
		trajectoryGroup.getStyleClass()
		               .add( "trajectory" );
		trajectoryGroup.visibleProperty()
		               .bind( Bindings.createBooleanBinding( () -> addedAircraft.equals( selectedAircraftState.get() ),
		                                                     selectedAircraftState ) );
		
		aircraftGroup.getChildren()
		             .add( trajectoryGroup );
		
		bindPositionToLayout( addedAircraft, trajectoryGroup );
		
		ObservableList<ObservableAircraftState.AirbornePos> trajectory = addedAircraft.getUnmodifiableTrajectory();
		trajectory.addListener( ( ListChangeListener<? super ObservableAircraftState.AirbornePos> ) change -> {
			if ( trajectoryGroup.visibleProperty()
			                    .get() ) {
				createTrajectoryLines( trajectoryGroup, trajectory );
			}
		} );
		
		mapParameters.zoomProperty()
		             .addListener( change -> {
			             if ( trajectoryGroup.visibleProperty()
			                                 .get() ) {
				             createTrajectoryLines( trajectoryGroup, trajectory );
			             }
		             } );
	}
	
	private void createTrajectoryLines( Group trajectoryGroup,
	                                    ObservableList<ObservableAircraftState.AirbornePos> trajectory ) {
		trajectoryGroup.getChildren()
		               .clear();
		
		double lastX = WebMercator.x( mapParameters.getZoom(), trajectory.get( trajectory.size() - 1 )
		                                                                 .position()
		                                                                 .longitude() );
		double lastY = WebMercator.y( mapParameters.getZoom(), trajectory.get( trajectory.size() - 1 )
		                                                                 .position()
		                                                                 .latitude() );
		
		IntStream.range( 0, trajectory.size() - 1 )
		         .forEach( i -> {
			         ObservableAircraftState.AirbornePos currentAirbornePos = trajectory.get( i );
			         ObservableAircraftState.AirbornePos nextAirbornePos = trajectory.get( i + 1 );
			         
			         double currentAltitude = currentAirbornePos.altitude();
			         double nextAltitude = nextAirbornePos.altitude();
			         
			         double startX = WebMercator.x( mapParameters.getZoom(), currentAirbornePos.position()
			                                                                                   .longitude() ) - lastX;
			         double startY = WebMercator.y( mapParameters.getZoom(), currentAirbornePos.position()
			                                                                                   .latitude() ) - lastY;
			         double endX = WebMercator.x( mapParameters.getZoom(), nextAirbornePos.position()
			                                                                              .longitude() ) - lastX;
			         double endY = WebMercator.y( mapParameters.getZoom(), nextAirbornePos.position()
			                                                                              .latitude() ) - lastY;
			         
			         Line line = new Line( startX, startY, endX, endY );
			         trajectoryGroup.getChildren()
			                        .add( line );
			         
			         applyColor( currentAltitude, nextAltitude, startX, startY, endX, endY, line );
		         } );
	}
	
	private void bindPositionToLayout( ObservableAircraftState addedAircraft, Group group ) {
		group.layoutXProperty()
		     .bind( Bindings.createDoubleBinding( () -> {
			     double longitude = addedAircraft.positionProperty()
			                                     .get()
			                                     .longitude();
			     double xPos = WebMercator.x( mapParameters.zoomProperty()
			                                               .get(), longitude );
			     return xPos - mapParameters.minXProperty()
			                                .get();
		     }, addedAircraft.positionProperty(), mapParameters.minXProperty(), mapParameters.zoomProperty() ) );
		group.layoutYProperty()
		     .bind( Bindings.createDoubleBinding( () -> {
			     double latitude = addedAircraft.positionProperty()
			                                    .get()
			                                    .latitude();
			     double yPos = WebMercator.y( mapParameters.zoomProperty()
			                                               .get(), latitude );
			     return yPos - mapParameters.minYProperty()
			                                .get();
		     }, addedAircraft.positionProperty(), mapParameters.minYProperty(), mapParameters.zoomProperty() ) );
	}
	
	private static void applyColor( double currentAltitude, double nextAltitude, double startX, double startY,
	                                double endX, double endY, Line line ) {
		if ( Double.compare( currentAltitude, nextAltitude ) == 0 ) {
			line.setStroke( ColorRamp.PLASMA.at( correspondingColor( currentAltitude ) ) );
		}
		else {
			Stop color1 = new Stop( 0, ColorRamp.PLASMA.at( correspondingColor( currentAltitude ) ) );
			Stop color2 = new Stop( 1, ColorRamp.PLASMA.at( correspondingColor( nextAltitude ) ) );
			LinearGradient gradient = new LinearGradient( startX, startY, endX, endY, true, CycleMethod.NO_CYCLE,
			                                              color1, color2 );
			line.setStroke( gradient );
		}
	}
}
