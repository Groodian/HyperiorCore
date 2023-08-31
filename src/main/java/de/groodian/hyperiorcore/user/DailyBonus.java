package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseTransaction;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.bukkit.entity.Player;

public class DailyBonus extends DatabaseTransaction {

    public static final int COLLECT_WAIT_MINUTES = 23 * 60;
    public static final int DAILY_BONUS_COINS = 500;
    public static final int DAILY_BONUS_VIP_COINS = 1000;

    private final Player player;
    private final DailyBonusType type;

    public DailyBonus(Player player, DailyBonusType type) {
        this.player = player;
        this.type = type;
    }

    @Override
    public void runOnSuccess() {
    }

    @Override
    public boolean run(DatabaseConnection databaseConnection) {
        User user = plugin.getUserManager().get(player.getUniqueId());

        if (user == null) {
            return false;
        }

        String name = null;

        switch (type) {
            case PLAYER -> {
                name = "daily_bonus";
                // set direct in cache, to avoid spam
                user.setDailyBonus(OffsetDateTime.now());
            }
            case VIP -> {
                name = "daily_bonus_vip";
                // set direct in cache, to avoid spam
                user.setDailyBonusVIP(OffsetDateTime.now());
            }
        }

        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "UPDATE hyperior_mc.users SET " + name + " = now() WHERE uuid = ? AND (" + name + " IS NULL OR (" + name +
                    " + interval '" + COLLECT_WAIT_MINUTES + " minutes' < now()))");

            ps.setObject(1, player.getUniqueId());

            if (ps.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
