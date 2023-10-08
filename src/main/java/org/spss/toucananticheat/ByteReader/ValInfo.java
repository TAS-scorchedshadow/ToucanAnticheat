package org.spss.toucananticheat.ByteReader;

public class ValInfo {
    private int num;
    private int bytes_read;

    public ValInfo(int num, int bytes_read) {
        this.num = num;
        this.bytes_read = bytes_read;
    }

    public int getNum() {
        return num;
    }

    public int getBytes_read() {
        return bytes_read;
    }

}
