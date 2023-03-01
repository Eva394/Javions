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
    private int generator;


    /**
     * Constructor. Creates an instance of Crc24
     *
     * @param generator 24 lesser bits of the generator
     * @author Eva Mangano 345375
     */
    public Crc24(int generator) {
        //this.generator = ;
    }


    private static int crc_bitwise(byte[] bytes, int generator) {
        int crc = 0;
        int[] augmented = new int[24];

        for ( int i = bytes.length ; i > 0 ; i-- ) {
            crc = ( ( crc << Byte.SIZE ) | bytes[i] ) ^ bytes[bytes.length - 1];
        }

        for ( int i = augmented.length ; i > 0 ; i-- ) {
            crc = ( ( crc << Byte.SIZE ) | bytes[i] ) ^ augmented[augmented.length - 1];
        }

        return crc;
    }


    private static void buildtable() {
        for ( int i = 0 ; i < )
    }


    /**
     * @param bytes
     * @return
     */
    public int crc(byte[] bytes) {

        return crc_bitwise( bytes, generator );
    }
}

////METHOD TO CALCULATE A CRC
////METHOD TO TRANSFORM WHATEVER WE RECEIVE INTO 'A BYTE[] SO THAT WE CAN DO THE CRC