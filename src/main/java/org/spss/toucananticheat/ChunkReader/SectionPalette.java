package org.spss.toucananticheat.ChunkReader;

import java.util.ArrayList;
import java.util.List;

import org.spss.toucananticheat.Blocks.Blocks;

public class SectionPalette implements Palette {
    private final int airId = 0;
    private int nonOreBlock = 0;
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
    public int blockNum() {
        return entryIds.size();
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
        if (!Blocks.isOre(entryId)) {
            nonOreBlock = entryId;
        }
        paletteMap.add(entryId);
        // System.out.printf("       +: %s\n", Blocks.idToString(entryId));
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

    private void removeHiddenOres() {
        int blockPos[][][] = new int [16][16][16];
        int i = 0;
        List<Position> ores = new ArrayList<>();

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int block = getBlock(i);
                    blockPos[x][y][z] = block;
                    if (Blocks.isOre(block)) {
                        ores.add(new Position(x, y, z, i));
                    }
                    i++;
                }
            }
        }

        for (Position ore : ores) {
            boolean hasAir = false;
            int[] x = {ore.getX() - 1, ore.getX() + 1, ore.getX(), ore.getX(), ore.getX(), ore.getX()};
            int[] y = {ore.getY(), ore.getY(), ore.getY() - 1, ore.getY() + 1, ore.getY(), ore.getY()};
            int[] z = {ore.getZ(), ore.getZ(), ore.getZ(), ore.getZ(), ore.getZ() - 1, ore.getZ() + 1};
            for (int j = 0; j < 6; j++) {
                if (Position.validSectionPosition(x[j], y[j], z[j])) {
                    if (blockPos[x[j]][y[j]][z[j]] == airId) {
                        hasAir = true;
                    }
                }
            }
        

            // for (int y = ore.getY() - 1; y <= ore.getY() + 1; y++) {
            //     for (int z = ore.getZ() - 1; z <= ore.getZ() + 1; z++) {
            //         for (int x = ore.getX() - 1; x <= ore.getX() + 1; x++) {
            //             if (Position.validSectionPosition(x, y, z)) {
            //                 if (blockPos[x][y][z] == airId) {
            //                     hasAir = true;
            //                 }
            //             }
            //         }
            //     }
            // }
            if (!hasAir) {
                replaceBlock(ore);
            }
        }
    }

    // Replace the block with a block on the palette that isn't 
    private void replaceBlock(Position ore) {
        entryIds.set(ore.getIndex(), nonOreBlock);
    }

    public List<Long> createOrefuscatedDataArr(int bits_per_entry) {
        removeHiddenOres();
        List<Long> dataArr = new ArrayList<>();
        int blockNo = 0;
        int mask = 0;
        for (int i = 0; i < bits_per_entry; i++) {
            mask <<= 1;
            mask++;
        }
        for (int i = 0; i < 64 * bits_per_entry; i++) {
            long block = 0;
            for (int j = 0; j < Math.floor(64 / bits_per_entry); j++) {
                if (blockNo >= entryIds.size()) {
                    break;
                }
                block |= (((long) entryIds.get(blockNo)) << (j*bits_per_entry)) & (mask << (j*bits_per_entry));
                blockNo++;
            }
            dataArr.add(block);
        }
        return dataArr;
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

    public static void main(String[] args) {
        int block = 0;
        long mask = 0;
        for (int i = 0; i < 5; i++) {
            mask <<= 1;
            mask++;
        }
        int data = 0x1F;
        int bits_per_entry = 5;
        int j = 2;
        block |= (((long) data) << (j*bits_per_entry)) & (mask << (j*bits_per_entry));
        System.out.printf("%x", block);
    }
}
