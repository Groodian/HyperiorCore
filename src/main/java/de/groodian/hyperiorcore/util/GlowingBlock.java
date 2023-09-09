package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Main;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GlowingBlock {

    private final Main plugin;
    private final List<Data> glowingBlocks;

    public GlowingBlock(Main plugin) {
        this.plugin = plugin;
        this.glowingBlocks = new ArrayList<>();
    }

    public void send(Player player, Location location, long lifetime) {
        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
        Shulker shulker = new Shulker(EntityType.SHULKER, world);
        shulker.setPos(location.getX(), location.getY(), location.getZ());
        shulker.setRot(0, 0);
        shulker.setGlowingTag(true);
        shulker.setInvisible(true);
        shulker.setInvulnerable(true);

        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundAddEntityPacket(shulker));
        List<SynchedEntityData.DataValue<?>> data = shulker.getEntityData().getNonDefaultValues();
        if (data != null) {
            connection.send(new ClientboundSetEntityDataPacket(shulker.getId(), data));
        }

        glowingBlocks.add(new Data(shulker.getId(), player, location));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            remove(location);
        }, lifetime);
    }

    public void remove(Location location) {
        for (Data data : glowingBlocks) {
            if (data.location.getBlockX() == location.getBlockX() &&
                data.location.getBlockY() == location.getBlockY() &&
                data.location.getBlockZ() == location.getBlockZ()) {
                ServerGamePacketListenerImpl connection = ((CraftPlayer) data.player).getHandle().connection;
                connection.send(new ClientboundRemoveEntitiesPacket(data.entityId));
                break;
            }
        }
    }

    private static class Data {

        private final int entityId;
        private final Player player;
        private final Location location;

        private Data(int entityId, Player player, Location location) {
            this.entityId = entityId;
            this.player = player;
            this.location = location;
        }

        public int getEntityId() {
            return entityId;
        }

        public Player getPlayer() {
            return player;
        }

        public Location getLocation() {
            return location;
        }

    }

}
