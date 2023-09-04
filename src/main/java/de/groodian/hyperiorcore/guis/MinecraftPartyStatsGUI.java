package de.groodian.hyperiorcore.guis;

import de.groodian.hyperiorcore.gui.GUI;
import de.groodian.hyperiorcore.user.MinecraftPartyStats;
import de.groodian.hyperiorcore.user.User;
import de.groodian.hyperiorcore.util.HSound;
import de.groodian.hyperiorcore.util.ItemBuilder;
import de.groodian.hyperiorcore.util.Time;
import java.time.Duration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class MinecraftPartyStatsGUI extends GUI {

    private final User showUser;
    private final MinecraftPartyStats.Player stats;

    public MinecraftPartyStatsGUI(User showUser, MinecraftPartyStats.Player stats) {
        super(Component.text("Stats für ")
                .append(Component.text(showUser.getName(), NamedTextColor.GOLD))
                .append(Component.text(":")), 45);
        this.showUser = showUser;
        this.stats = stats;
    }

    @Override
    protected void onOpen() {
        ItemStack white = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName(Component.empty()).build();
        ItemStack gray = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName(Component.empty()).build();

        putItemsDelayed(white, new int[]{0, 8, 36, 44}, 5);
        putItemsDelayed(gray, new int[]{1, 7, 37, 43}, 10);
        putItemsDelayed(gray, new int[]{2, 6, 38, 42, 9, 17, 27, 35}, 15);
        putItemDelayed(new ItemBuilder(Material.WRITABLE_BOOK).setName(nameComponent("Global")).setLore(
                Component.empty(),
                loreComponent("Platz", stats.rank()),
                loreComponent("Punkte", stats.points()),
                loreComponent("Spielzeit", Time.durationStringNoDay(Duration.ofSeconds(stats.playtime()))),
                loreComponent("Gespielte Spiele", stats.gamesPlayed()),
                loreComponent("Beendete Spiele", stats.gamesEnded()),
                Component.empty(),
                loreComponent("Erster", stats.gamesFirst()),
                loreComponent("Zweiter", stats.gamesSecond()),
                loreComponent("Dritter", stats.gamesThird()),
                loreComponent("Vierter", stats.gamesFourth()),
                loreComponent("Fünfter", stats.gamesFifth())
        ).build(), 12, 20);
        putItemDelayed(new ItemBuilder(Material.BOOK).setName(nameComponent("Minispiele")).setLore(
                Component.empty(),
                loreComponent("Gespielte Minispiele", stats.miniGamesPlayed()),
                Component.empty(),
                loreComponent("Erster", stats.miniGamesFirst()),
                loreComponent("Zweiter", stats.miniGamesSecond()),
                loreComponent("Dritter", stats.miniGamesThird()),
                loreComponent("Vierter", stats.miniGamesFourth()),
                loreComponent("Fünfter", stats.miniGamesFifth())
        ).build(), 14, 20);
        putItemDelayed(new ItemBuilder(Material.WHITE_WOOL).setName(nameComponent("WoolBlock")).setLore(
                loreComponentRecord("Runden", null, "WoolBlock", false)
        ).build(), 28, 20);
        putItemDelayed(new ItemBuilder(Material.LIME_WOOL).setName(nameComponent("AmpelRennen")).setLore(
                loreComponentRecord("Zeit", null, "TrafficLightRace", true)
        ).build(), 29, 20);
        putItemDelayed(new ItemBuilder(Material.WOODEN_AXE).setName(nameComponent("GunGame")).setLore(
                loreComponentRecord("Zeit", null, "GunGame", true)
        ).build(), 30, 20);
        putItemDelayed(new ItemBuilder(Material.NETHERRACK).setName(nameComponent("HeißerBoden")).setLore(
                loreComponentRecord("Zeit", null, "HotGround", true)
        ).build(), 32, 20);
        putItemDelayed(new ItemBuilder(Material.BOW).setName(nameComponent("Farbschlacht")).setLore(
                loreComponentRecord("Blöcke", null, "ColorBattle", false)
        ).build(), 33, 20);
        putItemDelayed(new ItemBuilder(Material.IRON_BARS).setName(nameComponent("Ausbruch")).setLore(
                loreComponentRecord("Zeit", null, "Breakout", true)
        ).build(), 34, 20);
        putItemDelayed(new ItemBuilder(Material.BRICK).setName(nameComponent("MasterBuilders")).setLore(
                loreComponentRecord("Punkte", null, "MasterBuilders", false)
        ).build(), 39, 20);
        putItemDelayed(new ItemBuilder(Material.STICK).setName(nameComponent("KingOfTheHill")).setLore(
                loreComponentRecord("Punkte", null, "KingOfTheHill", false)
        ).build(), 40, 20);
        putItemDelayed(new ItemBuilder(Material.FEATHER).setName(nameComponent("JumpAndRun")).setLore(
                loreComponentRecord("Zeit", "Classic", "JumpAndRun", true),
                loreComponentRecord("Zeit", "Halloween", "JumpAndRun", true),
                loreComponentRecord("Zeit", "Winter", "JumpAndRun", true),
                loreComponentRecord("Zeit", "House", "JumpAndRun", true)
        ).build(), 41, 20);

        new HSound(Sound.BLOCK_CHEST_OPEN).playFor(player);
    }

    private Component nameComponent(String name) {
        return Component.text(name, NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true);
    }

    private Component loreComponentRecord(String name, String map, String recordName, boolean time) {
        TextComponent.Builder builder = Component.text();

        builder.append(Component.text("» ", NamedTextColor.DARK_GRAY));
        builder.append(Component.text(name, NamedTextColor.GRAY));
        if (map != null) {
            builder.append(Component.text(" (", NamedTextColor.GRAY));
            builder.append(Component.text(map, NamedTextColor.YELLOW));
            builder.append(Component.text(")", NamedTextColor.GRAY));
        }
        builder.append(Component.text(": ", NamedTextColor.GRAY));

        for (MinecraftPartyStats.Record record : stats.records()) {
            if (record.name().equals(recordName + (map == null ? "" : map))) {
                if (time) {
                    builder.append(Component.text(convertTime(record.record()), NamedTextColor.GREEN));
                } else {
                    builder.append(Component.text(record.record(), NamedTextColor.GREEN));
                }
                builder.append(Component.text(" (" + Time.formatDate(record.achievedAt()) + ")", NamedTextColor.GRAY));
                return builder.build();
            }
        }

        builder.append(Component.text("-", NamedTextColor.GREEN));
        return builder.build();
    }

    private Component loreComponent(String name, int value) {
        return loreComponent(name, String.valueOf(value));
    }

    private Component loreComponent(String name, String value) {
        return Component.text("» ", NamedTextColor.DARK_GRAY)
                .append(Component.text(name + ": ", NamedTextColor.GRAY))
                .append(Component.text(value, NamedTextColor.GREEN));
    }

    private String convertTime(int ms) {
        if (ms % 1000 < 100) {
            if (ms % 100 < 10) {
                return ms / 1000 + ",00" + (ms % 1000) + "s";
            }
            return ms / 1000 + ",0" + (ms % 1000) + "s";
        }
        return ms / 1000 + "," + (ms % 1000) + "s";
    }

    @Override
    public void onUpdate() {
    }

}
