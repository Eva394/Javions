package ch.epfl.javions;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {
    private final byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
    
    public ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString.length() % 2 == 0) ;
    
        HexFormat hexFormat =
    }

   public int size(){
        return bytes.length;
   }

   public int byteAt(int index){
        if ( index<0 || index >= bytes.length){
            throw new IndexOutOfBoundsException();
        }
        else {
            return bytes[index];
        }
   }

    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);
        int numBytes = toIndex - fromIndex;
        Preconditions.checkArgument(numBytes < Long.SIZE );
        
        long result = 0 ;
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            result = (result << 8) | Byte.toUnsignedLong(bytes[i]);
        }
        return result;
    }
    
    
    @Override
    public int hashCode() {
        
        return Arrays.hashCode(bytes);
    }
    
    
    @Override
    public boolean equals(Object obj) {
        
        return Arrays.equals(obj);
    }
}