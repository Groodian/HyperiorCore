package de.groodian.hyperiorcore.level;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.HSound;
import de.groodian.hyperiorcore.util.Task;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Level {

    private Main plugin;

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

                final String uuid = player.getUniqueId().toString().replaceAll("-", "");
                final int points = getMpPoints(uuid);
                final int level = getLevel(uuid);

                if (getPoints(level + 1) <= points) {
                    new Task(plugin.getPlugin()) {
                        @Override
                        public void executeAsync() {
                            setLevel(uuid, player.getName(), level + 1);
                        }

                        @Override
                        public void executeSyncOnFinish() {
                            player.sendMessage("§7[§eLevel§7] §aDu bist nun §6Level " + getFormattedLevel(player) + "§a.");
                            new HSound(Sound.NOTE_BASS).playFor(player);
                            updateLevel(player);
                            plugin.getPrefix().setListName(player);
                        }
                    };
                }

            }

        }.runTaskLater(plugin, 10);

    }

    /**
     * This method should be executed async
     */
    public String getFormattedLevel(Player player) {
        int level = getLevel(player.getUniqueId().toString().replaceAll("-", ""));
        if (level < 10) {
            return "§f" + level;
        } else if (level < 20) {
            return "§a" + level;
        } else if (level < 30) {
            return "§2" + level;
        } else if (level < 40) {
            return "§b" + level;
        } else if (level < 50) {
            return "§3" + level;
        } else if (level < 60) {
            return "§9" + level;
        } else if (level < 70) {
            return "§e" + level;
        } else if (level < 80) {
            return "§6" + level;
        } else if (level < 90) {
            return "§c" + level;
        } else if (level < 100) {
            return "§4" + level;
        } else {
            return "§4§k:§4" + level + "§4§k:";
        }
    }

    /**
     * This method should be executed async
     */
    public int getLevel(String uuid) {
        if (hasLevel(uuid)) {
            try {
                PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT level FROM core WHERE UUID = ?");
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    return rs.getInt("level");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private int getPoints(int level) {
        return (int) ((0.33 * level * level * level) + (3.3 * level * level) + (330 * level));
    }

    private int getMpPoints(String uuid) {
        try {
            PreparedStatement ps = plugin.getMySQLManager().getMinecraftPartyMySQL().getConnection().prepareStatement("SELECT points FROM stats WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (int) rs.getLong("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setLevel(String uuid, String name, int level) {
        if (hasLevel(uuid)) {
            try {
                PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("UPDATE core SET level = ?, playername = ? WHERE UUID = ?");
                ps.setInt(1, level);
                ps.setString(2, name);
                ps.setString(3, uuid);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("INSERT INTO core (UUID, playername, level) VALUES(?,?,?)");
                ps.setString(1, uuid);
                ps.setString(2, name);
                ps.setInt(3, level);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasLevel(String uuid) {
        try {
            PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT level FROM core WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
