package de.groodian.hyperiorcore.boards;

import java.time.Duration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HTitle {

    private final Duration fadeIn;
    private final Duration stay;
    private final Duration fadeOut;
    private final Component title;
    private final Component subTitle;

    public HTitle(Duration fadeIn, Duration stay, Duration fadeOut, String title, String subTitle) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.title = LegacyComponentSerializer.legacyAmpersand().deserialize(title);
        this.subTitle = LegacyComponentSerializer.legacyAmpersand().deserialize(subTitle);
    }

    public HTitle(Duration fadeIn, Duration stay, Duration fadeOut, Component title, Component subTitle) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.title = title;
        this.subTitle = subTitle;
    }

    public void send() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTo(player);
        }
    }

    public void sendTo(Player player) {
        Title.Times times = Title.Times.times(fadeIn, stay, fadeOut);
        Title title = Title.title(this.title, this.subTitle);
        player.showTitle(title);
    }

}
