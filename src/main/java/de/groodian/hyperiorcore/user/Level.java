package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.HSound;
import de.groodian.hyperiorcore.util.Task;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Level {

    private final Main plugin;

    public Level(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * This method can be executed sync
     */
    public void updateLevel(final Player player) {

        new BukkitRunnable() {

            @Override
            public void run() {

                final User user = plugin.getUserManager().get(player.getUniqueId());

                if (pointsForLevel(user.getLevel() + 1) <= user.getTotalXP()) {
                    new Task(plugin.getPlugin()) {
                        @Override
                        public void executeAsync() {
                            setLevel(player.getUniqueId(), user.getLevel() + 1);
                        }

                        @Override
                        public void executeSyncOnFinish() {
                            player.sendMessage("§7[§eLevel§7] §aDu bist nun §6Level " + getFormattedLevel(user.getLevel()) + "§a.");
                            new HSound(Sound.BLOCK_NOTE_BLOCK_BASS).playFor(player);
                            updateLevel(player);
                            plugin.getPrefix().setListName(player);
                        }
                    };
                }

            }

        }.runTaskLater(plugin, 10);

    }

    public static TextComponent getFormattedLevel(int level) {
        if (level < 10) {
            return Component.text(level, NamedTextColor.WHITE);
        } else if (level < 20) {
            return Component.text(level, NamedTextColor.GREEN);
        } else if (level < 30) {
            return Component.text(level, NamedTextColor.DARK_GREEN);
        } else if (level < 40) {
            return Component.text(level, NamedTextColor.AQUA);
        } else if (level < 50) {
            return Component.text(level, NamedTextColor.DARK_AQUA);
        } else if (level < 60) {
            return Component.text(level, NamedTextColor.BLUE);
        } else if (level < 70) {
            return Component.text(level, NamedTextColor.YELLOW);
        } else if (level < 80) {
            return Component.text(level, NamedTextColor.GOLD);
        } else if (level < 90) {
            return Component.text(level, NamedTextColor.RED);
        } else if (level < 100) {
            return Component.text(level, NamedTextColor.DARK_RED);
        } else {
            return Component.text()
                    .append(Component.text(":", NamedTextColor.DARK_RED).decoration(TextDecoration.OBFUSCATED, true))
                    .append(Component.text(level, NamedTextColor.DARK_RED))
                    .append(Component.text(":", NamedTextColor.DARK_RED).decoration(TextDecoration.OBFUSCATED, true))
                    .build();
        }
    }

    private int pointsForLevel(int level) {
        return (int) ((0.33 * level * level * level) + (3.3 * level * level) + (330 * level));
    }

    private void setLevel(UUID uuid, int level) {
        User user = plugin.getUserManager().get(uuid);

        if (user != null) {
            try {
                DatabaseConnection databaseConnection = plugin.getDatabaseManager().getConnection();
                PreparedStatement ps = databaseConnection.getPreparedStatement(
                        "UPDATE hyperior_mc.users SET level = ? WHERE uuid = ?");

                ps.setInt(1, level);
                ps.setObject(2, uuid);
                ps.executeUpdate();

                databaseConnection.finish();

                user.setLevel(level);

                return;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getConsoleSender().sendMessage("§4Error while setting the level!");
    }

    private void addTotalXP(UUID uuid, int xp) {
        User user = plugin.getUserManager().get(uuid);

        if (user != null) {
            try {
                DatabaseConnection databaseConnection = plugin.getDatabaseManager().getConnection();
                PreparedStatement ps = databaseConnection.getPreparedStatement(
                        "UPDATE hyperior_mc.users SET total_xp = total_xp + ? WHERE uuid = ?");

                ps.setInt(1, xp);
                ps.setObject(2, uuid);
                ps.executeUpdate();

                databaseConnection.finish();

                user.setTotalXP(user.getTotalXP() + xp);

                return;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getConsoleSender().sendMessage("§4Error while adding xp!");
    }

}
