package org.spss.toucananticheat;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AntiNuke implements Listener {
    private HashMap<UUID, NukeCounter> allPlayerData = new HashMap<UUID, NukeCounter>();
    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        NukeCounter counter;
        counter = allPlayerData.get(player.getUniqueId());
        if (counter == null) {
            counter = new NukeCounter(player.getUniqueId());
            allPlayerData.put(player.getUniqueId(), counter);
        }

        counter.processPacket("BREAK");

        Block broken = event.getBlock();
        Block block = player.getTargetBlock(null, 5);
        Location l = block.getLocation();
        String outStr = String.format("Material: %s, x:%d, y:%d, z:%d, num:%d", block.getType(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), counter.getRepeatOccurances());
        //player.sendMessage(outStr);
        if (!broken.getLocation().equals(block.getLocation())) {
            event.setCancelled(true);
        }

        if (counter.getRepeatOccurances() >= 20) {
            player.kickPlayer("Dirty Cheater");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        NukeCounter counter;
        counter = allPlayerData.get(player.getUniqueId());
        if (counter == null) {
            counter = new NukeCounter(player.getUniqueId());
            allPlayerData.put(player.getUniqueId(), counter);
        }

        counter.processPacket("MOVE");
    }
}
