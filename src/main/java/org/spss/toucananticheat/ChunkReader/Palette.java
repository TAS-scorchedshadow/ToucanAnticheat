package org.spss.toucananticheat.ChunkReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Palette {
    public int getBlock(int index);

    public void readBlock(long block, int bits_per_entry);
}
