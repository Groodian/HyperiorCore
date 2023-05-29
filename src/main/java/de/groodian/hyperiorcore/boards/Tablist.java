package de.groodian.hyperiorcore.boards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Tablist {

    private final Component header;
    private final Component footer;

    public Tablist(String header, String footer) {
        this.header = LegacyComponentSerializer.legacyAmpersand().deserialize(header);
        this.footer = LegacyComponentSerializer.legacyAmpersand().deserialize(footer);
    }

    public Tablist(Component header, Component footer) {
        this.header = header;
        this.footer = footer;
    }

    public void send() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTo(player);
        }
    }

    public void sendTo(Player player) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

}
