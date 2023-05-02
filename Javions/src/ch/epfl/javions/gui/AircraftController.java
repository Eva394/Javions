package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.Objects;

/**
 * Manages the view of the aircrafts
 * @author Eva Mangano 345375
 */
public final class AircraftController {

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
        } );
        aircraftStates.removeListener( (SetChangeListener<? super ObservableAircraftState>)change -> {
            if ( change.wasRemoved() ) {
                pane.getChildren()
                    .remove();
            }
        } );
    }


    public Pane pane() {
        return pane;
    }


    private void createAircraftGroup(ObservableAircraftState addedAircraft) {
        Group aircrafsGroup = new Group();
        aircrafsGroup.setId( addedAircraft.getIcaoAddress()
                                          .string() );
        aircrafsGroup.viewOrderProperty()
                     .bind( addedAircraft.altitudeProperty()
                                         .negate() );
        pane.getChildren()
            .add( aircrafsGroup );

        createIconAndLabelGroup( aircrafsGroup, addedAircraft );
        createTrajectoryGroup( aircrafsGroup, addedAircraft );
    }


    private void createIconAndLabelGroup(Group aircrafsGroup, ObservableAircraftState addedAircraft) {
        Group iconAndLabelGroup = new Group();
        aircrafsGroup.getChildren()
                     .add( iconAndLabelGroup );
        iconAndLabelGroup.layoutXProperty()
                         .bind( Bindings.createDoubleBinding( () -> {
                                                                  double longitude = addedAircraft.positionProperty()
                                                                                                  .get()
                                                                                                  .longitude();
                                                                  double xPos =
                                                                          WebMercator.x( mapParameters.getZoom(),
                                                                                         longitude );
                                                                  return xPos - mapParameters.minXProperty()
                                                                                             .get();
                                                              },
                                                              addedAircraft.positionProperty(),
                                                              mapParameters.minXProperty(),
                                                              mapParameters.zoomProperty() ) );
        iconAndLabelGroup.layoutYProperty()
                         .bind( Bindings.createDoubleBinding( () -> {
                                                                  double latitude = addedAircraft.positionProperty()
                                                                                                 .get()
                                                                                                 .longitude();
                                                                  double yPos =
                                                                          WebMercator.y( mapParameters.getZoom(),
                                                                                         latitude );
                                                                  return yPos - mapParameters.minYProperty()
                                                                                             .get();
                                                              },
                                                              addedAircraft.positionProperty(),
                                                              mapParameters.minYProperty(),
                                                              mapParameters.zoomProperty() ) );

        createIcon( iconAndLabelGroup, addedAircraft );
    }


    private void createIcon(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {
        AircraftIcon icon = AircraftIcon.iconFor( addedAircraft.getAircraftData()
                                                               .typeDesignator(),
                                                  addedAircraft.getAircraftData()
                                                               .description(),
                                                  addedAircraft.getCategory(),
                                                  addedAircraft.getAircraftData()
                                                               .wakeTurbulenceCategory() );
        pane.getStyleClass()
            .add( "aircraft" );
        SVGPath iconPath = new SVGPath();
        iconPath.contentProperty()
                .set( icon.svgPath() );
        iconAndLabelGroup.getChildren()
                         .add( iconPath );
    }


    private void createTrajectoryGroup(Group aircrafsGroup, ObservableAircraftState addedAircraft) {
    }
}
