package org.spss.toucananticheat;
import java.util.UUID;

public class NukeCounter {
    private UUID playerUUID;
    private String lastPacketType = "BREAK";
    private Integer repeatOccurances = 0;

    public NukeCounter(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void processPacket(String packetType) {
        if (packetType.equals("MOVE") && lastPacketType.equals("BREAK")) {
            repeatOccurances++;
        } else if (packetType.equals("BREAK") && lastPacketType.equals("MOVE")) {
            repeatOccurances++;
        } else {
            repeatOccurances = 0;
        }
        lastPacketType = packetType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Integer getRepeatOccurances() {
        return repeatOccurances;
    }


}
