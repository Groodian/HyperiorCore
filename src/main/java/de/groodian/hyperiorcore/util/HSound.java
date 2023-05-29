package de.groodian.hyperiorcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HSound {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public HSound(Sound sound) {
        this.sound = sound;
        this.volume = 1;
        this.pitch = 1;
    }

    public HSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playFor(player);
        }
    }

    public void playFor(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}
