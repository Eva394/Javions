package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          25/04/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Represents the base of the map. Manages the display and the interactions with the background.
 */
public final class BaseMapController {

    private static final int TILE_SIZE = 256;
    private boolean redrawNeeded;
    private final TileManager tileManager;
    private final ObjectProperty<MapParameters> mapParameters;  //TODO idk if this is a property or not
    private final Canvas canvas;
    private final Pane pane;
    private final LongProperty minScrollTime;
    private final ObjectProperty<Point2D> lastMousePosition;


    /**
     * Constructor. Builds an instance of <code>BaseMapManager</code>
     *
     * @param tileManager   tile manager
     * @param mapParameters parameters of the map (position and zoom)
     */
    public BaseMapController( TileManager tileManager, MapParameters mapParameters ) {
        this.tileManager = tileManager;
        this.mapParameters = new SimpleObjectProperty<>( mapParameters );
        this.canvas = new Canvas();
        this.pane = new Pane();
        pane.getChildren()
                .add( canvas );
        this.redrawNeeded = true;
        this.minScrollTime = new SimpleLongProperty();
        this.lastMousePosition = new SimpleObjectProperty<>();
        bindCanvasToPane();
        installListeners();
        installHandlers();

        canvas.sceneProperty()
                .addListener( ( p, oldS, newS ) -> {
                    assert oldS == null;
                    newS.addPreLayoutPulseListener( this::redrawIfNeeded );
                } );
    }


    /**
     * Returns the JavaFX pane which displays the base of the map
     *
     * @return the JavaFX pane which displays the base of the map
     */
    public Pane pane() {
        return pane;
    }


    /**
     * Centers the map on the given position (<code>position</code>)
     *
     * @param position the position to center the map on
     */
    public void centerOn( GeoPos position ) {
        int currentZoomLevel = mapParameters.get()
                .getZoom();
        mapParameters.get()
                .scroll( WebMercator.x( currentZoomLevel, position.longitude() ) - mapParameters.get().getMinX() - canvas.getWidth() / 2,
                        WebMercator.y( currentZoomLevel, position.latitude() ) - mapParameters.get().getMinY() - canvas.getHeight() / 2 );
    }


    private void storeMousePosition( Point2D position ) {
        lastMousePosition.set( position );
    }


    private void installHandlers() {
        pane.setOnScroll( this::zoomHandler );
        pane.setOnMouseDragged( this::movementHandler );
        pane.setOnMousePressed( event -> storeMousePosition( new Point2D( event.getX(), event.getY() ) ) );
        pane.setOnMouseReleased( event -> storeMousePosition( new Point2D( event.getX(), event.getY() ) ) );
    }


    private void movementHandler( MouseEvent event ) {
        Point2D newMousePosition = new Point2D( event.getX(), event.getY() );
        double xTranslation = lastMousePosition.get()
                .getX() - newMousePosition.getX();
        double yTranslation = lastMousePosition.get()
                .getY() - newMousePosition.getY();

        mapParameters.get()
                .scroll( xTranslation, yTranslation );
        storeMousePosition( newMousePosition );
    }


    private void zoomHandler( ScrollEvent event ) {
        int zoomDelta = ( int ) Math.signum( event.getDeltaY() );
        if ( zoomDelta == 0 ) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if ( currentTime < minScrollTime.get() ) {
            return;
        }
        minScrollTime.set( currentTime + 200 );

        double posMouseX = event.getX();
        double posMouseY = event.getY();

        mapParameters.get()
                .scroll( posMouseX, posMouseY );

        mapParameters.get()
                .changeZoomLevel( zoomDelta );

        mapParameters.get()
                .scroll( -posMouseX, -posMouseY );

        storeMousePosition( new Point2D( posMouseX, posMouseY ) );
    }


    private void installListeners() {
        canvas.heightProperty()
                .addListener( ( heightProperty, oldHeight, newHeight ) -> redrawOnNextPulse() );
        canvas.widthProperty()
                .addListener( ( width, oldWidth, newWidth ) -> redrawOnNextPulse() );
        //mapParameters.addListener( (parameter, oldParameter, newParameter) -> redrawOnNextPulse() );
    }


    private void defineGraphicsContexts() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double mapMinX = mapParameters.get()
                .getMinX();
        double mapMinY = mapParameters.get()
                .getMinY();
        int zoom = mapParameters.get()
                .getZoom();

        int firstRow = ( int ) mapMinX / TILE_SIZE;
        int firstColumn = ( int ) mapMinY / TILE_SIZE;

        int lastRow = ( int ) ( mapMinX + width ) / TILE_SIZE + 1;
        int lastColumn = ( int ) ( mapMinY + height ) / TILE_SIZE + 1;

        double shiftedRow = -( mapMinX - firstRow * TILE_SIZE );
        for ( int row = firstRow ; row < lastRow ; row++ ) {
            double shiftedCol = -( mapMinY - firstColumn * TILE_SIZE );
            for ( int col = firstColumn ; col < lastColumn ; col++ ) {
                if ( !TileManager.TileId.isValid( zoom, row, col ) ) {
                    continue;
                }

                TileManager.TileId tileId = new TileManager.TileId( zoom, row, col );
                try {
                    Image tile = tileManager.imageForTileAt( tileId );
                    graphicsContext.drawImage( tile, shiftedRow, shiftedCol );
                }
                catch ( IOException ignored ) {
                    ignored.printStackTrace();
                }
                shiftedCol += TILE_SIZE;
            }
            shiftedRow += TILE_SIZE;
        }
    }


    private void bindCanvasToPane() {
        canvas.widthProperty()
                .bind( pane.widthProperty() );
        canvas.heightProperty()
                .bind( pane.heightProperty() );
    }


    private void redrawIfNeeded() {
        if ( !redrawNeeded ) {
            return;
        }
        redrawNeeded = false;
        defineGraphicsContexts();
        redrawOnNextPulse();
    }


    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
