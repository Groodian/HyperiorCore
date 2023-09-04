package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseTransaction;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class CoinSystem extends DatabaseTransaction {

    public static class Add extends CoinSystem {

        private final boolean endOfRound;
        private final int coins;
        private final Player player;

        public Add(boolean endOfRound, int coins, Player player) {
            this.endOfRound = endOfRound;
            this.coins = coins;
            this.player = player;
        }

        @Override
        public void runOnSuccess() {
            User user = plugin.getUserManager().get(player.getUniqueId());

            if (user == null) {
                return;
            }

            user.setCoins(user.getCoins() + coins);

            if (endOfRound) {
                if (user.has("coins")) {
                    player.sendMessage("§7[§eCoinSystem§7] §e+" + coins + " §6Coins §7(x2 §eVIP-Boost§7)");
                } else {
                    player.sendMessage("§7[§eCoinSystem§7] §e+" + coins + " §6Coins §7(x2 mit dem §eVIP-Rang§7)");
                }
            } else {
                player.sendMessage("§7[§eCoinSystem§7] §e+" + coins + " §6Coins");
            }
        }

        @Override
        public boolean run(DatabaseConnection databaseConnection) {
            return addCoinsInDatabase(databaseConnection, player, coins);
        }

    }

    public static class Remove extends CoinSystem {

        private final int coins;
        private final Player player;

        public Remove(int coins, Player player) {
            this.coins = coins;
            this.player = player;
        }

        @Override
        public void runOnSuccess() {
            User user = plugin.getUserManager().get(player.getUniqueId());

            if (user == null) {
                return;
            }

            user.setCoins(user.getCoins() - coins);

            player.sendMessage("§7[§eCoinSystem§7] §c-" + coins + " §6Coins");
        }

        @Override
        public boolean run(DatabaseConnection databaseConnection) {
            return addCoinsInDatabase(databaseConnection, player, -coins);
        }

    }

    protected boolean addCoinsInDatabase(DatabaseConnection databaseConnection, Player player, int coins) {
        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "UPDATE hyperior_mc.users SET coins = coins + ? WHERE uuid = ? AND coins + ? >= 0");

            ps.setInt(1, coins);
            ps.setObject(2, player.getUniqueId());
            ps.setInt(3, coins);

            if (ps.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage("§4Error while adding coins!");

        return false;
    }

}
