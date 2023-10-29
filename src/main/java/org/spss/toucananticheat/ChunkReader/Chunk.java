package org.spss.toucananticheat.ChunkReader;

import java.nio.ByteBuffer;
import java.util.List;

import org.spss.toucananticheat.Blocks.Blocks;
import org.spss.toucananticheat.ByteReader.ByteReader;
import org.spss.toucananticheat.ByteReader.ValInfo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

// https://www.spigotmc.org/threads/modify-chunk-before-being-sent-to-player.582295/
public class Chunk {
    private final int SECTION_HEIGHT = 16;
    private final int SECTION_WIDTH = 16;
    private final int SIZE_OF_BLOCK_STATES = 4096;

    private int chunkX;
    private int chunkZ;
    private boolean fullChunk;
    private int bitmask;
    private byte[] buffer;
    private int size;
    private PacketContainer packet;
    private List<Palette> palettes;

    public Chunk(PacketContainer packet) {
        if (packet.getType() == PacketType.Play.Server.MAP_CHUNK) {
            this.buffer = packet.getByteArrays().read(0);
            if (buffer.length == 0) {
                return;
            }
            this.chunkX = packet.getIntegers().read(0);
            this.chunkZ = packet.getIntegers().read(1);
            this.packet = packet;
            // int blockNum = ((int) buffer[0] << 8) + ((int) buffer[1]);
            readData();
            // System.out.println("buffer size: " + Integer.toString(buffer.length));
            // System.out.println("block num: " + Integer.toString(blockNum));
            // System.out.println("chunkX: " + Integer.toString(this.chunkX));
            // System.out.println("chunkZ: " + Integer.toString(this.chunkZ));
        } else {
            System.out.println("not chunk packet");
        }
    }

    private void readData() {
        int bytes_read = 0;
        int block_entry_no = 0;
        while (bytes_read < buffer.length) {
            int mask = 0xFF;
            int b0 = buffer[bytes_read] & mask;
            int b1 = buffer[bytes_read + 1] & mask;
            int blockNum = (b0 << 8) | b1;
            // System.out.println("blockNum: " + Integer.toString(blockNum));
            bytes_read += 2;
            // Unsigned bit
            int bits_per_entry = buffer[bytes_read] & 0xFF;
            bytes_read += 1;
            System.out.println("bits per entry: " + Integer.toString(bits_per_entry));
            if (bits_per_entry > 8 || bits_per_entry <= 0) {
                System.out.println("Invalid bits_per_entry");
                return;
            }
            if (bits_per_entry <= 4) {
                bits_per_entry = 4;
            }
            long test_block = 0;
            for (int j = 0; j < 8; j++) {
                test_block = (test_block << 8) + buffer[bytes_read + j];
            }
            //System.out.printf("long: 0x%16X\n", test_block);

            // Palette

            // Palette length
            ValInfo paletteLengthInfo = ByteReader.readVarInt(buffer, bytes_read);
            bytes_read += paletteLengthInfo.getBytes_read();
            int paletteLength = (int) paletteLengthInfo.getNum();
            // System.out.println("pallete length: " + Integer.toString(paletteLength));
            SectionPalette palette = new SectionPalette(chunkX, 0, chunkZ);
            // Read pallete
            for (int i = 0; i < paletteLength; i++) {
                ValInfo paletteInfo = ByteReader.readVarInt(buffer, bytes_read);
                // System.out.printf("Number %d", bytes_read);
                bytes_read += paletteInfo.getBytes_read();
                // System.out.printf("Number-Post %d\n", bytes_read);
                palette.addEntry((int) paletteInfo.getNum());
            }
            // Read data array length
            ValInfo data_array_length_info = ByteReader.readVarInt(buffer, bytes_read);
            int data_array_length = (int) data_array_length_info.getNum();
            System.out.println("data_array_length: " + Integer.toString(data_array_length));
            bytes_read += data_array_length_info.getBytes_read();

            int data_array_index = bytes_read;

            for (int i = 0; i < data_array_length; i++) {
                long block = 0;
                for (int j = 0; j < 8; j++) {
                    block = (block << 8) + ((long) buffer[bytes_read] & 0xFF);
                    bytes_read += 1;
                }
                // if (bits_per_entry == 5) {
                // System.out.printf("long: 0x%16X\n", block);
                // }
                // if (i == 0) {
                //     System.out.printf("first block: 0x%x\n", block);
                // }
                palette.readBlock(block, bits_per_entry);
            }

            // palette.printPalette();
            // palette.readMap();

            // Update the packet to remove all ores that don't have an adjacent air block
            List<Long> new_data_arr = palette.createOrefuscatedDataArr(bits_per_entry);
            if (new_data_arr.size() != data_array_length) {
                System.out.println("Error: Mismatch data array lengths");
                break;
            }

            // palette.readMap();

            for (int i = 0; i < new_data_arr.size(); i++) {
                long block = new_data_arr.get(i);
                ByteBuffer blockBuffer = ByteBuffer.allocate(Long.BYTES);
                blockBuffer.putLong(block);
                for (int j = 0; j < 8; j++) {
                    // if (buffer[data_array_index] != blockBuffer.get(j)) {
                    //     System.out.printf("Changing buffer at index %d from 0x%x to 0x%x\n", data_array_index, buffer[data_array_index], blockBuffer.get(j));
                    // }
                    buffer[data_array_index] = blockBuffer.get(j);
                    data_array_index++;
                }
            }
            // y += CHUNK_SECTION_HEIGHT;
        }
    }

    public PacketContainer createPacket() {
        PacketContainer newPacket = new PacketContainer(PacketType.Play.Server.MAP_CHUNK);
        newPacket.getByteArrays().write(0, buffer);
        newPacket.getIntegers().write(0, chunkX);
        newPacket.getIntegers().write(1, chunkZ);
        newPacket.getBooleans().write(0, fullChunk);
        newPacket.getIntegers().write(2, bitmask);
        return newPacket;
    }

    public void updateBlock(int x, int y, int z, byte newBlockId) {
        int blockIndex = getIndex(x, y, z);
        buffer[blockIndex] = newBlockId;
    }

    public byte getBlockId(int x, int y, int z) {
        return buffer[getIndex(x, y, z)];
    }

    private int getIndex(int x, int y, int z) {
        return y + (z * 16) + (x * 16 * 16);
    }

    public int getSize() {
        return size;
    }

    //https://stackoverflow.com/questions/14827398/converting-byte-array-values-in-little-endian-order-to-short-values
    public static short byteArrayToShortLE(final byte[] b, final int offset)
    {
        short value = 0;
        for (int i = 0; i < 2; i++)
        {
            value |= (b[i + offset] & 0x000000FF) << (i * 8);
        }

        return value;
    }
}