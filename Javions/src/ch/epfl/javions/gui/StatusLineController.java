package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Represents the view of the status line
 *
 * @author Nagyung Kim (339628)
 */
public class StatusLineController {
    
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;
    
    
    /**
     * Constructor. Builds an instance of <code>StatusLineController</code>
     */
    public StatusLineController() {
        aircraftCountProperty = new SimpleIntegerProperty( 0 );
        messageCountProperty = new SimpleLongProperty( 0 );
        
        Text aircraftText = new Text();
        aircraftText.textProperty()
                    .bind( Bindings.format( " Aéronefs visibles : %d", aircraftCountProperty ) );
        
        Text messageText = new Text();
        messageText.textProperty()
                   .bind( Bindings.format( "Messages reçus : %d", messageCountProperty ) );
        
        pane = new BorderPane( null, null, messageText, null, aircraftText );
        pane.getStyleClass()
            .add( "status" );
    }
    
    
    /**
     * Getter for the pane for the view of the status line
     *
     * @return the pane
     */
    public BorderPane pane() {
        return pane;
    }
    
    
    /**
     * Getter for the property of the number of aircrafts <code>aircraftCountProperty</code>
     *
     * @return the property of the number of aircrafts
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }
    
    
    /**
     * Getter for the property of the number of messages <code>messageCountProperty</code>
     *
     * @return the property of the number of messages
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}