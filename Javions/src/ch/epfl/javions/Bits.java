package ch.epfl.javions;

import java.util.Objects;

/**
 * Contains methods for extracting a subset of a 64bit long value
 *
 * @author Eva Mangano 345375
 */
public final class Bits {
    
    
    private Bits() {
    
    }
    
    
    /**
     * Extracts a range of bits from the given value, from the index start and of given size.
     *
     * @param value value given form which to extract the range
     * @param start index of the start of the range to be extracted
     * @param size  size of the range to be extracted
     * @return the int value of the extracted range of bits
     * @throws IllegalArgumentException if the size is less than 1 or more than 32 (size of an int)
     * @author Eva Mangano 345375
     */
    public static int extractUInt( long value, int start, int size ) {
        
        Preconditions.checkArgument( ( size > 0 && size < Integer.SIZE ) );
        Objects.checkFromIndexSize( start, size, Long.SIZE );
        
        return ( int ) ( ( value << ( Long.SIZE - ( start + size ) ) >>> ( Long.SIZE - size ) ) );
    }
    
    
    /**
     * Returns true if the bit at the index is 1
     *
     * @param value given value
     * @param index index of the bit to look at
     * @return a boolean, true if the bit at the index is 1
     * @throws IndexOutOfBoundsException if the index is not between 0 (included) and 64 (excluded)
     * @author Eva Mangano 345375
     */
    public static boolean testBit( long value, int index ) {
        
        Objects.checkIndex( index, Long.SIZE );
        
        long mask = 1L << index;
        
        return ( value & mask ) == mask;
    }
}
