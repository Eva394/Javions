package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

public final class ColorRamp {

    /**
     * List of colours in the plasma spectrum
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
    public static final int MIN_NUMBER_COLOURS = 2;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 1;
    private final Color[] colors;


    public ColorRamp(Color... colors) {

        Preconditions.checkArgument( colors.length >= MIN_NUMBER_COLOURS );
        this.colors = colors;
    }

    /*

    public Color at(double position) {
        if (position <= 0) {
            return colors[0];
        } else if (position >= 1) {
            return colors[colors.length - 1];
        } else {
            int index = (int) (position * (colors.length - 1));
            double weight = (position - (double) index / (colors.length - 1)) * (colors.length - 1);
            return colors[index].interpolate(colors[index + 1], weight);
        }
    }

     */


    public Color at(double value) {

        double position = Math2.clamp( MIN_VALUE, value, MAX_VALUE );

        int index = (int) (position * (colors.length - 1));
        double weight = (position - (double) index / (colors.length - 1)) * (colors.length - 1);
        if (index < colors.length - 1) {
            return colors[index].interpolate(colors[index + 1], weight);
        } else {
            return colors[index];
        }


        /*
        int i = (int)( val * ( colors.length - 1 ) );
        double location = ( val - i * 1.0 / ( colors.length - 1 ) ) * ( colors.length - MAX_VALUE );

        return colors[i - MAX_VALUE].interpolate( colors[i], location );

         */


    }


}

