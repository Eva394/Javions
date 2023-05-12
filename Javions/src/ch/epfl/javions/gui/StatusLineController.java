package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class StatusLineController {

    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController() {
        aircraftCountProperty = new SimpleIntegerProperty(0);
        messageCountProperty = new SimpleLongProperty(0);

        Text aircraftText = new Text();
        aircraftText.textProperty()
                .bind(Bindings.format("aeronef visible : %d",aircraftCountProperty));

        Text messageText = new Text();
        messageText.textProperty()
                .bind(Bindings.format("messages recus : %d",messageCountProperty));

        pane = new BorderPane(null, null, messageText, null, aircraftText);
        pane.getStyleClass().add("status");
    }

    public BorderPane pane() {
        return pane;
    }

    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }

}