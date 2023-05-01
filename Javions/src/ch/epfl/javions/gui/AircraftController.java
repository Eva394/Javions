package ch.epfl.javions.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public final class AircraftController {

    private final Pane pane;

    public AircraftController(double viewWidth, double viewHeight, ObservableSet<AircraftState> aircraftStates, ObservableValue<AircraftState> selectedAircraftState) {
        pane = new Pane();
        pane.setPrefSize(viewWidth, viewHeight);
        pane.setPickOnBounds(false);

        // create a group for each aircraft
        for (AircraftState aircraftState : aircraftStates) {
            Group aircraftGroup = new Group();

            // create a line representing the aircraft's trajectory
            Line trajectoryLine = new Line();
            trajectoryLine.setStrokeWidth(1.0);
            trajectoryLine.setStrokeLineCap(StrokeLineCap.ROUND);
            trajectoryLine.setStrokeLineJoin(StrokeLineJoin.ROUND);
            trajectoryLine.setOpacity(0.0); // initially hidden
            aircraftGroup.getChildren().add(trajectoryLine);

            // create an icon representing the aircraft
            ImageView aircraftIcon = new ImageView(aircraftState.getIcon());
            aircraftIcon.setPreserveRatio(true);
            aircraftIcon.setSmooth(true);
            aircraftIcon.setCache(true);
            aircraftGroup.getChildren().add(aircraftIcon);

            // create a label displaying the aircraft's call sign
            Label callSignLabel = new Label(aircraftState.getCallSign());
            callSignLabel.getStyleClass().add("call-sign-label");
            callSignLabel.setWrapText(true);
            callSignLabel.setOpacity(0.0); // initially hidden
            aircraftGroup.getChildren().add(callSignLabel);

            // bind the icon's position to the aircraft's position
            aircraftIcon.xProperty().bind(aircraftState.getXProperty().subtract(aircraftIcon.getImage().getWidth() / 2));
            aircraftIcon.yProperty().bind(aircraftState.getYProperty().subtract(aircraftIcon.getImage().getHeight() / 2));

            // bind the label's position to the aircraft's position
            callSignLabel.layoutXProperty().bind(aircraftState.getXProperty().subtract(callSignLabel.widthProperty().divide(2)));
            callSignLabel.layoutYProperty().bind(aircraftState.getYProperty().add(aircraftIcon.getImage().getHeight() / 2).add(5));

            // hide/show the trajectory and label depending on the selected aircraft state
            ChangeListener<? super AircraftState> selectedAircraftStateListener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends AircraftState> observable, AircraftState oldValue, AircraftState newValue) {
                    if (newValue == null || newValue == aircraftState) {
                        trajectoryLine.setOpacity(1.0);
                        callSignLabel.setOpacity(1.0);
                    } else {
                        trajectoryLine.setOpacity(0.0);
                        callSignLabel.setOpacity(0.0);
                    }
                }
            };
            selectedAircraftState.addListener(selectedAircraftStateListener);

            // add the aircraft group to the pane
            pane.getChildren().add(aircraftGroup);
        }
    }

    public Pane getPane() {
        return pane;
    }

}