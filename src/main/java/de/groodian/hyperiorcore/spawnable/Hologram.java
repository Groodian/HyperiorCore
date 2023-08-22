package de.groodian.hyperiorcore.spawnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Hologram extends SpawnAble {

    private final List<Component> text;
    private final List<ArmorStand> entities;

    public Hologram(Location location, Component... text) {
        super(location);

        this.text = Arrays.asList(text);
        this.entities = new ArrayList<>();

        createHologram();
    }

    public Hologram(Location location, String... text) {
        super(location);

        List<Component> newText = new ArrayList<>();

        for (String currentLore : text) {
            newText.add(LegacyComponentSerializer.legacySection().deserialize(currentLore));
        }

        this.text = newText;
        this.entities = new ArrayList<>();

        createHologram();
    }

    private void createHologram() {
        ServerLevel serverLevel = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(location.getWorld().getName()))).getHandle();

        Location tempLocation = location.clone();
        for (Component component : text) {
            ArmorStand armorStand = new ArmorStand(serverLevel, tempLocation.getX(), tempLocation.getY(), tempLocation.getZ());
            org.bukkit.entity.ArmorStand armorStandBukkitEntity = (org.bukkit.entity.ArmorStand) armorStand.getBukkitEntity();

            armorStandBukkitEntity.customName(component);
            armorStandBukkitEntity.setCustomNameVisible(true);
            armorStandBukkitEntity.setInvisible(true);
            armorStandBukkitEntity.setGravity(false);

            tempLocation.subtract(0, 0.3, 0);

            entities.add(armorStand);
        }
    }

    @Override
    protected void handleSpawnFor(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        for (ArmorStand armorStand : entities) {
            connection.send(new ClientboundAddEntityPacket(armorStand));
            List<SynchedEntityData.DataValue<?>> data = armorStand.getEntityData().getNonDefaultValues();
            if (data != null) {
                connection.send(new ClientboundSetEntityDataPacket(armorStand.getId(), data));
            }
        }
    }

    @Override
    protected void handleDestroyFor(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        for (ArmorStand armorStand : entities) {
            connection.send(new ClientboundRemoveEntitiesPacket(armorStand.getId()));
        }
    }

}
