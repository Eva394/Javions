package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;

import java.util.Objects;

/**
 * Manages the view of the aircrafts
 *
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
        this.mapParameters = Objects.requireNonNull(mapParameters);
        this.aircraftStates = Objects.requireNonNull(aircraftStates);
        this.selectedAircraftState = selectedAircraftState;

        this.pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getStylesheets()
                .add(AIRCRAFT_CSS);

        aircraftStates.addListener((SetChangeListener<? super ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                createAircraftGroup(change.getElementAdded());
            } else {
                pane.getChildren()
                        .removeIf(group -> group.getId()
                                .equals(change.getElementRemoved()
                                        .getIcaoAddress()
                                        .string()));
            }
        });
    }


    public Pane pane() {
        return pane;
    }


    private void createAircraftGroup(ObservableAircraftState addedAircraft) {
        Group aircrafGroup = new Group();
        aircrafGroup.setId(addedAircraft.getIcaoAddress()
                .string());
        aircrafGroup.viewOrderProperty()
                .bind(addedAircraft.altitudeProperty()
                        .negate());
        pane.getChildren()
                .add(aircrafGroup);

        createIconAndLabelGroup(aircrafGroup, addedAircraft);
//        if ( addedAircraft.getIcaoAddress()
//                          .string()
//                          .equals( selectedAircraftState.get()
//                                                        .getIcaoAddress()
//                                                        .string() ) ) {
        createTrajectoryGroup(aircrafGroup, addedAircraft);
//        }
    }


    private void createIconAndLabelGroup(Group aircrafGroup, ObservableAircraftState addedAircraft) {
        Group iconAndLabelGroup = new Group();
        aircrafGroup.getChildren()
                .add(iconAndLabelGroup);

        bindPositionToLayout(addedAircraft, iconAndLabelGroup);

        createIcon(iconAndLabelGroup, addedAircraft);
        createLabelGroup(iconAndLabelGroup, addedAircraft);
    }


    private void createLabelGroup(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {
        Group labelGroup = new Group();
        iconAndLabelGroup.getChildren()
                .add(labelGroup);

        createRectangle(iconAndLabelGroup, addedAircraft);
        createText(iconAndLabelGroup, addedAircraft);
    }


    private void createText(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {
    }


    private void createRectangle(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {

    }


    private void createIcon(Group iconAndLabelGroup, ObservableAircraftState addedAircraft) {
        SVGPath iconPath = new SVGPath();
        AircraftData data = addedAircraft.getAircraftData();
        boolean dataIsNull = data == null;
        AircraftTypeDesignator typeDesignator = dataIsNull
                ? new AircraftTypeDesignator("")
                : data.typeDesignator();
        AircraftDescription description = dataIsNull
                ? new AircraftDescription("")
                : data.description();
        WakeTurbulenceCategory wakeTurbulenceCategory = dataIsNull
                ? WakeTurbulenceCategory.of("")
                : data.wakeTurbulenceCategory();

        AircraftIcon icon = AircraftIcon.iconFor(typeDesignator,
                description,
                addedAircraft.categoryProperty()
                        .get(),
                wakeTurbulenceCategory);

        iconPath.contentProperty()
                .bind(addedAircraft.categoryProperty()
                        .map(aircraft -> icon.svgPath()));
//                        Bindings.createObjectBinding( icon::svgPath, addedAircraft.categoryProperty() ) );

        if (icon.canRotate()) {
            iconPath.rotateProperty()
                    .bind(addedAircraft.trackOrHeadingProperty()
                            .map(trackOrHeading -> (Units.convertTo(addedAircraft.trackOrHeadingProperty()
                                            .get(),
                                    Units.Angle.DEGREE))));
        }

        iconPath.fillProperty()
                .bind(addedAircraft.altitudeProperty()
                        .map(altitude -> ColorRamp.PLASMA.at(Math.pow((Double) altitude / 12000,
                                Math.pow(3, -1)))));

        iconPath.getStyleClass()
                .add("aircraft");
        iconAndLabelGroup.getChildren()
                .add(iconPath);
    }


    private void createTrajectoryGroup(Group aircrafGroup, ObservableAircraftState addedAircraft) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass()
                .add("trajectory");
        aircrafGroup.getChildren()
                .add(trajectoryGroup);
        trajectoryGroup.visibleProperty()
                .set(true);

        bindPositionToLayout(addedAircraft, trajectoryGroup);

        ObservableList<ObservableAircraftState.AirbonePos> trajectory = addedAircraft.getUnmodifiableTrajectory();
        trajectory.addListener((ListChangeListener<? super ObservableAircraftState.AirbonePos>) change -> createLines(
                trajectoryGroup,
                trajectory));
        mapParameters.zoomProperty()
                .addListener(change -> {
                    createLines(trajectoryGroup, trajectory);
                    System.out.printf("%-5s %-45s \n ", "in", trajectoryGroup.toString());
                });
        System.out.printf("%-5s %-45s \n ", "out", trajectoryGroup.toString());
    }


    private void createLines(Group trajectoryGroup, ObservableList<ObservableAircraftState.AirbonePos> trajectory) {

        for (int i = 0; i < trajectory.size() - 1; i++) {
            ObservableAircraftState.AirbonePos currentAirbonePos = trajectory.get(i);
            ObservableAircraftState.AirbonePos nextAirbonePos = trajectory.get(i + 1);

            double currentAltitude = currentAirbonePos.altitude();
            double nextAltitude = nextAirbonePos.altitude();

            GeoPos currentPoint = currentAirbonePos.position();
            double currentLongitude = currentPoint.longitude();
            double currentLatitude = currentPoint.latitude();

            GeoPos nextPoint = nextAirbonePos.position();
            double nextLongitude = nextPoint.longitude();
            double nextLatitude = nextPoint.latitude();

            Line line = new Line(currentLongitude, currentLatitude, nextLongitude, nextLatitude);
            trajectoryGroup.getChildren()
                    .add(line);
            line.visibleProperty()
                    .set(true);

//            if ( Double.compare( currentAltitude, nextAltitude ) == 0 ) {
//                line.fillProperty()
//                    .set( ColorRamp.PLASMA.at( Math.pow( currentAltitude / 12000, Math.pow( 3, -1 ) ) ) );
//            }
//            else {
//                Stop color1 = new Stop( 0,
//                                        ColorRamp.PLASMA.at( Math.pow( currentAltitude / 12000, Math.pow( 3, -1 ) )
//                                        ) );
//                Stop color2 = new Stop( 1, ColorRamp.PLASMA.at( Math.pow( nextAltitude / 12000, Math.pow( 3, -1 ) )
//                ) );
//                LinearGradient gradient = new LinearGradient( currentLongitude,
//                                                              currentLatitude,
//                                                              nextLongitude,
//                                                              nextLatitude,
//                                                              true,
//                                                              CycleMethod.NO_CYCLE,
//                                                              color1,
//                                                              color2 );
//                line.fillProperty()
//                    .set( gradient );
//            }
        }
    }


    private void bindPositionToLayout(ObservableAircraftState addedAircraft, Group group) {
        group.layoutXProperty()
                .bind(Bindings.createDoubleBinding(() -> {
                    double longitude = addedAircraft.positionProperty()
                            .get()
                            .longitude();
                    double xPos = WebMercator.x(mapParameters.zoomProperty()
                            .get(), longitude);
                    return xPos - mapParameters.minXProperty()
                            .get();
                }, addedAircraft.positionProperty(), mapParameters.minXProperty(), mapParameters.zoomProperty()));
        group.layoutYProperty()
                .bind(Bindings.createDoubleBinding(() -> {
                    double latitude = addedAircraft.positionProperty()
                            .get()
                            .latitude();
                    double yPos = WebMercator.y(mapParameters.zoomProperty()
                            .get(), latitude);
                    return yPos - mapParameters.minYProperty()
                            .get();
                }, addedAircraft.positionProperty(), mapParameters.minYProperty(), mapParameters.zoomProperty()));
    }
}
