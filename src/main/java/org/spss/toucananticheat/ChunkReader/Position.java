package org.spss.toucananticheat.ChunkReader;

public class Position {
    private int x;
    private int y;
    private int z;
    private int index;

    public static boolean validSectionPosition(int x, int y, int z) {
        if (x >= 0 && x < 16 && y >= 0 && y < 16 && z >= 0 && z < 16) {
            return true;
        }
        return false;
    }

    public static boolean validSectionPosition(Position p) {
        int x = p.getX();
        int y = p.getY();
        int z = p.getZ();
        return validSectionPosition(x, y, z);
    }

    public Position(int x, int y, int z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getIndex() {
        return index;
    }
}
