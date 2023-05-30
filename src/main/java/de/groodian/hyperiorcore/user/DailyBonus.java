package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.Task;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.bukkit.entity.Player;

public class DailyBonus {

    public static final int COLLECT_WAIT_MINUTES = 23 * 60;
    public static final int DAILY_BONUS_COINS = 500;
    public static final int DAILY_BONUS_VIP_COINS = 1000;

    private final Main plugin;

    public DailyBonus(Main plugin) {
        this.plugin = plugin;
    }

    public void collect(Player player, DailyBonusType dailyBonusType) {
        User user = plugin.getUserManager().get(player.getUniqueId());

        if (user != null) {
            switch (dailyBonusType) {
                case PLAYER -> {
                    // set direct in cache, to avoid spam
                    user.setDailyBonus(OffsetDateTime.now());
                    collect(player, "daily_bonus", DAILY_BONUS_COINS);
                }
                case VIP -> {
                    // set direct in cache, to avoid spam
                    user.setDailyBonusVIP(OffsetDateTime.now());
                    collect(player, "daily_bonus_vip", DAILY_BONUS_VIP_COINS);
                }
            }
        }

    }

    private void collect(Player player, String name, int coins) {
        new Task(plugin) {
            @Override
            public void executeAsync() {
                boolean success = updateInDatabase(player, name);
                cache.add(success);
            }

            @Override
            public void executeSyncOnFinish() {
                if ((boolean) cache.get(0)) {
                    plugin.getCoinSystem().addCoins(player, coins, false);
                }
            }
        };
    }

    private boolean updateInDatabase(Player player, String name) {
        try {
            DatabaseConnection databaseConnection = plugin.getDatabaseManager().getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "UPDATE hyperior_mc.users SET " + name + " = now() WHERE uuid = ? AND (" + name + " IS NULL OR (" + name + " + interval '" + COLLECT_WAIT_MINUTES + " minutes' < now()))");

            ps.setObject(1, player.getUniqueId());
            int rows = ps.executeUpdate();

            databaseConnection.finish();

            if (rows == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
