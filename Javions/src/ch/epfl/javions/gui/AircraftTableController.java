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

import static ch.epfl.javions.Units.Angle.DEGREE;

public final class AircraftTableController {

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
    private static final NumberFormat POSITION_NUMBER_FORMAT = NumberFormat.getInstance();
    private static final NumberFormat ALT_VEL_NUMBER_FORMAT = NumberFormat.getInstance();
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
    private final TableView pane; //TODO find the < > type


    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> selectedAircraftState) {

        this.aircraftStates = Objects.requireNonNull( aircraftStates );
        this.selectedAircraftState = selectedAircraftState;
        this.pane = new TableView<>();
        pane.getStyleClass()
            .add( TABLE_CSS );
        pane.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS );
        pane.setTableMenuButtonVisible( true );

        POSITION_NUMBER_FORMAT.setMinimumFractionDigits( 4 );
        POSITION_NUMBER_FORMAT.setMaximumFractionDigits( 4 );
        ALT_VEL_NUMBER_FORMAT.setMinimumFractionDigits( 0 );
        ALT_VEL_NUMBER_FORMAT.setMinimumFractionDigits( 0 );

        pane.getColumns()
            .addAll( createColumns() );

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


    private List<TableColumn<ObservableAircraftState, String>> createColumns() {
        List<TableColumn<ObservableAircraftState, String>> columns = new ArrayList<>();
        columns.add( createStringColumn( ICAO_COLUMN_TITLE,
                                         ICAO_COLUMN_WIDTH,
                                         state -> state.getIcaoAddress()
                                                       .string() ) );
//        columns.add( createStringColumn( CALL_SIGN_COLUMN_TITLE, CALL_SIGN_COLUMN_WIDTH, state -> state.callSignProperty().map( CallSign::string
//        ) ) );
        columns.add( createStringColumn( REGISTRATION_COLUMN_TITLE,
                                         REGISTRATION_COLUMN_WIDTH,
                                         state -> state.getAircraftData()
                                                       .registration()
                                                       .string() ) );
        columns.add( createStringColumn( MODEL_COLUMN_TITLE,
                                         MODEL_COLUMN_WIDTH,
                                         state -> state.getAircraftData()
                                                       .model() ) );
        columns.add( createStringColumn( TYPE_COLUMN_TITLE,
                                         TYPE_COLUMN_WIDTH,
                                         state -> state.getAircraftData()
                                                       .typeDesignator()
                                                       .string() ) );
        columns.add( createStringColumn( DESCRIPTION_COLUMN_TITLE,
                                         DESCRIPTION_COLUMN_WIDTH,
                                         state -> state.getAircraftData()
                                                       .description()
                                                       .string() ) );
        columns.add( createNumericColumn( LONGITUDE_COLUMN_TITLE,
                                          state -> Bindings.createDoubleBinding( () -> state.getPosition()
                                                                                            .longitude(), state.positionProperty() ),
                                          POSITION_NUMBER_FORMAT,
                                          DEGREE ) );
        columns.add( createNumericColumn( LATITUDE_COLUMN_TITLE,
                                          state -> Bindings.createDoubleBinding( () -> state.getPosition()
                                                                                            .longitude(), state.positionProperty() ),
                                          POSITION_NUMBER_FORMAT,
                                          DEGREE ) );
        columns.add( createNumericColumn( ALTITUDE_COLUMN_TITLE,
                                          ObservableAircraftState::altitudeProperty,
                                          ALT_VEL_NUMBER_FORMAT,
                                          Units.Length.METER ) );
        columns.add( createNumericColumn( VELOCITY_COLUMN_TITLE,
                                          ObservableAircraftState::velocityProperty,
                                          ALT_VEL_NUMBER_FORMAT,
                                          Units.Speed.KILOMETER_PER_HOUR ) );

        return columns;
    }

