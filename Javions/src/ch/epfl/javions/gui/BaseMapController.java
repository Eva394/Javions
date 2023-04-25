package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          25/04/2023
 */


import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {

    private static final int TILE_SIZE = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Canvas canvas;
    private final Pane pane;
    private final SimpleLongProperty minScrollTime = new SimpleLongProperty();
    private boolean redrawNeeded;
    private WebMercator lastMousePosition;


    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        this.pane = new Pane( canvas );
        this.redrawNeeded = true;
        bindCanvasToPane();
        defineGraphicsContexts();

        //pane.getChildren().add(canvas) ;

        canvas.sceneProperty()
              .addListener( (p, oldS, newS) -> {
                  assert oldS == null;
                  newS.addPreLayoutPulseListener( this::redrawIfNeeded );
              } );
    }


    private void defineGraphicsContexts() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        TileManager.TileId tileId = new TileManager.TileId( mapParameters.getZoomProperty(),
                                                            mapParameters.getMinXProperty(),
                                                            mapParameters.getMinYProperty() );
        try {
            graphicsContext.drawImage( tileManager.imageForTileAt( tileId ), mapParameters.getMinXProperty(),
                                       mapParameters.getMinYProperty() );
        }
        catch ( IOException ignored ) {
            ignored.printStackTrace();
        }
    }


    private void bindCanvasToPane() {
        canvas.widthProperty()
              .bind( pane.widthProperty() );
        canvas.heightProperty()
              .bind( pane.heightProperty() );
    }


    public Pane pane() {
        return pane;
    }


    public void centerOn(GeoPos position) {
        //TODO
    }


    private void redrawIfNeeded() {
        if ( !redrawNeeded ) {
            return;
        }
        redrawNeeded = false;
        redrawOnNextPulse();
    }


    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
