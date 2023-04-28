package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * represents the parameters of the portion of the map visible in the graphical interface
 * @author Nagyung Kim (339628)
 */

public final class MapParameters {
    public static final int MIN_ZOOM_LEVEL = 6;
    public static final int MAX_ZOOM_LEVEL = 19;
    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;


    /**
     * @param zoom a value of type int representing the zoom level
     * @param minX a double value representing the x-coordinate of the top-left corner of the visible portion of the
     *             map
     * @param minY a double value representing the y-coordinate of the top-left corner of the visible portion of the
     *             map
     */


    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument( MIN_ZOOM_LEVEL <= zoom && zoom <= MAX_ZOOM_LEVEL );
        this.zoom = new SimpleIntegerProperty( zoom );
        this.minX = new SimpleDoubleProperty( minX );
        this.minY = new SimpleDoubleProperty( minY );
    }


    /**
     * @return zoom value
     */


    public IntegerProperty zoomProperty() {
        return zoom;
    }


    /**
     * @return minX value
     */


    public DoubleProperty minXProperty() {
        return minX;
    }


    /**
     * @return minY value
     */


    public DoubleProperty minYProperty() {
        return minY;
    }


    /**
     * @return zoom level
     */


    public int getZoom() {
        return zoom.get();
    }


    /**
     * @return minX value
     */


    public double getMinX() {
        return minX.get();
    }


    /**
     * @return minY value
     */


    public double getMinY() {
        return minY.get();
    }


    /**
     * @param deltaX change in x value
     * @param deltaY change in y value
     */


    public void scroll(double deltaX, double deltaY) {
        minX.set( minX.get() + deltaX );
        minY.set( minY.get() + deltaY );
    }


    /**
     * @param deltaZoom change in zoom level
     */


    public void changeZoomLevel(int deltaZoom) {
        System.out.println( "changing zoom level" );

        int newZoom = Math2.clamp( MIN_ZOOM_LEVEL, zoom.get() + deltaZoom, MAX_ZOOM_LEVEL );
        System.out.println( "newZoom = " + newZoom );
        double factor = Math.scalb( 1d, newZoom - zoom.get() );
        minX.set( minX.get() * factor );
        minY.set( minY.get() * factor );
        zoom.set( newZoom );

        System.out.println( "zoomProperty().get() = " + zoomProperty().get() );
        System.out.println( "minXProperty().get() = " + minXProperty().get() );
        System.out.println( "minYProperty().get() = " + minYProperty().get() );
        System.out.println( "*******************************************" );
    }
}