//    private void createLatitudeColumn() {
//        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>();
//        latitudeColumn.setText( LATITUDE_COLUMN_TITLE );
//        latitudeColumn.setPrefWidth( NUMERIC_COLUMN_WIDTH );
//        latitudeColumn.getStyleClass()
//                      .add( "numeric" );
//        latitudeColumn.setCellValueFactory( cellData -> {
//            double longitude = Units.convertTo( cellData.getValue()
//                                                        .getPosition()
//                                                        .latitude(), DEGREE );
//            return new SimpleStringProperty( POSITION_NUMBER_FORMAT.format( longitude ) );
//        } );
//
//        latitudeColumn.setComparator( (latitudeString1, latitudeString2) -> {
//            if ( latitudeString1.isEmpty() || latitudeString2.isEmpty() ) {
//                return latitudeString1.compareTo( latitudeString2 );
//            }
//            else {
//                double longitude1 = Double.parseDouble( latitudeString1 );
//                double longitude2 = Double.parseDouble( latitudeString2 );
//                return Double.compare( longitude1, longitude2 );
//            }
//        } );
//
//        pane.getColumns()
//            .add( latitudeColumn );
//    }
//
//
//    private void createLongitudeColumn() {
//        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>();
//        longitudeColumn.setText( LONGITUDE_COLUMN_TITLE );
//        longitudeColumn.setPrefWidth( NUMERIC_COLUMN_WIDTH );
//        longitudeColumn.getStyleClass()
//                       .add( "numeric" );
//
//        longitudeColumn.setCellValueFactory( cellData -> {
//            double longitude = Units.convertTo( cellData.getValue()
//                                                        .getPosition()
//                                                        .longitude(), DEGREE );
//            return new SimpleStringProperty( POSITION_NUMBER_FORMAT.format( longitude ) );
//        } );
//
//        longitudeColumn.setComparator( (longitudeString1, longitudeString2) -> {
//            if ( longitudeString1.isEmpty() || longitudeString2.isEmpty() ) {
//                return longitudeString1.compareTo( longitudeString2 );
//            }
//            else {
//                double longitude1 = Double.parseDouble( longitudeString1 );
//                double longitude2 = Double.parseDouble( longitudeString2 );
//                return Double.compare( longitude1, longitude2 );
//            }
//        } );
//
//        // Add the longitude column to the table
//        pane.getColumns()
//            .add( longitudeColumn );
//    }


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


    private TableColumn<ObservableAircraftState, String> createStringColumn(String columnTitle, int columnWidth,
                                                                            Function<ObservableAircraftState, String> function) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>();
        column.setText( columnTitle );
        column.setPrefWidth( columnWidth );
        column.setCellValueFactory( newLine -> new ReadOnlyStringWrapper( function.apply( newLine.getValue() ) ) );
        return column;
    }


    private TableColumn<ObservableAircraftState, String> createNumericColumn(String columnTitle,
                                                                             Function<ObservableAircraftState, DoubleExpression> function,
                                                                             NumberFormat numberFormat, double unit) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>();
        column.setText( columnTitle );
        column.setPrefWidth( NUMERIC_COLUMN_WIDTH );
        column.setComparator( (longitudeStr1, longitudeStr2) -> {
            if ( longitudeStr1.isEmpty() || longitudeStr2.isEmpty() ) {
                return longitudeStr1.compareTo( longitudeStr2 );
            }
            else {
                Double longitude1 = null;
                Double longitude2 = null;
                try {
                    longitude1 = numberFormat.parse( longitudeStr1 )
                                             .doubleValue();
                    longitude2 = numberFormat.parse( longitudeStr2 )
                                             .doubleValue();
                }
                catch ( Exception ignored ) {
                    ignored.printStackTrace();
                }
                return Double.compare( longitude1, longitude2 );
            }
        } );
        column.setCellValueFactory( state -> new ReadOnlyStringWrapper( numberFormat.format( Units.convertTo( function.apply( state.getValue() )
                                                                                                                      .getValue(), unit ) ) ) );

        return column;
    }
}
