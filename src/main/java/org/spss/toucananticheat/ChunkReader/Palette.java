package org.spss.toucananticheat.ChunkReader;

public interface Palette {
    public int getBlock(int index);

    public int blockNum();

    public void readBlock(long block, int bits_per_entry);
}
