package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.Task;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CoinSystem {

    private final Main plugin;

    public CoinSystem(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * This method can be executed sync
     */
    public void addCoins(final Player player, int coins, final boolean endOfRound) {
        User user = plugin.getUserManager().get(player.getUniqueId());

        if (endOfRound) {
            if (user.has("coins")) {
                coins = coins * 2;
            }
        }

        final int fCoins = coins;

        new Task(plugin.getPlugin()) {

            @Override
            public void executeAsync() {
                boolean success = addCoinsInDatabase(player, fCoins);
                cache.add(success);
            }

            @Override
            public void executeSyncOnFinish() {
                if ((boolean) cache.get(0)) {
                    if (endOfRound) {
                        if (user.has("coins")) {
                            player.sendMessage("§7[§eCoinSystem§7] §e+" + fCoins + " §6Coins §7(x2 §eVIP-Boost§7)");
                        } else {
                            player.sendMessage("§7[§eCoinSystem§7] §e+" + fCoins + " §6Coins §7(x2 mit dem §eVIP-Rang§7)");
                        }
                    } else {
                        player.sendMessage("§7[§eCoinSystem§7] §e+" + fCoins + " §6Coins");
                    }
                }

            }
        };

    }

    /**
     * This method can be executed sync
     */
    public void removeCoins(final Player player, final int coins, final boolean notify) {
        User user = plugin.getUserManager().get(player.getUniqueId());

        new Task(plugin.getPlugin()) {
            @Override
            public void executeAsync() {
                boolean success = addCoinsInDatabase(player, -coins);
                cache.add(success);
            }

            @Override
            public void executeSyncOnFinish() {
                if ((boolean) cache.get(0)) {
                    if (notify) {
                        player.sendMessage("§7[§eCoinSystem§7] §c-" + coins + " §6Coins");
                    }
                }
            }
        };
    }

    private boolean addCoinsInDatabase(Player player, int coins) {
        User user = plugin.getUserManager().get(player.getUniqueId());

        if (user != null) {
            try {
                DatabaseConnection databaseConnection = plugin.getDatabaseManager().getConnection();
                PreparedStatement ps = databaseConnection.getPreparedStatement(
                        "UPDATE hyperior_mc.users SET coins = coins + ? WHERE uuid = ?");

                ps.setInt(1, coins);
                ps.setObject(2, player.getUniqueId());
                ps.executeUpdate();

                databaseConnection.finish();

                user.setCoins(user.getCoins() + coins);

                return true;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getConsoleSender().sendMessage("§4Error while adding coins!");

        return false;
    }

}
