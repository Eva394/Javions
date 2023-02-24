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
        Preconditions.checkArgument(index > 0 && index <= bytes.length);
        int i = bytes[index];
    }

    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);
        int numBytes = toIndex - fromIndex;
        Preconditions.checkArgument(numBytes < Long.SIZE);

        long result = 0;
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            result = (result << 8) | Byte.toUnsignedLong(bytes[i]);
        }
        return result;
    }


    @Override
    public int hashCode() {

        return Arrays.hashCode(bytes);
    }
}
