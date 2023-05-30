package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

    private final Location location;
    private final List<Component> text;
    private final List<ArmorStand> entities;

    public Hologram(Location location, Component... text) {
        this.location = location;
        this.text = Arrays.asList(text);
        this.entities = new ArrayList<>();
    }

    public Hologram(Location location, String... text) {
        List<Component> newText = new ArrayList<>();

        for (String currentLore : text) {
            newText.add(LegacyComponentSerializer.legacySection().deserialize(currentLore));
        }

        this.location = location;
        this.text = newText;
        this.entities = new ArrayList<>();
    }

    public void spawnHologram() {
        Location tempLocation = location.clone();
        for (Component component : text) {
            ArmorStand armorStand = (ArmorStand) tempLocation.getWorld().spawnEntity(tempLocation, EntityType.ARMOR_STAND);

            armorStand.customName(component);
            armorStand.setCustomNameVisible(true);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            tempLocation.subtract(0, 0.3, 0);

            entities.add(armorStand);
        }
    }

    public void destroyHologram() {
        for (ArmorStand armorStand : entities) {
            armorStand.remove();
        }
    }

}
