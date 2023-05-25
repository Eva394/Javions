package ch.epfl.javions.gui;


import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manages the view of the table
 */
public final class AircraftTableController {

    private static final String TABLE_CSS = "/table.css";
    private static final String VELOCITY_COLUMN_TITLE = "Vitesse (km/h)";
    private static final String REGISTRATION_COLUMN_TITLE = "Immatriculation";
    private static final String CALL_SIGN_COLUMN_TITLE = "Indicatif";
    private static final String ICAO_COLUMN_TITLE = "OACI";
    private static final String MODEL_COLUMN_TITLE = "Modèle";
    private static final String TYPE_COLUMN_TITLE = "Type";
    private static final String DESCRIPTION_COLUMN_TITLE = "Description";
    private static final String LONGITUDE_COLUMN_TITLE = "Longitude (°)";
    private static final String LATITUDE_COLUMN_TITLE = "Latitude (°)";
    private static final String ALTITUDE_COLUMN_TITLE = "Altitude (m)";
    private static final int NUMERIC_COLUMN_WIDTH = 85;
    private static final int ICAO_COLUMN_WIDTH = 60;
    private static final int CALL_SIGN_COLUMN_WIDTH = 70;
    private static final int REGISTRATION_COLUMN_WIDTH = 90;
    private static final int MODEL_COLUMN_WIDTH = 230;
    private static final int TYPE_COLUMN_WIDTH = 50;
    private static final int DESCRIPTION_COLUMN_WIDTH = CALL_SIGN_COLUMN_WIDTH;
    private static final int DOUBLE_CLICK_COUNT = 2;
    private static final int DECIMAL_DIGITS_POSITION = 4;
    private static final int DECIMAL_DIGITS_ALT_VEL = 0;
    private static final NumberFormat POSITION_NUMBER_FORMAT = NumberFormat.getInstance();
    private static final NumberFormat ALT_VEL_NUMBER_FORMAT = NumberFormat.getInstance();
    private static final String NUMERIC_STYLE_CLASS = "numeric";
    private Consumer<ObservableAircraftState> consumer;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final TableView<ObservableAircraftState> pane;


    public AircraftTableController( ObservableSet<ObservableAircraftState> aircraftStates,
                                    ObjectProperty<ObservableAircraftState> selectedAircraftState ) {

        this.aircraftStates = Objects.requireNonNull( aircraftStates );
        this.selectedAircraftState = selectedAircraftState;
        this.pane = new TableView<>();
        pane.getStylesheets()
                .add( TABLE_CSS );
        pane.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS );
        pane.setTableMenuButtonVisible( true );

        setNumberFormats();

        pane.getColumns()
                .addAll( createColumns() );

        aircraftStates.addListener( ( SetChangeListener<? super ObservableAircraftState> ) change -> {
            if ( change.wasAdded() ) {
                pane.getItems()
                        .add( change.getElementAdded() );
                pane.sort();
            }
            else {
                pane.getItems()
                        .remove( change.getElementRemoved() );
                pane.sort();
            }
        } );

        selectedAircraftState.addListener( ( observableState, previousState, newState ) -> {
            if ( newState != null ) {
                if ( !Objects.equals( pane.getSelectionModel().getSelectedItem(), newState ) ) {
                    pane.scrollTo( newState );
                }
                pane.getSelectionModel()
                        .select( newState );

            }
        } );

        pane.getSelectionModel()
                .selectedItemProperty()
                .addListener( ( observableState, previousState, newState ) -> {
                    if ( newState != null && !Objects.equals( previousState, newState ) ) {
                        selectedAircraftState.set( newState );
                    }
                } );

