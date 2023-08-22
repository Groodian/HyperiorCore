package de.groodian.hyperiorcore.spawnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPC extends SpawnAble {

    private final GameProfile gameProfile;
    private final ServerPlayer serverPlayer;
    private final List<Packet<?>> createPackets;
    private final Map<String, List<Packet<?>>> packets;

    private NPCInteract npcInteract;

    public NPC(Location location, String name) {
        super(location);
        this.packets = new HashMap<>();

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(location.getWorld().getName()))).getHandle();
        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.serverPlayer = new ServerPlayer(minecraftServer, serverLevel, gameProfile);

        serverPlayer.setPos(location.getX(), location.getY(), location.getZ());
        serverPlayer.setRot(location.getYaw(), location.getPitch());

        this.createPackets = List.of(
                new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer),
                new ClientboundAddPlayerPacket(serverPlayer));

        setRotation(location);
    }

    public void setRotation(Location location) {
        packets.put("rotation", List.of(new ClientboundRotateHeadPacket(serverPlayer, getFixRotation(location.getYaw())),
                new ClientboundMoveEntityPacket.Rot(getEntityId(), getFixRotation(location.getYaw()), getFixRotation(location.getPitch()),
                        false)));
    }

    public void setSkin(String value, String signature) {
        // https://api.mojang.com/users/profiles/minecraft/groodian
        // https://sessionserver.mojang.com/session/minecraft/profile/90ed7af46e8c4d54824de74c2519c655?unsigned=false
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
    }

    public void setItemMainHand(ItemStack itemStack) {
        packets.put("item", List.of(new ClientboundSetEquipmentPacket(getEntityId(),
                List.of(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack))))));
    }

    public void setInteract(NPCInteract npcInteract) {
        this.npcInteract = npcInteract;
    }

    public void onInteract(Player player) {
        if (npcInteract != null) {
            npcInteract.onInteract(player);
        }
    }

    @Override
    protected void handleSpawnFor(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;

        for (Packet<?> packet : createPackets) {
            connection.send(packet);
        }

        for (Map.Entry<String, List<Packet<?>>> entry : packets.entrySet()) {
            for (Packet<?> packet : entry.getValue()) {
                connection.send(packet);
            }
        }
    }

    @Override
    protected void handleDestroyFor(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(getEntityId()));
    }

    private byte getFixRotation(float x) {
        return (byte) ((x % 360) * 256 / 360);
    }

    public int getEntityId() {
        return serverPlayer.getBukkitEntity().getEntityId();
    }

}
