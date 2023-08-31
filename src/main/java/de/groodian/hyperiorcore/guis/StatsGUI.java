package de.groodian.hyperiorcore.guis;

import de.groodian.hyperiorcore.gui.GUI;
import de.groodian.hyperiorcore.main.HyperiorCore;
import de.groodian.hyperiorcore.user.MinecraftPartyStats;
import de.groodian.hyperiorcore.user.User;
import de.groodian.hyperiorcore.user.XP;
import de.groodian.hyperiorcore.util.HSound;
import de.groodian.hyperiorcore.util.ItemBuilder;
import de.groodian.hyperiorcore.util.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;

public class StatsGUI extends GUI {

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private final User showUser;
    private final MinecraftPartyStats.Player stats;

    public StatsGUI(User showUser, MinecraftPartyStats.Player stats) {
        super(Component.text("Stats für ").append(Component.text(showUser.getName(), NamedTextColor.GOLD)).append(Component.text(":")), 45);
        this.showUser = showUser;
        this.stats = stats;
    }

    @Override
    protected void onOpen() {
        int level = XP.getLevel(showUser.getTotalXP()); // 1
        int xpNextLevel = XP.pointsForLevel(level + 1); // 200
        int xpCurrentLevel = showUser.getTotalXP() - XP.pointsForLevel(level);

        putItem(new ItemBuilder(Material.PLAYER_HEAD)
                        .setName(Component.text(showUser.getName(), NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true))
                        .setLore(Component.empty(),
                                Component.text("Rang: ", NamedTextColor.GRAY)
                                        .append(Component.text(showUser.getRank().name(), showUser.getRank().color())),
                                Component.text("Level: ", NamedTextColor.GRAY)
                                        .append(XP.getFormattedLevel(level))
                                        .append(Component.text(" (" + xpCurrentLevel + "/" + xpNextLevel + ")", NamedTextColor.GRAY)),
                                Component.text("Online Zeit: ", NamedTextColor.GRAY)
                                        .append(Component.text(Time.durationStringNoDay(Duration.ofSeconds(showUser.getConnectionTime())),
                                                NamedTextColor.GOLD)),
                                Component.text("Gesamte XP: ", NamedTextColor.GRAY)
                                        .append(Component.text(showUser.getTotalXP(), NamedTextColor.GOLD)),
                                Component.text("An unterschiedlichen Tagen gespielt: ", NamedTextColor.GRAY)
                                        .append(Component.text(showUser.getLoginDays(), NamedTextColor.GOLD)),
                                Component.text("Spielt seit: ", NamedTextColor.GRAY)
                                        .append(Component.text(dateFormatter.format(Date.from(showUser.getFirstLogin().toInstant())),
                                                NamedTextColor.GOLD)),
                                Component.text("Coins: ", NamedTextColor.GRAY)
                                        .append(Component.text(showUser.getCoins(), NamedTextColor.GOLD))
                        )
                        .setSkullOwner(showUser.getUuid())
                        .build(),
                13);

        if (stats == null) {
            putItem(new ItemBuilder(Material.RED_MUSHROOM_BLOCK)
                            .setName(Component.text("Minecraft Party Stats", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true))
                            .setLore(Component.text("Dieser Spieler hat kein Minecraft Party gespielt.", NamedTextColor.RED))
                            .build(),
                    31);
        } else {
            putItem(new ItemBuilder(Material.RED_MUSHROOM_BLOCK)
                            .setName(Component.text("Minecraft Party Stats", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true))
                            .build(),
                    31,
                    () -> HyperiorCore.getPaper().getDefaultGUIManager().open(player, new MinecraftPartyStatsGUI(showUser, stats)));
        }

        new HSound(Sound.BLOCK_CHEST_OPEN).playFor(player);
    }

    @Override
    public void onUpdate() {
    }

}
