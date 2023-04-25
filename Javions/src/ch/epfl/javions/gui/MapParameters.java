package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          25/04/2023
 */


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class MapParameters {

    private final IntegerProperty zoom;

    //TODO idk if this is double or int
    private final IntegerProperty minX;
    //TODO idk if this is double or int
    private final IntegerProperty minY;


    public MapParameters(int zoom, int minX, int minY) {
        //TODO i needed intellij to stop doing errors everywhere so i created this
        //  it should be correct so far but idk if theres something more to do or not
        this.zoom = new SimpleIntegerProperty( zoom );
        this.minX = new SimpleIntegerProperty( minX );
        this.minY = new SimpleIntegerProperty( minY );
    }


    public int getZoom() {
        //TODO i needed intellij to stop doing errors everywhere so i created this but the body is still to do
        return 0;
    }


    public int getMinX() {
        //TODO i needed intellij to stop doing errors everywhere so i created this but the body is still to do
        return 0;
    }


    public int getMinY() {
        //TODO i needed intellij to stop doing errors everywhere so i created this but the body is still to do
        return 0;
    }
}
