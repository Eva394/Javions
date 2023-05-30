package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * represents the parameters of the portion of the map visible in the graphical interface
 *
 * @author Nagyung Kim (339628)
 */
public final class MapParameters {
	private static final int MIN_ZOOM_LEVEL = 6;
	private static final int MAX_ZOOM_LEVEL = 19;
	private final IntegerProperty zoom;
	private final DoubleProperty minX;
	private final DoubleProperty minY;
	
	
	/**
	 * Constructor. Builds an instance of <code>MapParameters</code>
	 *
	 * @param zoom a value of type int representing the zoom level
	 * @param minX a double value representing the x-coordinate of the top-left corner of the visible portion of the
	 *             map
	 * @param minY a double value representing the y-coordinate of the top-left corner of the visible portion of the
	 *             map
	 * @throws IllegalArgumentException if the zoom level (<code>zoom</code>) is out of bounds
	 */
	public MapParameters( int zoom, double minX, double minY ) {
		Preconditions.checkArgument( MIN_ZOOM_LEVEL <= zoom && zoom <= MAX_ZOOM_LEVEL );
		this.zoom = new SimpleIntegerProperty( zoom );
		this.minX = new SimpleDoubleProperty( minX );
		this.minY = new SimpleDoubleProperty( minY );
	}
	
	
	/**
	 * Getter for the <code>zoom</code> property
	 *
	 * @return zoom property
	 */
	public IntegerProperty zoomProperty() {
		return zoom;
	}
	
	
	/**
	 * Getter for the <code>minX</code> property
	 *
	 * @return minX property
	 */
	public DoubleProperty minXProperty() {
		return minX;
	}
	
	
	/**
	 * Getter for the <code>minY</code> property
	 *
	 * @return minY property
	 */
	public DoubleProperty minYProperty() {
		return minY;
	}
	
	
	/**
	 * Getter for the <code>zoom</code> value
	 *
	 * @return zoom level
	 */
	public int getZoom() {
		return zoom.get();
	}
	
	
	/**
	 * Getter for the <code>minX</code> value
	 *
	 * @return minX value
	 */
	public double getMinX() {
		return minX.get();
	}
	
	
	/**
	 * Getter for the <code>minY</code> value
	 *
	 * @return minY value
	 */
	public double getMinY() {
		return minY.get();
	}
	
	
	/**
	 * Moves the map of <code>deltaX</code> in the X-axis direction and of <code>deltaY</code> in the Y-axis direction
	 *
	 * @param deltaX change in x value
	 * @param deltaY change in y value
	 */
	public void scroll( double deltaX, double deltaY ) {
		minX.set( minX.get() + deltaX );
		minY.set( minY.get() + deltaY );
	}
	
	
	/**
	 * Changes the zoom level of <code>deltaZoom</code>
	 *
	 * @param deltaZoom change in zoom level
	 */
	public void changeZoomLevel( int deltaZoom ) {
		int newZoom = Math2.clamp( MIN_ZOOM_LEVEL, zoom.get() + deltaZoom, MAX_ZOOM_LEVEL );
		double factor = Math.scalb( 1d, newZoom - zoom.get() );
		
		minX.set( minX.get() * factor );
		minY.set( minY.get() * factor );
		zoom.set( newZoom );
	}
}