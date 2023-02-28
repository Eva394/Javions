package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          28/02/2023
 */




/**
 * Represents a crc calculator oof 24 bits
 *
 * @author Eva Mangano 345375
 */
public final class Crc24 {

    /**
     * Contains the 24 lesser bits of the generator used to calculate the CRC24 of the ADS-B messages of the aircraft
     */
    public static int GENERATOR = 0xFFF409;


    /**
     * Constructor. Creates an instance of Crc24
     *
     * @param generator 24 lesser bits of the generator
     * @author Eva Mangano 345375
     */
    public Crc24(int generator) {

    }


    //TODO wtf is this
    //TODO use the optimized version (3rd)
    private static Crc24 crc_bitwise(byte[] bytes) {
/*        int crc = 0;

        byte[] augmentedBytes = new byte[bytes.length + 24];

        for ( int i = 0 ; i < augmentedBytes.length ; i++ ) {

        }*/

        return null;
    }


    /**
     * @param bytes
     * @return
     */
    public int crc(byte[] bytes) {
        return 0;
    }
}

////METHOD TO CALCULATE A CRC
////METHOD TO TRANSFORM WHATEVER WE RECEIVE INTO 'A BYTE[] SO THAT WE CAN DO THE CRC