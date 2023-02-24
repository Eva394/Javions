package ch.epfl.javions;

/** @author Nagyung Kim (339628)*/

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {
    private final byte[] bytes;

    /**
     * gives a byte string
     * whose content is that of the array passed as argument.
     * @param bytes:
     *             array of bytes
     *
     */

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * copy the array which passed the constructor
     * @return a copy of the array which passed the constructor
     */

    public byte[] getBytes() {
        return bytes.clone();
    }


    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString.length() % 2 == 0);

        HexFormat hexFormat = HexFormat.of().withUpperCase();
        byte[] tempBytes = hexFormat.parseHex(hexString);
        return new ByteString(tempBytes);
    }

    /**
     * returns the size of the string (the number of bytes it contains)
     * @return the size of the string (the number of bytes it contains)
     */


    public int size() {
        return bytes.length;
    }

    /**
     * shows the bytes at the given index
     * @param index
     *          index
     * @return the byte at the given index
     * @throws IndexOutOfBoundsException
     *          if the index is invalid
     */


    public int byteAt(int index) {

        if (index < 0 || index >= bytes.length) {
            throw new IndexOutOfBoundsException();
        } else {
            return Byte.toUnsignedInt(bytes[index]);
        }
    }

    /**
     * shows the bytes between the fromIndex and toIndex
     * @param fromIndex
     *          the index indicates the start of the range (inclusive)
     * @param toIndex
     *          the index indicates the end of the range (exclusive)
     * @return the bytes between the fromIndex and toIndex
     * @throws IndexOutOfBoundsException
     *          if the ranged is not entirely between 0 and size of the string
     * @throws IllegalArgumentException
     *          if the difference between toIndex and fromIndex is not strictly
     *          less than the number of bytes contained in a long type value
     */

    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);
        int numBytes = toIndex - fromIndex;

        if (numBytes > Long.SIZE) {
            throw new IndexOutOfBoundsException();
        } else {
            long result = 0;
            for (int i = 0; i < toIndex; i++) {
                result = (result << 8) | Byte.toUnsignedLong(bytes[i]);
            }
            return result;
        }
    }

    /**
     * shows a representation of the bytes of the string in hexadecimal
     * @return a representation of the bytes of the string in hexadecimal
     */


    @Override
    public String toString(){
        HexFormat hf = HexFormat.of().withUpperCase();
        String string = hf.formatHex(bytes);
        return string;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ByteString object) {
            return Arrays.equals(object.bytes, this.bytes) ;
        }
        else {
            return  false ;
        }
    }

    /**
     * returns the value returned by the hashCode method of Arrays
     * @return the value returned by the hashCode method of Arrays
     * applied to the array containing the bytes
     */

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }


}
