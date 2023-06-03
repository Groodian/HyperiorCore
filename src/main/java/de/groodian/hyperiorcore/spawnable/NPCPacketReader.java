package de.groodian.hyperiorcore.spawnable;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.PacketReader;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCPacketReader extends PacketReader {

    private final Main plugin;

    public NPCPacketReader(Main plugin, Player player) {
        super("NPC", player);
        this.plugin = plugin;
    }

    @Override
    protected void readPacket(Packet<?> packet) {
        if (packet instanceof ServerboundInteractPacket serverboundInteractPacket) {
            if (serverboundInteractPacket.getActionType() == ServerboundInteractPacket.ActionType.INTERACT) {
                for (SpawnAble spawnAble : plugin.getSpawnAbleManager().getSpawnAbleList()) {
                    if (spawnAble instanceof NPC npc) {
                        if (npc.getEntityId() == serverboundInteractPacket.getEntityId()) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    npc.onInteract(player);
                                }
                            }.runTask(plugin);
                        }
                    }
                }
            }
        }
    }

}
