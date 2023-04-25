package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          25/04/2023
 */


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class MapParameters {

    private final IntegerProperty zoom;

    //TODO idk if this is double or int
    private final DoubleProperty minX;
    //TODO idk if this is double or int
    private final DoubleProperty minY;


    public MapParameters(int zoom, double minX, double minY) {
        //TODO i needed intellij to stop doing errors everywhere so i created this
        //  it should be correct so far but idk if theres something more to do or not
        this.zoom = new SimpleIntegerProperty( zoom );
        this.minX = new SimpleDoubleProperty( minX );
        this.minY = new SimpleDoubleProperty( minY );
    }


    public int getZoom() {
        //TODO i needed intellij to stop doing errors everywhere so i created this but the body is still to do
        return 0;
    }


    public double getMinX() {
        //TODO i needed intellij to stop doing errors everywhere so i created this but the body is still to do
        return 0;
    }


    public double getMinY() {
        //TODO i needed intellij to stop doing errors everywhere so i created this but the body is still to do
        return 0;
    }
}
