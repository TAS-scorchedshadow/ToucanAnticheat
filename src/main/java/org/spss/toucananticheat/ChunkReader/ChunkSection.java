package org.spss.toucananticheat.ChunkReader;

public class ChunkSection {
    private Palette palette;
    private byte skyLight[][][];

    public ChunkSection(Palette palette) {
        this.skyLight = new byte[16][16][16];
        this.palette = palette;
    }

    public void setSkyLight(int x, int y, int z, byte value) {
        skyLight[x][y][z] = value;
    }

    public int getBlock(int index) {
        return palette.getBlock(index);
    }

    // Returns true if ores were removed
    // Returns false if no ores were found/removed
    
}
