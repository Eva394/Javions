package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

/**
 * Represents a gradation of colors
 * @author Nagyung Kim (339628)
 */

public final class ColorRamp {

    /**
     * The predefined color ramp representing the plasma spectrum.
     */

    public static final ColorRamp PLASMA = new ColorRamp( Color.valueOf( "0x0d0887ff" ),
            Color.valueOf( "0x220690ff" ),
            Color.valueOf( "0x320597ff" ),
            Color.valueOf( "0x40049dff" ),
            Color.valueOf( "0x4e02a2ff" ),
            Color.valueOf( "0x5b01a5ff" ),
            Color.valueOf( "0x6800a8ff" ),
            Color.valueOf( "0x7501a8ff" ),
            Color.valueOf( "0x8104a7ff" ),
            Color.valueOf( "0x8d0ba5ff" ),
            Color.valueOf( "0x9814a0ff" ),
            Color.valueOf( "0xa31d9aff" ),
            Color.valueOf( "0xad2693ff" ),
            Color.valueOf( "0xb6308bff" ),
            Color.valueOf( "0xbf3984ff" ),
            Color.valueOf( "0xc7427cff" ),
            Color.valueOf( "0xcf4c74ff" ),
            Color.valueOf( "0xd6556dff" ),
            Color.valueOf( "0xdd5e66ff" ),
            Color.valueOf( "0xe3685fff" ),
            Color.valueOf( "0xe97258ff" ),
            Color.valueOf( "0xee7c51ff" ),
            Color.valueOf( "0xf3874aff" ),
            Color.valueOf( "0xf79243ff" ),
            Color.valueOf( "0xfa9d3bff" ),
            Color.valueOf( "0xfca935ff" ),
            Color.valueOf( "0xfdb52eff" ),
            Color.valueOf( "0xfdc229ff" ),
            Color.valueOf( "0xfccf25ff" ),
            Color.valueOf( "0xf9dd24ff" ),
            Color.valueOf( "0xf5eb27ff" ),
            Color.valueOf( "0xf0f921ff" ) );
    private static final int MIN_NUMBER_COLOURS = 2;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 1;
    private final Color[] colors;

    /**
     * Constructs a ColorRamp with the specified colors.
     *
     * @param colors the array of colors defining the ramp
     * @throws IllegalArgumentException if the number of colors is less than 2
     */


    public ColorRamp( Color... colors ) {

        Preconditions.checkArgument( colors.length >= MIN_NUMBER_COLOURS );
        this.colors = colors;
    }

    /**
     * Retrieves the interpolated color at the given position in the color ramp.
     *  @param value the position in the ramp, between 0.0 and 1.0 (inclusive)
     *  @return the interpolated color at the specified position in the ramp
     */


    public Color at( double value ) {

        if ( value <= MIN_VALUE ) {
            return colors[0];
        }

        if ( value >= MAX_VALUE ) {
            return colors[colors.length - 1];
        }

        int index = (int) ( value * ( colors.length - 1 ) );
        double weight = ( value - (double) index / ( colors.length - 1 ) ) * ( colors.length - 1 );

        if ( index < colors.length - 1 ) {
            return colors[index].interpolate( colors[index + 1], weight );
        } else {
            return colors[index];
        }
    }




}

