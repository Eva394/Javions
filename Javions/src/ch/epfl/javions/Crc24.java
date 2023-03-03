package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          28/02/2023
 */




/**
 * Represents a crc calculator of 24 bits
 *
 * @author Eva Mangano 345375
 */
public final class Crc24 {

    /**
     * Contains the 24 lesser bits of the generator used to calculate the CRC24 of the ADS-B messages of the aircraft
     */
    public final static int GENERATOR = 0xFFF409;
    private final static int CRC_SIZE = 24;
    private int[] table;


    /**
     * Constructor. Creates an instance of Crc24
     *
     * @param generator 24 lesser bits of the generator
     * @author Eva Mangano 345375
     */
    public Crc24(int generator) {
        this.table = buildtable( generator );
    }


    /**
     * Treats the crc bit by bit
     *
     * @param bytes     array of bytes to be treated
     * @param generator 24 lesser bits of the generator
     * @return the crc
     * @author Eva Mangano 345375
     */
    private static int crc_bitwise(byte[] bytes, int generator) {
        long crc = 0;
        int[] generatorTable = new int[]{0, generator};

        for ( int i = 0 ; i < bytes.length ; i++ ) {
            for ( int bit = Byte.SIZE - 1 ; bit >= 0 ; bit-- ) {
                int b = Bits.extractUInt( bytes[i], bit, 1 );
                int crcHighOrderBit = Bits.extractUInt( crc, CRC_SIZE - 1, 1 );

                crc = ( ( crc << 1 ) | b ) ^ generatorTable[crcHighOrderBit];
            }
        }

        for ( int i = 0 ; i < CRC_SIZE ; i++ ) {
            int crcHighOrderBit = Bits.extractUInt( crc, CRC_SIZE - 1, 1 );

            crc = ( crc << 1 ) ^ generatorTable[crcHighOrderBit];
        }

        return Bits.extractUInt( crc, 0, CRC_SIZE );
    }


    /**
     * Builds a table of 256 inputs corresponding to the generator
     *
     * @param generator 24 lesser bits of the generator
     * @return array of ints containing the generator
     */
    private static int[] buildtable(int generator) {
        int[] generatorTable = new int[256];

        for ( int i = 0 ; i < 256 ; i++ ) {
            generatorTable[i] = crc_bitwise( new byte[]{(byte)i}, generator );
        }

        return generatorTable;
    }


    /**
     * Computes the crc of the array </bytes>
     *
     * @param bytes array to be treated
     * @return the crc
     */
    public int crc(byte[] bytes) {
        int crc = 0;

        for ( int octet = 0 ; octet < bytes.length ; octet++ ) {
            int o = Byte.toUnsignedInt( bytes[octet] );
            int crcHighOrderByte = Bits.extractUInt( crc, CRC_SIZE - Byte.SIZE, Byte.SIZE );

            crc = ( ( crc << 8 ) | o ) ^ table[crcHighOrderByte];
        }

        for ( int octet = 0 ; octet < CRC_SIZE / Byte.SIZE ; octet++ ) {
            int crcHighOrderByte = Bits.extractUInt( crc, CRC_SIZE - Byte.SIZE, Byte.SIZE );

            crc = ( crc << 8 ) ^ table[crcHighOrderByte];
        }

        return Bits.extractUInt( crc, 0, CRC_SIZE );
    }
}