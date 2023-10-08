package org.spss.toucananticheat;

import org.spss.toucananticheat.ChunkReader.Chunk;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

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
                event.getPlayer().sendMessage("Trigger");
                PacketContainer packet = event.getPacket();

                // //
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
                Chunk chunk = new Chunk(packet);
            }
        });
    }
}
