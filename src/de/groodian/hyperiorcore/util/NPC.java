package de.groodian.hyperiorcore.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPC {

    private Plugin plugin;
    private int entityID;
    private Location location;
    private GameProfile gameProfile;
    private String name;
    private List<Packet<?>> packets = new ArrayList<>();
    private Map<Player, Long> isSetFor = new HashMap<>();
    private Packet<?> tablistRemovePacket;

    public NPC(String name, Location location, Plugin plugin) {
        this.location = location;
        this.plugin = plugin;

        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
        gameProfile = new GameProfile(UUID.randomUUID(), name);

        create();
    }

    public void spawnFor(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Packet<?> packet : packets) {
            connection.sendPacket(packet);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> connection.sendPacket(tablistRemovePacket), 40);
        isSetFor.put(player, System.currentTimeMillis());
    }

    public void destroyFor(Player player) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityID);
        // removeFromTablist();
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        isSetFor.remove(player);
    }

    public void updateFor(Player player) {
        if (isSetFor.containsKey(player)) {
            if (player.getLocation().distance(location) > 70.0) {
                destroyFor(player);
                // System.out.println(entityID + " destroy for " + player.getName());
            } else {
                if ((System.currentTimeMillis() - isSetFor.get(player)) > 120000) {
                    destroyFor(player);
                    spawnFor(player);
                    // System.out.println(entityID + " respawn for " + player.getName());
                }
            }
        } else {
            if (player.getLocation().distance(location) <= 70.0) {
                spawnFor(player);
                // System.out.println(entityID + " spawn for " + player.getName());
            }
        }
    }

    public void teleport(Location location) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setValue(packet, "a", entityID);
        setValue(packet, "b", getFixLocation(location.getX()));
        setValue(packet, "c", getFixLocation(location.getY()));
        setValue(packet, "d", getFixLocation(location.getZ()));
        setValue(packet, "e", getFixRotation(location.getYaw()));
        setValue(packet, "f", getFixRotation(location.getPitch()));
        packets.add(packet);
        headRotation(location.getYaw(), location.getPitch());
        this.location = location;
    }

    public void changeSkin(String value, String signature) {
        // https://api.mojang.com/users/profiles/minecraft/groodian
        // https://sessionserver.mojang.com/session/minecraft/profile/90ed7af46e8c4d54824de74c2519c655?unsigned=false
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
    }

    public void animation(int animation) {
        // https://wiki.vg/Protocol#Animation
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) animation);
        packets.add(packet);
    }

    public void status(int status) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) status);
        packets.add(packet);
    }

    public void equip(int slot, ItemStack itemStack) {
        // https://wiki.vg/Protocol#Entity_Equipment
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        setValue(packet, "a", entityID);
        setValue(packet, "b", slot);
        setValue(packet, "c", itemStack);
        packets.add(packet);
    }

    public void sleep(boolean state) {
        if (state) {
            Location bedLocation = new Location(location.getWorld(), 1, 1, 1);
            PacketPlayOutBed packet = new PacketPlayOutBed();
            setValue(packet, "a", entityID);
            setValue(packet, "b", new BlockPosition(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ()));
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte) 0);
            }
            packets.add(packet);
            teleport(location.clone().add(0, 0.1, 0));
        } else {
            animation(2);
            teleport(location.clone().subtract(0, 0.1, 0));
        }
    }

    private void create() {
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        setValue(packet, "a", entityID);
        setValue(packet, "b", gameProfile.getId());
        setValue(packet, "c", getFixLocation(location.getX()));
        setValue(packet, "d", getFixLocation(location.getY()));
        setValue(packet, "e", getFixLocation(location.getZ()));
        setValue(packet, "f", getFixRotation(location.getYaw()));
        setValue(packet, "g", getFixRotation(location.getPitch()));
        // setValue(packet, "h", ); // item in hand

        DataWatcher w = new DataWatcher(null);
        // https://wiki.vg/Entity_metadata#Entity_Metadata_Format
        // w.a(0, (byte) 0x20);
        w.a(6, (float) 20);
        w.a(7, 2);
        w.a(10, (byte) 127);
        setValue(packet, "i", w);
        addToTablist();
        packets.add(packet);
        removeFromTablist();
        teleport(location);
    }

    private void headRotation(float yaw, float pitch) {
        PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));
        packets.add(packet);
        packets.add(packetHead);
    }

    private void addToTablist() {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(name)[0]);
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        players.add(data);
        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        setValue(packet, "b", players);
        packets.add(packet);
    }

    private void removeFromTablist() {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, -1, null, null);
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) this.getValue(packet, "b");
        players.add(data);
        this.setValue(packet, "b", players);
        tablistRemovePacket = packet;
    }

    public void setValue(Object obj, String name, Object value) {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object getValue(Object obj, String name) {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private int getFixLocation(double pos) {
        return (int) MathHelper.floor(pos * 32.0D);
    }

    private byte getFixRotation(float x) {
        return (byte) ((int) (x * 256.0F / 360.0F));
    }

    public boolean isSetFor(Player player) {
        return isSetFor.containsKey(player);
    }

    public int getEntityID() {
        return entityID;
    }

    public Location getLocation() {
        return location;
    }

}

