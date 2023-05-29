package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

    private final Location location;
    private final Component[] text;
    private final ArrayList<ArmorStand> entities;

    public Hologram(Location location, Component... text) {
        this.location = location;
        this.text = text;
        this.entities = new ArrayList<>();
    }

    public void spawnHologram() {
        Location tempLocation = location.clone();
        for (Component component : text) {
            ArmorStand armorStand = (ArmorStand) tempLocation.getWorld()
                    .spawnEntity(tempLocation, EntityType.ARMOR_STAND);

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
