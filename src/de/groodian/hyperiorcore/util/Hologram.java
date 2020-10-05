package de.groodian.hyperiorcore.util;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Hologram extends SpawnAble {

    private String[] text;
    private ArrayList<EntityArmorStand> entities;

    public Hologram(Location location, String... text) {
        super(location);
        this.text = text;

        entities = new ArrayList<>();

        createHologram();
    }

    @Override
    protected void handleSpawnFor(Player player) {
        for (EntityArmorStand entity : entities) {
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    protected void handleDestroyFor(Player player) {
        for (EntityArmorStand entity : entities) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void createHologram() {
        Location tempLocation = location.clone();
        for (String text : text) {
            EntityArmorStand entity = new EntityArmorStand(((CraftWorld) tempLocation.getWorld()).getHandle(), tempLocation.getX(), tempLocation.getY(), tempLocation.getZ());
            entity.setCustomName(text);
            entity.setCustomNameVisible(true);
            entity.setInvisible(true);
            entity.setGravity(false);
            tempLocation.subtract(0, 0.3, 0);
            entities.add(entity);
        }
    }

}

