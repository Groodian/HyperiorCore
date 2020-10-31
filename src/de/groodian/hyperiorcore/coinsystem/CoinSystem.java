package de.groodian.hyperiorcore.coinsystem;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.Task;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoinSystem {

    private Main plugin;
    private Map<UUID, Integer> cache;

    public CoinSystem(Main plugin) {
        this.plugin = plugin;
        cache = new HashMap<>();
    }

    /**
     * This method can be executed sync
     */
    public void addCoins(final Player player, int coins, final boolean endOfRound) {
        if (endOfRound) {
            if (plugin.getRanks().has(player.getUniqueId(), "coins")) {
                coins = coins * 2;
            }
        }

        editCache(player.getUniqueId(), coins);

        final int fCoins = coins;
        new Task(plugin.getPlugin()) {

            @Override
            public void executeAsync() {
                changeCoins(player, fCoins);
            }

            @Override
            public void executeSyncOnFinish() {
                if (endOfRound) {
                    if (plugin.getRanks().has(player.getUniqueId(), "coins")) {
                        player.sendMessage("§7[§eCoinSystem§7] §e+" + fCoins + " §6Coins §7(x2 §eVIP-Boost§7)");
                    } else {
                        player.sendMessage("§7[§eCoinSystem§7] §e+" + fCoins + " §6Coins §7(x2 mit dem §eVIP-Rang§7)");
                    }
                } else {
                    player.sendMessage("§7[§eCoinSystem§7] §e+" + fCoins + " §6Coins");
                }
            }
        };

    }

    /**
     * This method can be executed sync
     */
    public void removeCoins(final Player player, final int coins, final boolean notify) {
        editCache(player.getUniqueId(), -coins);

        new Task(plugin.getPlugin()) {
            @Override
            public void executeAsync() {
                changeCoins(player, -coins);
            }

            @Override
            public void executeSyncOnFinish() {
                if (notify) {
                    player.sendMessage("§7[§eCoinSystem§7] §c-" + coins + " §6Coins");
                }
            }
        };
    }

    /**
     * This method can be executed sync
     */
    public int getCoins(Player player) {
        return getCoins(player.getUniqueId());
    }

    /**
     * This method can be executed sync
     */
    public int getCoins(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        return -1;
    }

    /**
     * This method should be executed async
     */
    public void login(UUID uuid) {
        cache.put(uuid, getCoinsFromDatabase(uuid));
    }

    /**
     * This method can be executed sync
     */
    public void logout(UUID uuid) {
        cache.remove(uuid);
    }

    private void editCache(UUID uuid, int coins) {
        // edit cache instant to provide that player can buy without having enough coins because of the async database delay
        if (cache.containsKey(uuid)) {
            cache.put(uuid, cache.get(uuid) + coins);
        } else {
            cache.put(uuid, coins);
        }
    }

    private int getCoinsFromDatabase(UUID uuid) {
        String stringUUID = uuid.toString().replaceAll("-", "");
        if (hasCoins(uuid)) {
            try {
                PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT coins FROM core WHERE UUID = ?");
                ps.setString(1, stringUUID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    return rs.getInt("coins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void changeCoins(Player player, int coins) {
        UUID uuid = player.getUniqueId();
        String stringUUID = player.getUniqueId().toString().replaceAll("-", "");
        String name = player.getName();

        try {
            if (hasCoins(uuid)) {
                PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("UPDATE core SET coins = ?, playername = ? WHERE UUID = ?");
                ps.setInt(1, getCoinsFromDatabase(uuid) + coins);
                ps.setString(2, name);
                ps.setString(3, stringUUID);
                ps.executeUpdate();
            } else {
                PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("INSERT INTO core (UUID, playername, coins) VALUES(?,?,?)");
                ps.setString(1, stringUUID);
                ps.setString(2, name);
                ps.setInt(3, coins);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasCoins(UUID uuid) {
        String stringUUID = uuid.toString().replaceAll("-", "");
        try {
            PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT coins FROM core WHERE UUID = ?");
            ps.setString(1, stringUUID);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
