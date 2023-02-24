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

    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString.length() % 2 == 0);

        HexFormat hexFormat = HexFormat.of().withUpperCase();
        byte[] tempBytes = hexFormat.parseHex(hexString);
        return new ByteString(tempBytes);
    }


    public int size() {
        return bytes.length;
    }

    public int byteAt(int index) {
        Preconditions.checkArgument(index > 0 || index <= bytes.length);
        return Byte.toUnsignedInt(bytes[index]);
    }

    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);
        int numBytes = toIndex - fromIndex;
        Preconditions.checkArgument(numBytes < Long.SIZE);

        long result = 0;
        for (int i = 0; i < toIndex; i++) {
            result = (result << 8) | Byte.toUnsignedLong(bytes[i]);
        }
        return result;
    }


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

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }


}