        pane.setOnMouseClicked( event -> {
            if ( selectedAircraftState.get() != null && event.getButton()
                    .equals( MouseButton.PRIMARY )
                    && event.getClickCount() == DOUBLE_CLICK_COUNT ) {
                consumer.accept( selectedAircraftState.get() );
            }
        } );
    }

    public TableView<ObservableAircraftState> pane() {
        return pane;
    }

    public void setOnDoubleClick( Consumer<ObservableAircraftState> consumer ) {
        this.consumer = consumer;
    }

    private List<TableColumn<ObservableAircraftState, String>> createColumns() {
        List<TableColumn<ObservableAircraftState, String>> columns = new ArrayList<>();

        columns.add( createStringColumn( ICAO_COLUMN_TITLE, ICAO_COLUMN_WIDTH, state -> state.getIcaoAddress()
                .string() ) );
        columns.add( createCallSignColumn() );
        columns.add( createStringColumn( REGISTRATION_COLUMN_TITLE, REGISTRATION_COLUMN_WIDTH,
                state -> state.getAircraftData() != null ? state.getAircraftData()
                        .registration()
                        .string() : "" ) );
        columns.add( createStringColumn( MODEL_COLUMN_TITLE, MODEL_COLUMN_WIDTH,
                state -> state.getAircraftData() != null ? state.getAircraftData()
                        .model() : "" ) );
        columns.add( createStringColumn( TYPE_COLUMN_TITLE, TYPE_COLUMN_WIDTH,
                state -> state.getAircraftData() != null ? state.getAircraftData()
                        .typeDesignator()
                        .string() : "" ) );
        columns.add( createStringColumn( DESCRIPTION_COLUMN_TITLE, DESCRIPTION_COLUMN_WIDTH,
                state -> state.getAircraftData() != null ? state.getAircraftData()
                        .description()
                        .string() : "" ) );
        columns.add( createNumericColumn( LONGITUDE_COLUMN_TITLE, state -> Bindings.createDoubleBinding(
                () -> state.getPosition()
                        .longitude(), state.positionProperty() ), POSITION_NUMBER_FORMAT, Units.Angle.DEGREE ) );
        columns.add( createNumericColumn( LATITUDE_COLUMN_TITLE, state -> Bindings.createDoubleBinding(
                () -> state.getPosition()
                        .latitude(), state.positionProperty() ), POSITION_NUMBER_FORMAT, Units.Angle.DEGREE ) );
        columns.add( createNumericColumn( ALTITUDE_COLUMN_TITLE, ObservableAircraftState::altitudeProperty,
                ALT_VEL_NUMBER_FORMAT, Units.Length.METER ) );
        columns.add( createNumericColumn( VELOCITY_COLUMN_TITLE, ObservableAircraftState::velocityProperty,
                ALT_VEL_NUMBER_FORMAT, Units.Speed.KILOMETER_PER_HOUR ) );

        return columns;
    }

    private TableColumn<ObservableAircraftState, String> createCallSignColumn() {
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>();
        callSignColumn.setText( CALL_SIGN_COLUMN_TITLE );
        callSignColumn.setPrefWidth( CALL_SIGN_COLUMN_WIDTH );
        callSignColumn.setCellValueFactory( newLine -> newLine.getValue()
                .callSignProperty()
                .map( CallSign::string ) );
        return callSignColumn;
    }

    private TableColumn<ObservableAircraftState, String> createStringColumn( String columnTitle, int columnWidth,
                                                                             Function<ObservableAircraftState, String> function ) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>();
        column.setText( columnTitle );
        column.setPrefWidth( columnWidth );
        column.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( function.apply( newLine.getValue() ) ) );
        return column;
    }

    private TableColumn<ObservableAircraftState, String> createNumericColumn( String columnTitle,
                                                                              Function<ObservableAircraftState, DoubleExpression> function,
                                                                              NumberFormat numberFormat, double unit ) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>();
        column.setText( columnTitle );
        column.setPrefWidth( NUMERIC_COLUMN_WIDTH );
        column.getStyleClass()
                .add( NUMERIC_STYLE_CLASS );
        column.setComparator( ( valueString1, valueString2 ) -> {
            if ( valueString1.isEmpty() || valueString2.isEmpty() ) {
                return valueString1.compareTo( valueString2 );
            }
            else {
                double value1;
                double value2;
                try {
                    value1 = numberFormat.parse( valueString1 )
                            .doubleValue();
                    value2 = numberFormat.parse( valueString2 )
                            .doubleValue();
                    return Double.compare( value1, value2 );
                }
                catch ( Exception ignored ) {
                }
                return 0;
            }
        } );
        column.setCellValueFactory( state -> {
            Double value = function.apply( state.getValue() )
                    .getValue();
            if ( state.getValue().getIcaoAddress().string().equals( "3C4DC8" ) ) {
                System.out.println( "updating value : " + numberFormat.format( Units.convertTo( value, unit ) ) );
            }
            return new ReadOnlyStringWrapper(
                    !Double.isNaN( value ) ? numberFormat.format( Units.convertTo( value, unit ) ) : "" );
        } );

        return column;
    }

    private static void setNumberFormats() {
        POSITION_NUMBER_FORMAT.setMinimumFractionDigits( DECIMAL_DIGITS_POSITION );
        POSITION_NUMBER_FORMAT.setMaximumFractionDigits( DECIMAL_DIGITS_POSITION );
        ALT_VEL_NUMBER_FORMAT.setMinimumFractionDigits( DECIMAL_DIGITS_ALT_VEL );
        ALT_VEL_NUMBER_FORMAT.setMaximumFractionDigits( DECIMAL_DIGITS_ALT_VEL );
    }
}
