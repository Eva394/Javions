package ch.epfl.javions.gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableSet;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class StatusLineController {

    private final BorderPane pane;
    private final LongProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController(ObservableSet<ObservableAircraftState> aircraftStates,
                                ObjectProperty<ObservableAircraftState> selectedAircraftState) {

        AircraftTableController aircraftTableController = new AircraftTableController(aircraftStates, selectedAircraftState);
        int numLines = aircraftTableController.getNumLines();

        pane = new BorderPane();
        pane.getStyleClass().add("status");

        Text aircraftText = new Text();
        aircraftText.textProperty()
                .bind(new SimpleStringProperty("Visible aircraft: ")
                        .concat(String.valueOf(numLines)));

        /*Text messageText = new Text();
        messageText.textProperty()
                .bind(new SimpleStringProperty("Received messages: ")
                        .concat());

         */

        pane.setLeft(aircraftText);
        //pane.setRight(messageText);

        aircraftCountProperty = new SimpleLongProperty(0L);
        messageCountProperty = new SimpleLongProperty(0L);
    }

    public BorderPane getPane() {
        return pane;
    }

    public LongProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }

}