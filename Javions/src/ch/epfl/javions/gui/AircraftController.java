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

//        aircraftStates.forEach( observableAircraftState -> aircraftStates.addListener( (SetChangeListener<?
//                super ObservableAircraftState>)change -> createAircraftGroup(
//                change.getElementAdded() ) ) );
        aircraftStates.addListener( (SetChangeListener<? super ObservableAircraftState>)change -> {
            if ( change.wasAdded() ) {
                createAircraftGroup( change.getElementAdded() );
            }
        } );
        aircraftStates.removeListener( (SetChangeListener<? super ObservableAircraftState>)change -> {
            if ( change.wasRemoved() ) {
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
        pane.getChildren()
            .add( aircrafGroup );

        createIconAndLabelGroup( aircrafGroup, addedAircraft );
        createTrajectoryGroup( aircrafGroup, addedAircraft );
    }


    private void createIconAndLabelGroup(Group aircrafGroup, ObservableAircraftState addedAircraft) {
        Group iconAndLabelGroup = new Group();
        aircrafGroup.getChildren()
                    .add( iconAndLabelGroup );

        iconAndLabelGroup.layoutXProperty()
                         .bind( Bindings.createDoubleBinding( () -> {
                                                                  double longitude = addedAircraft.positionProperty()
                                                                                                  .get()
                                                                                                  .longitude();
                                                                  double xPos =
                                                                          WebMercator.x( mapParameters.zoomProperty()
                                                                                                            .get(),
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
                                                                          WebMercator.y( mapParameters.zoomProperty()
                                                                                                            .get(),
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
        SVGPath iconPath = new SVGPath();
        AircraftIcon icon = AircraftIcon.iconFor( addedAircraft.getAircraftData()
                                                               .typeDesignator(),
                                                  addedAircraft.getAircraftData()
                                                               .description(),
                                                  addedAircraft.categoryProperty()
                                                               .get(),
                                                  addedAircraft.getAircraftData()
                                                               .wakeTurbulenceCategory() );

        iconPath.contentProperty()
                .bind( Bindings.createObjectBinding( icon::svgPath, addedAircraft.categoryProperty() ) );

        if ( icon.canRotate() ) {
            iconPath.rotateProperty()
                    .bind( Bindings.createObjectBinding( () -> addedAircraft.trackOrHeadingProperty()
                                                                            .get(),
                                                         addedAircraft.trackOrHeadingProperty() ) );
        }

        iconPath.fillProperty()
                .bind( addedAircraft.altitudeProperty()
                                    .map( altitude -> ColorRamp.PLASMA.at( (Double)altitude ) ) );
//                .bind( Bindings.createObjectBinding( () -> ColorRamp.PLASMA.at( addedAircraft.altitudeProperty()
//                                                                                             .get() ),
//                                                     addedAircraft.altitudeProperty() ) );

        pane.getStyleClass()
            .add( icon.svgPath() );
        iconAndLabelGroup.getChildren()
                         .add( iconPath );
    }


    private void createTrajectoryGroup(Group aircrafGroup, ObservableAircraftState addedAircraft) {
    }
}
