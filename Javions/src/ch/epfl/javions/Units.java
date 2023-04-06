package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */



/**
 * @author Eva Mangano 345375 Defines the prefixes useful for the project. Contains definitions for Angle, Length, Time,
 * Speed.
 */
public final class Units {

    /**
     * Defines the value of the centi- prefix.
     */
    public static final double CENTI = 1e-2;

    /**
     * Defines the value of the kilo- prefix.
     */
    public static final double KILO = 1e3;


    private Units() {

    }


    /**
     * Converts the value given (expressed in the unit fromUnit) to the unit toUnit.
     * @param value    value to convert, expressed in the unit fromUnit
     * @param fromUnit unit from which to convert
     * @param toUnit   unit to which to convert
     * @return a double of the converted value
     * @author Eva Mangano 345375
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * ( fromUnit / toUnit );
    }


    /**
     * Converts the value given (expressed in the unit fromUnit) to the reference unit.
     * @param value    value to convert, expressed in the unit fromUnit
     * @param fromUnit unit from which to convert
     * @return a double of the converted value
     * @author Eva Mangano 345375
     */
    public static double convertFrom(double value, double fromUnit) {
        return value * fromUnit;
    }


    /**
     * Converts the value given (expressed in the reference unit) to the unit toUnit
     * @param value  value to convert, expressed in the reference unit
     * @param toUnit unit to convert to
     * @return a double of the converted value
     * @author Eva Mangano 345375
     */
    public static double convertTo(double value, double toUnit) {
        return value * ( 1 / toUnit );
    }


    /**
     * @author Eva Mangano 345375 Defines the units of the angles.
     */
    public final class Angle {

        /**
         * Value af a radian (reference unit)
         */
        public final static double RADIAN = 1.;
        /**
         * Value of a turn
         */
        public final static double TURN = 2 * Math.PI;
        /**
         * Value of a degree
         */
        public final static double DEGREE = TURN / ( 360 );
        /**
         * Value of a T32
         */
        public final static double T32 = TURN / Math.scalb( 1, 32 );


        private Angle() {
        }
    }



    /**
     * @author Eva Mangano 345375 Defines the units of length
     */
    public final class Length {

        /**
         * Value of a meter (reference unit)
         */
        public final static double METER = 1.;
        /**
         * Value of a centimeter
         */
        public final static double CENTIMETER = CENTI * METER;
        /**
         * Value of an inch
         */
        public final static double INCH = 2.54 * CENTIMETER;
        /**
         * Value of a foot
         */
        public final static double FOOT = 12 * INCH;
        /**
         * Value of a kilometer
         */
        public final static double KILOMETER = KILO * METER;
        /**
         * Value of a nautical mile
         */
        public final static double NAUTICAL_MILE = 1852 * METER;


        private Length() {
        }
    }



    /**
     * @author Eva Mangano 345375 Defines the units of time
     */
    public final class Time {

        /**
         * Value of a second (reference unit)
         */
        public final static double SECOND = 1.;
        /**
         * Value of a minute
         */
        public final static double MINUTE = 60 * SECOND;
        /**
         * Value of an hour
         */
        public final static double HOUR = 60 * MINUTE;


        private Time() {
        }
    }



    /**
     * @author Eva Mangano 345375 Defines the units of speed
     */

    public final class Speed {

        /**
         * Value of a knot
         */
        public final static double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        /**
         * Value of a kilometer per hour
         */
        public final static double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }
}
