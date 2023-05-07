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

/**
 * Manages the view of the aircrafts
 * @author Eva Mangano 345375
 */
public final class AircraftController {


    public static final double ALTITUDE_POWER = 1. / 3.;
    public static final double ALTITUDE_FACTOR = 1. / 12000.;
    private static final int MIN_ZOOM_LEVEL_FOR_VISIBLE_LABELS = 11;
    private static final int LABEL_MARGIN = 4;
    private static final String AIRCRAFT_CSS = "aircraft.css";
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final Pane pane;


    public AircraftController(MapParameters mapParameters, ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.mapParameters = Objects.requireNonNull( mapParameters );
        this.aircraftStates = Objects.requireNonNull( aircraftStates );
        this.selectedAircraftState = selectedAircraftState;

        this.pane = new Pane();
        pane.setPickOnBounds( false );
        pane.getStylesheets()
            .add( AIRCRAFT_CSS );

        aircraftStates.addListener( (SetChangeListener<? super ObservableAircraftState>)change -> {
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


    public Pane pane() {
        return pane;
    }


    private void createAircraftGroup(ObservableAircraftState addedAircraft) {
        Group aircrafGroup = new Group();
        aircrafGroup.setId( addedAircraft.getIcaoAddress()
                                         .string() );
        aircrafGroup.viewOrderProperty()
                    .bind( addedAircraft.altitudeProperty()
                                        .negate() );
        aircrafGroup.setOnMouseClicked( event -> {
            selectedAircraftState.set( addedAircraft );
        } );
        pane.getChildren()
            .add( aircrafGroup );

        createTrajectoryGroup( aircrafGroup, addedAircraft );
        createIconAndLabelGroup( aircrafGroup, addedAircraft );
    }


    private void createIconAndLabelGroup(Group aircrafGroup, ObservableAircraftState addedAircraft) {

        Group iconAndLabelGroup = new Group();

        aircrafGroup.getChildren()
                    .add( iconAndLabelGroup );

        bindPositionToLayout( addedAircraft, iconAndLabelGroup );

        createLabelGroup( iconAndLabelGroup, addedAircraft );
        createIcon( iconAndLabelGroup, addedAircraft );
    }


    private void createLabelGroup(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {
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
                                                        selectedAircraftState,
                                                        mapParameters.zoomProperty() ) );

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

        //TODO this doenst update the values
        //TODO "?" doesnt work
        labelText.textProperty()
                 .bind( Bindings.format( "%s\n%f m\u2002%f km/h",
                                         aircraftID,
                                         !Double.isNaN( addedAircraft.velocityProperty()
                                                                     .get() )
                                         ? addedAircraft.velocityProperty()
                                                        .get()
                                         : "?",
                                         !Double.isNaN( addedAircraft.altitudeProperty()
                                                                     .get() )
                                         ? addedAircraft.altitudeProperty()
                                                        .get()
                                         : "?" ) );
//                                         addedAircraft.altitudeProperty(),
//                                         addedAircraft.velocityProperty() ) );
    }


    private void createIcon(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {
        SVGPath iconPath = new SVGPath();
        AircraftData data = addedAircraft.getAircraftData();
        boolean dataIsNull = data == null;
        AircraftTypeDesignator typeDesignator = dataIsNull
                                                ? new AircraftTypeDesignator( "" )
                                                : data.typeDesignator();
        AircraftDescription description = dataIsNull
                                          ? new AircraftDescription( "" )
                                          : data.description();
        WakeTurbulenceCategory wakeTurbulenceCategory = dataIsNull
                                                        ? WakeTurbulenceCategory.of( "" )
                                                        : data.wakeTurbulenceCategory();

        AircraftIcon icon = AircraftIcon.iconFor( typeDesignator,
                                                  description,
                                                  addedAircraft.categoryProperty()
                                                               .get(),
                                                  wakeTurbulenceCategory );

        iconPath.contentProperty()
                .bind( addedAircraft.categoryProperty()
                                    .map( aircraft -> icon.svgPath() ) );

        iconPath.rotateProperty()
                .bind( addedAircraft.trackOrHeadingProperty()
                                    .map( trackOrHeading -> icon.canRotate()
                                                            ? ( Units.convertTo( addedAircraft.trackOrHeadingProperty()
                                                                                              .get(),
                                                                                 Units.Angle.DEGREE ) )
                                                            : 0 ) );

        iconPath.fillProperty()
                .bind( addedAircraft.altitudeProperty()
                                    .map( altitude -> ColorRamp.PLASMA.at( Math.pow( (Double)altitude * ALTITUDE_FACTOR,
                                                                                     ALTITUDE_POWER ) ) ) );

        iconPath.getStyleClass()
                .add( "aircraft" );
        iconAndLabelGroup.getChildren()
                         .add( iconPath );
    }


    private void createTrajectoryGroup(Group aircrafGroup, ObservableAircraftState addedAircraft) {

        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass()
                       .add( "trajectory" );
        trajectoryGroup.visibleProperty()
                       .bind( Bindings.createBooleanBinding( () -> addedAircraft.equals( selectedAircraftState.get() ),
                                                             selectedAircraftState ) );

        aircrafGroup.getChildren()
                    .add( trajectoryGroup );

        bindPositionToLayout( addedAircraft, trajectoryGroup );

        ObservableList<ObservableAircraftState.AirbonePos> trajectory = addedAircraft.getUnmodifiableTrajectory();
        trajectory.addListener( (ListChangeListener<? super ObservableAircraftState.AirbonePos>)change -> {
            if ( trajectoryGroup.visibleProperty()
                                .get() ) {
                createLines( trajectoryGroup, trajectory );
            }
        } );

        mapParameters.zoomProperty()
                     .addListener( change -> {
                         if ( trajectoryGroup.visibleProperty()
                                             .get() ) {
                             createLines( trajectoryGroup, trajectory );
                         }
                     } );
    }


    private void createLines(Group trajectoryGroup, ObservableList<ObservableAircraftState.AirbonePos> trajectory) {
        trajectoryGroup.getChildren()
                       .clear();

        double lastX = WebMercator.x( mapParameters.getZoom(),
                                      trajectory.get( trajectory.size() - 1 )
                                                .position()
                                                .longitude() );
        double lastY = WebMercator.y( mapParameters.getZoom(),
                                      trajectory.get( trajectory.size() - 1 )
                                                .position()
                                                .latitude() );

        ObservableAircraftState.AirbonePos currentAirbornePos = trajectory.get( 0 );
        ObservableAircraftState.AirbonePos nextAirbornePos;

        for ( int i = 0 ; i < trajectory.size() - 1 ; i++ ) {
            nextAirbornePos = trajectory.get( i + 1 );

            double currentAltitude = currentAirbornePos.altitude();
            double nextAltitude = nextAirbornePos.altitude();

            double startX = WebMercator.x( mapParameters.getZoom(),
                                           currentAirbornePos.position()
                                                             .longitude() ) - lastX;
            double startY = WebMercator.y( mapParameters.getZoom(),
                                           currentAirbornePos.position()
                                                             .latitude() ) - lastY;
            double endX = WebMercator.x( mapParameters.getZoom(),
                                         nextAirbornePos.position()
                                                        .longitude() ) - lastX;
            double endY = WebMercator.y( mapParameters.getZoom(),
                                         nextAirbornePos.position()
                                                        .latitude() ) - lastY;
            Line line = new Line( startX, startY, endX, endY );
            trajectoryGroup.getChildren()
                           .add( line );

            applyColour( currentAltitude, nextAltitude, startX, startY, endX, endY, line );

            currentAirbornePos = nextAirbornePos;
        }
    }


    private void bindPositionToLayout(ObservableAircraftState addedAircraft, Group group) {
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


    private static void applyColour(double currentAltitude, double nextAltitude, double startX, double startY,
                                    double endX, double endY, Line line) {
        if ( Double.compare( currentAltitude, nextAltitude ) == 0 ) {
            line.setStroke( ColorRamp.PLASMA.at( Math.pow( currentAltitude * ALTITUDE_FACTOR, ALTITUDE_POWER ) ) );
        }
        else {
            Stop color1 = new Stop( 0,
                                    ColorRamp.PLASMA.at( Math.pow( currentAltitude * ALTITUDE_FACTOR,
                                                                   ALTITUDE_POWER ) ) );
            Stop color2 = new Stop( 1,
                                    ColorRamp.PLASMA.at( Math.pow( nextAltitude * ALTITUDE_FACTOR, ALTITUDE_POWER ) ) );
            LinearGradient gradient = new LinearGradient( startX,
                                                          startY,
                                                          endX,
                                                          endY,
                                                          true,
                                                          CycleMethod.NO_CYCLE,
                                                          color1,
                                                          color2 );
            line.setStroke( gradient );
        }
    }
}
