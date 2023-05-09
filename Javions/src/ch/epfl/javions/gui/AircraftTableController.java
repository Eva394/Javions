package ch.epfl.javions.gui;


import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.Consumer;

import static ch.epfl.javions.Units.Angle.DEGREE;

public final class AircraftTableController {

    public static final String REGISTRATION_COLUMN_TITLE = "Immatriculation";
    public static final String CALL_SIGN_COLUMN_TITLE = "Indicatif";
    public static final String ICAO_COLUMN_TITLE = "OACI";
    public static final String MODEL_COLUMN_TITLE = "Modèle";
    public static final String TYPE_COLUMN_TITLE = "Type";
    public static final String DESCRIPTION_COLUMN_TITLE = "Description";
    public static final String LONGITUDE_COLUMN_TITLE = "Longitude (°)";
    public static final String LATITUDE_COLUMN_TITLE = "Latitude (°)";
    private static final NumberFormat LONGITUDE_AND_LATITUDE_NUMBER_FORMAT = NumberFormat.getInstance();
    private static final NumberFormat ALTITUDE_AND_VELOCITY_NUMBER_FORMAT = NumberFormat.getInstance();
    private static final String TABLE_CSS = "table.css";
    private static final int NUMERIC_COLUMN_WIDTH = 85;
    private static final int ICAO_COLUMN_WIDTH = 60;
    private static final int CALL_SIGN_COLUMN_WIDTH = 70;
    private static final int REGISTRATION_COLUMN_WIDTH = 90;
    private static final int MODEL_COLUMN_WIDTH = 230;
    private static final int TYPE_COLUMN_WIDTH = 50;
    private static final int DESCRIPTION_COLUMN_WIDTH = CALL_SIGN_COLUMN_WIDTH;
    private static final int DOUBLE_CLICK_COUNT = 2;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final TableView pane;


    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> selectedAircraftState) {

        this.aircraftStates = Objects.requireNonNull( aircraftStates );
        this.selectedAircraftState = selectedAircraftState;
        this.pane = new TableView();
        pane.getStyleClass()
            .add( TABLE_CSS );
        pane.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS );
        pane.setTableMenuButtonVisible( true );

        LONGITUDE_AND_LATITUDE_NUMBER_FORMAT.setMinimumFractionDigits( 4 );
        LONGITUDE_AND_LATITUDE_NUMBER_FORMAT.setMaximumFractionDigits( 4 );
        ALTITUDE_AND_VELOCITY_NUMBER_FORMAT.setMinimumFractionDigits( 0 );
        ALTITUDE_AND_VELOCITY_NUMBER_FORMAT.setMinimumFractionDigits( 0 );

        createICAOColumn();
        createCallSignColumn();
        createRegistrationColumn();
        createModelColumn();
        createTypeColumn();
        createDescriptionColumn();
        createLongitudeColumn();
        createLatitudeColumn();

        aircraftStates.addListener( (SetChangeListener<? super ObservableAircraftState>)change -> {
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
        selectedAircraftState.addListener( change -> {
            pane.getSelectionModel()
                .select( change );
            pane.scrollTo( change );
        } );
        pane.getSelectionModel()
            .selectedItemProperty()
            .addListener( newSelectedAircraft -> System.out.println( newSelectedAircraft.getClass() ) );
    }


    public TableView pane() {
        return pane;
    }


    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        pane.setOnMouseClicked( event -> {
            if ( selectedAircraftState != null && event.getButton()
                                                       .equals( MouseButton.PRIMARY ) && event.getClickCount() == DOUBLE_CLICK_COUNT ) {
                consumer.accept( selectedAircraftState.get() );
            }
        } );
    }

