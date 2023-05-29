package de.groodian.hyperiorcore.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public record Rank(int value,
                   String name,
                   List<String> permissions,
                   TextColor color,
                   TextComponent longPrefix,
                   TextComponent shortPrefix) {

    public static final Rank DEFAULT_RANK = new Rank(9,
                                                     "Player",
                                                     new ArrayList<>(),
                                                     NamedTextColor.GREEN,
                                                     Component.text("", NamedTextColor.GREEN),
                                                     Component.text("", NamedTextColor.GREEN));

    public static final List<Rank> RANKS = Arrays.asList(
            new Rank(
                    1,
                    "Admin",
                    Arrays.asList("cosmetics.buy",
                                  "dailybonus.vip",
                                  "minecraftparty.start",
                                  "coins",
                                  "minecraftparty.premiumjoin",
                                  "joinfullserver",
                                  "team",
                                  "kick",
                                  "lookup",
                                  "unban",
                                  "ban",
                                  "broadcast",
                                  "motd",
                                  "lobby.setup",
                                  "lobby.build",
                                  "maintenance",
                                  "pban",
                                  "serverstarter",
                                  "minecraftparty.build",
                                  "minecraftparty.setup",
                                  "commandsblock.bypass",
                                  "ranks.all",
                                  "slots"),
                    NamedTextColor.DARK_RED,
                    prefixHelper("Admin", NamedTextColor.DARK_RED),
                    prefixHelper("Admin", NamedTextColor.DARK_RED)),

            new Rank(
                    2,
                    "Moderator",
                    Arrays.asList("cosmetics.buy",
                                  "dailybonus.vip",
                                  "minecraftparty.start",
                                  "coins",
                                  "minecraftparty.premiumjoin",
                                  "joinfullserver",
                                  "team",
                                  "kick",
                                  "lookup",
                                  "unban",
                                  "ban",
                                  "broadcast"),
                    NamedTextColor.BLUE,
                    prefixHelper("Moderator", NamedTextColor.BLUE),
                    prefixHelper("Mod", NamedTextColor.BLUE)),

            new Rank(
                    3,
                    "Builder",
                    Arrays.asList("cosmetics.buy",
                                  "dailybonus.vip",
                                  "minecraftparty.start",
                                  "coins",
                                  "minecraftparty.premiumjoin",
                                  "joinfullserver",
                                  "team"),
                    NamedTextColor.DARK_AQUA,
                    prefixHelper("Builder", NamedTextColor.DARK_AQUA),
                    prefixHelper("Builder", NamedTextColor.DARK_AQUA)),

            new Rank(
                    4,
                    "Supporter",
                    Arrays.asList("cosmetics.buy",
                                  "dailybonus.vip",
                                  "minecraftparty.start",
                                  "coins",
                                  "minecraftparty.premiumjoin",
                                  "joinfullserver",
                                  "team",
                                  "kick"),
                    NamedTextColor.DARK_GREEN,
                    prefixHelper("Supporter", NamedTextColor.DARK_GREEN),
                    prefixHelper("Sup", NamedTextColor.DARK_GREEN)),

            new Rank(
                    5,
                    "YouTuber",
                    Arrays.asList("cosmetics.buy",
                                  "dailybonus.vip",
                                  "minecraftparty.start",
                                  "coins",
                                  "minecraftparty.premiumjoin",
                                  "joinfullserver"),
                    NamedTextColor.LIGHT_PURPLE,
                    prefixHelper("YouTuber", NamedTextColor.LIGHT_PURPLE),
                    prefixHelper("YT", NamedTextColor.LIGHT_PURPLE)),

            new Rank(
                    6,
                    "VIP",
                    Arrays.asList("cosmetics.buy",
                                  "dailybonus.vip",
                                  "minecraftparty.start",
                                  "coins",
                                  "minecraftparty.premiumjoin",
                                  "joinfullserver"),
                    NamedTextColor.YELLOW,
                    prefixHelper("VIP", NamedTextColor.YELLOW),
                    prefixHelper("VIP", NamedTextColor.YELLOW))
                                                        );

    private static TextComponent prefixHelper(String name, TextColor color) {
        return Component.text(name, color).append(Component.text(" | ", NamedTextColor.GRAY)).color(color);
    }

}
