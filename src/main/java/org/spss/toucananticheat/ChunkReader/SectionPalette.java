package org.spss.toucananticheat.ChunkReader;

import java.util.ArrayList;
import java.util.List;

import org.spss.toucananticheat.Blocks.Blocks;

public class SectionPalette implements Palette {
    private List<Integer> entryIds;
    private List<Integer> paletteMap;

    // x, z and then y
    private int base_position[];

    public SectionPalette(int x, int y, int z) {
        entryIds = new ArrayList<>();
        paletteMap = new ArrayList<>();
        base_position = new int[3];
        base_position[0] = x;
        base_position[1] = z;
        base_position[2] = y;
    }

    @Override
    public int getBlock(int index) {
        return paletteMap.get(entryIds.get(index));
    }

    public int[] getLocation(int index) {
        int[] position = new int[3];
        position[2] = (int) index / 256 + (base_position[2] * 16);
        position[1] = ((index % 256) / 16) + (base_position[1] * 16);
        position[0] = (index % 256) % 16 + (base_position[0] * 16);
        return position;
    }

    public void addEntry(int entryId) {
        paletteMap.add(entryId);
        System.out.printf("       +: %s\n", Blocks.idToString(entryId));
    }

    @Override
    public void readBlock(long block, int bits_per_entry) {
        long mask = 0;
        for (int i = 0; i < bits_per_entry; i++) {
            mask = (mask << 1) + 1;
        }
        int shift_no = 0;
        for (int i = 0; i < Math.floor(64 / bits_per_entry); i++) {
            long val = (block & mask) >>> shift_no;
            entryIds.add((int) val);
            shift_no += bits_per_entry;
            mask = mask << bits_per_entry;
        }
    }

    public void readMap() {
        System.out.println(entryIds.size());
        // if (entryIds.size() == 4096) {
            for (int i = 0; i < entryIds.size(); i++) {
                int[] block_pos = getLocation(i);
                if (!Blocks.idToString(getBlock(i)).equals("minecraft:air")
                        && !Blocks.idToString(getBlock(i)).equals("minecraft:grass_block")
                        && !Blocks.idToString(getBlock(i)).equals("minecraft:bedrock")
                        && !Blocks.idToString(getBlock(i)).equals("minecraft:dirt")) {
                    System.out.printf("Block: %s at position %d x %d y %d z\n", Blocks.idToString(getBlock(i)),
                            block_pos[0],
                            block_pos[2], block_pos[1]);
                }
                // System.out.println("Read block: " + Blocks.idToString(getBlock(i)));
            }
        //}
    }

    public void printPalette() {
        //if (entryIds.size() == 4096) {
            System.out.println("-----------START Pallete---------------");
            for (int i = 0; i < paletteMap.size(); i++) {
                System.out.printf("Pallete : %s(%d)\n", Blocks.idToString(paletteMap.get(i)),paletteMap.get(i));
            }
            System.out.println("-----------END Pallete---------------");
        //}
    }
}
