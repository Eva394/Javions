package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

public final class MapParameters {
    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom < 6 || zoom > 19); {
        }
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    public IntegerProperty zoomProperty() {
        return zoom;
    }

    public DoubleProperty minXProperty() {
        return minX;
    }

    public DoubleProperty minYProperty() {
        return minY;
    }

    public int getZoom() {
        return zoom.get();
    }

    public int getMinX() {
        return (int) minX.get();
    }

    public int getMinY() {
        return (int) minY.get();
    }

    public void scroll(double deltaX, double deltaY) {
        minX.set(minX.get() + deltaX);
        minY.set(minY.get() + deltaY);
    }

    public void changeZoomLevel(int deltaZoom) {
        int newZoom = zoom.get() + deltaZoom;

        Math2.clamp(6, newZoom,19);

        double factor = Math.pow(2, newZoom - zoom.get());
        minX.set(minX.get() * factor);
        minY.set(minY.get() * factor);
        zoom.set(newZoom);
    }
}