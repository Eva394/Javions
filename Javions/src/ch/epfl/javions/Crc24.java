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
    public final static int GENERATOR = 0xFFF409;
    private final static int CRC_SIZE = 24;
    private byte[] table;


    /**
     * Constructor. Creates an instance of Crc24
     *
     * @param generator 24 lesser bits of the generator
     * @author Eva Mangano 345375
     */
    public Crc24(int generator) {
        // this.table = buildtable(generator); ;
    }


    private static int crc_bitwise(byte[] bytes, int generator) {
        long crc = 0;

        for ( int i = 0 ; i < bytes.length ; i++ ) {
            for ( int bit = Byte.SIZE - 1 ; bit >= 0 ; bit-- ) {
                int b = Bits.extractUInt( bytes[i], bit, 1 );
                crc = ( crc << 1 ) | b;
                //System.out.println( Bits.extractUInt( crc, CRC_SIZE, 24 ) );

                if ( Bits.extractUInt( crc, CRC_SIZE, 1 ) == 1 ) {
                    crc = crc ^ generator;
                }
            }
        }

        for ( int i = 0 ; i < CRC_SIZE ; i++ ) {
            crc = crc << 1;

            if ( Bits.extractUInt( crc, CRC_SIZE, 1 ) == 1 ) {
                crc = crc ^ generator;
            }
        }

        //        for ( int i = 0 ; i < bytes.length ; i++ )
        //            for ( int j = bytes.length ; j > 0 ; j-- ) {
        //                crc = ( ( crc << 1 ) | bytes[i] ) ^ bytes[table.length - 1] ;
        //            }
        ////
        //        for ( int i = augmented.length ; i > 0 ; i-- ) {
        //            crc = ( ( crc << Byte.SIZE ) | bytes[i] ) ^ table[table.length - 1];
        //        }

        return Bits.extractUInt( crc, 0, CRC_SIZE );
    }

    //    private static int[] buildtable(int generator) {
    //        // for ( int i = 0 ; i < )
    //    }


    /**
     * @param bytes
     * @return
     */
    public int crc(byte[] bytes) {

        return crc_bitwise( bytes, Crc24.GENERATOR );
    }
}