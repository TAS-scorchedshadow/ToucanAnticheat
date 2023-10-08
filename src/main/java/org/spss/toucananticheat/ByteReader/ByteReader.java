package org.spss.toucananticheat.ByteReader;

public class ByteReader {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    // From https://wiki.vg/Data_types#VarInt_and_VarLong
    public static ValInfo readVarInt(byte[] buffer, int index) {
        int value = 0;
        int position = 0;
        int bytes_read = 0;
        byte currentByte;
        while (true) {
            currentByte = buffer[index + bytes_read];
            value += ((int) (currentByte & SEGMENT_BITS) & 0xFF) << position;
            bytes_read += 1;
            if ((currentByte & CONTINUE_BIT) == 0)
                break;

            position += 7;

            if (position >= 32)
                throw new RuntimeException("VarInt is too big");
        }
        return new ValInfo(value, bytes_read);
    }
}