/*
    private void createLongitudeColumn() {
        TableColumn<ObservableAircraftState, Double> longitudeColumn = new TableColumn<>();
        longitudeColumn.getStyleClass()
                       .add( "numeric" );
        longitudeColumn.setText( LONGITUDE_COLUMN_TITLE );
        longitudeColumn.setPrefWidth( NUMERIC_COLUMN_WIDTH );
        longitudeColumn.setComparator( (o1, o2) -> {
            String o1String = LONGITUDE_AND_LATITUDE_NUMBER_FORMAT.format( o1 );
            String o2String = LONGITUDE_AND_LATITUDE_NUMBER_FORMAT.format( o2 );
            if ( o1String.isEmpty() || o2String.isEmpty() ) {
                return String.CASE_INSENSITIVE_ORDER.compare( o1String, o2String );
            }
            else {
                return Double.compare( o1, o2 );
            }
        } );

        pane.getColumns()
            .add( longitudeColumn );
    }

 */


    private void createLatitudeColumn() {
        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>();
        latitudeColumn.setText( LATITUDE_COLUMN_TITLE );
        latitudeColumn.setPrefWidth( NUMERIC_COLUMN_WIDTH );
        latitudeColumn.getStyleClass()
                      .add( "numeric" );
        latitudeColumn.setCellValueFactory( cellData -> {
            double longitude = Units.convertTo( cellData.getValue()
                                                        .getPosition()
                                                        .latitude(), DEGREE );
            return new SimpleStringProperty( LONGITUDE_AND_LATITUDE_NUMBER_FORMAT.format( longitude ) );
        } );

        latitudeColumn.setComparator( (latitudeString1, latitudeString2) -> {
            if ( latitudeString1.isEmpty() || latitudeString2.isEmpty() ) {
                return latitudeString1.compareTo( latitudeString2 );
            }
            else {
                double longitude1 = Double.parseDouble( latitudeString1 );
                double longitude2 = Double.parseDouble( latitudeString2 );
                return Double.compare( longitude1, longitude2 );
            }
        } );

        pane.getColumns()
            .add( latitudeColumn );
    }


    private void createLongitudeColumn() {
        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>();
        longitudeColumn.setText( LONGITUDE_COLUMN_TITLE );
        longitudeColumn.setPrefWidth( NUMERIC_COLUMN_WIDTH );
        longitudeColumn.getStyleClass()
                       .add( "numeric" );

        longitudeColumn.setCellValueFactory( cellData -> {
            double longitude = Units.convertTo( cellData.getValue()
                                                        .getPosition()
                                                        .longitude(), DEGREE );
            return new SimpleStringProperty( LONGITUDE_AND_LATITUDE_NUMBER_FORMAT.format( longitude ) );
        } );

        longitudeColumn.setComparator( (longitudeString1, longitudeString2) -> {
            if ( longitudeString1.isEmpty() || longitudeString2.isEmpty() ) {
                return longitudeString1.compareTo( longitudeString2 );
            }
            else {
                double longitude1 = Double.parseDouble( longitudeString1 );
                double longitude2 = Double.parseDouble( longitudeString2 );
                return Double.compare( longitude1, longitude2 );
            }
        } );

        // Add the longitude column to the table
        pane.getColumns()
            .add( longitudeColumn );
    }


    private void createDescriptionColumn() {
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>();
        descriptionColumn.setText( DESCRIPTION_COLUMN_TITLE );
        descriptionColumn.setPrefWidth( DESCRIPTION_COLUMN_WIDTH );
        descriptionColumn.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( newLine.getValue()
                                                                                            .getAircraftData() != null
                                                                                     ? newLine.getValue()
                                                                                              .getAircraftData()
                                                                                              .description()
                                                                                              .string()
                                                                                     : "" ) );
        pane.getColumns()
            .add( descriptionColumn );
    }


    private void createTypeColumn() {
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>();
        typeColumn.setText( TYPE_COLUMN_TITLE );
        typeColumn.setPrefWidth( TYPE_COLUMN_WIDTH );
        typeColumn.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( newLine.getValue()
                                                                                     .getAircraftData() != null
                                                                              ? newLine.getValue()
                                                                                       .getAircraftData()
                                                                                       .typeDesignator()
                                                                                       .string()
                                                                              : "" ) );
        pane.getColumns()
            .add( typeColumn );
    }


    private void createModelColumn() {
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>();
        modelColumn.setText( MODEL_COLUMN_TITLE );
        modelColumn.setPrefWidth( MODEL_COLUMN_WIDTH );
        modelColumn.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( newLine.getValue()
                                                                                      .getAircraftData() != null
                                                                               ? newLine.getValue()
                                                                                        .getAircraftData()
                                                                                        .model()
                                                                               : "" ) );
        pane.getColumns()
            .add( modelColumn );
    }


    private void createRegistrationColumn() {
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>();
        registrationColumn.setText( REGISTRATION_COLUMN_TITLE );
        registrationColumn.setPrefWidth( REGISTRATION_COLUMN_WIDTH );
        registrationColumn.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( newLine.getValue()
                                                                                             .getAircraftData() != null
                                                                                      ? newLine.getValue()
                                                                                               .getAircraftData()
                                                                                               .registration()
                                                                                               .string()
                                                                                      : "" ) );
        pane.getColumns()
            .add( registrationColumn );
    }


    private void createCallSignColumn() {
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>();
        callSignColumn.setText( CALL_SIGN_COLUMN_TITLE );
        callSignColumn.setPrefWidth( CALL_SIGN_COLUMN_WIDTH );
        callSignColumn.setCellValueFactory( newLine -> newLine.getValue()
                                                              .callSignProperty()
                                                              .map( CallSign::string ) );
        pane.getColumns()
            .add( callSignColumn );
    }


    private void createICAOColumn() {
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>();
        icaoColumn.setText( ICAO_COLUMN_TITLE );
        icaoColumn.setPrefWidth( ICAO_COLUMN_WIDTH );
        icaoColumn.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( newLine.getValue()
                                                                                     .getIcaoAddress()
                                                                                     .string() ) );
        pane.getColumns()
            .add( icaoColumn );
    }
}
