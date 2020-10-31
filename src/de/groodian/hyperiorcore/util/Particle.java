package de.groodian.hyperiorcore.util;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Particle {

    private EnumParticle particleType;
    private boolean longDistance;
    private int red;
    private int green;
    private int blue;
    private float brightness;
    private int amount;
    private int data;

    public Particle(EnumParticle enumParticle) {
        this(enumParticle, true, 0, 0, 0, 0, 0, 0);
    }

    public Particle(EnumParticle particleType, boolean longDistance, int red, int green, int blue, float brightness, int amount, int data) {
        this.particleType = particleType;
        this.longDistance = longDistance;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.brightness = brightness;
        this.amount = amount;
        this.data = data;
    }

    public void send(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPlayer(player, location);
        }
    }

    public void sendPlayer(Player player, Location location) {
        if (player.getLocation().distance(location) < 50) {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particleType, longDistance, (float) location.getX(), (float) location.getY(), (float) location.getZ(), red / 255.0f, green / 255.0f, blue / 255.0f, brightness, amount, data);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
