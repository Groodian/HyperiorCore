package de.groodian.hyperiorcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

// https://www.spigotmc.org/threads/comprehensive-particle-spawning-guide-1-13-1-19.343001/

public class HParticle {

    private final Particle particle;
    private final int count;
    private final Object data;

    public HParticle(Particle particle) {
        this(particle, 1, null);
    }

    public HParticle(Particle particle, int count, Object data) {
        this.particle = particle;
        this.count = count;
        this.data = data;
    }

    public void send(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPlayer(player, location);
        }
    }

    public void sendPlayer(Player player, Location location) {
        if (player.getLocation().distance(location) < 50) {
            if (data == null) {
                player.spawnParticle(particle, location, count);
            } else {
                player.spawnParticle(particle, location, count, data);
            }
        }
    }

}
