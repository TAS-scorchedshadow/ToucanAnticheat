package org.spss.toucananticheat;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spss.toucananticheat.ChunkReader.Chunk;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.ChunkCoordIntPair;

public final class OutgoingTest {

    public static void register() {

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(
                new PacketAdapter(TouCanAntiCheat.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        PacketContainer packet = event.getPacket();
                        String message = packet.getStrings().read(0);

                        if (message.contains("shit") || message.contains("damn")) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("Bad manners!");
                        }
                    }
                });

        manager.addPacketListener(new PacketAdapter(TouCanAntiCheat.getInstance(), PacketType.Play.Server.MAP_CHUNK) {

            @Override
            public void onPacketSending(PacketEvent event) {
                // Handle x-ray checking
                PacketContainer packet = event.getPacket();

                Chunk chunk = new Chunk(packet);

                //
                // https://www.spigotmc.org/threads/modify-chunk-before-being-sent-to-player.582295/
                // byte[] buffer = packet.getByteArrays().read(0);

                // System.out.println("--------------");
                // System.out.println(buffer);
                // int count = 0;
                // for (byte b : buffer) {
                // System.out.print(String.format("0x%02X", b));
                // count++;
                // }
                // System.out.println("------" + count + "-----");
            }
        });

        manager.addPacketListener(new PacketAdapter(TouCanAntiCheat.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            // Resend the current chunk packet
                            Player p = event.getPlayer();
                            CraftChunk chunk = (CraftChunk) Bukkit.getWorlds().get(0).getChunkAt(p.getLocation());
                            EntityPlayer ep = ((CraftPlayer) p).getHandle();
                            ep.b.sendPacket(new PacketPlayOutMapChunk(chunk.getHandle()));
                        }
                    },
                    1
                );
            }
        });
    }
}
