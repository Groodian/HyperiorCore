package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final DatabaseManager databaseManager;
    private final Map<UUID, User> cache;

    public UserManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.cache = new HashMap<>();
    }

    public User get(UUID uuid) {
        return cache.get(uuid);
    }

    public void login(UUID uuid, String name) {
        cache.put(uuid, getOrCreateUser(uuid, name));
    }

    public void logout(UUID uuid) {
        cache.remove(uuid);
    }

    public User getOrCreateUser(UUID uuid, String name) {
        User user = loadUser(uuid);

        if (user == null) {
            createUser(uuid, name);
            user = loadUser(uuid);
        }

        return user;
    }

    public User loadUser(UUID uuid) {
        User user = null;

        try {
            DatabaseConnection databaseConnection = databaseManager.getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "SELECT name, rank, level, total_xp, coins, daily_bonus, daily_bonus_vip FROM hyperior_mc.users WHERE uuid = ?");
            ps.setObject(1, uuid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int rankValue = rs.getInt("rank");
                Rank rank = Rank.DEFAULT_RANK;

                for (Rank allRank : Rank.RANKS) {
                    if (allRank.value() == rankValue) {
                        rank = allRank;
                    }
                }

                user = new User(uuid,
                                rs.getString("name"),
                                rank,
                                rs.getInt("level"),
                                rs.getInt("total_xp"),
                                rs.getInt("coins"),
                                rs.getObject("daily_bonus", OffsetDateTime.class),
                                rs.getObject("daily_bonus_vip", OffsetDateTime.class));
            }

            databaseConnection.finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void createUser(UUID uuid, String name) {
        try {
            DatabaseConnection databaseConnection = databaseManager.getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "INSERT INTO hyperior_mc.users (uuid, name, rank, level, total_xp, coins) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (uuid) DO NOTHING");
            ps.setObject(1, uuid);
            ps.setString(2, name);
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setInt(6, 0);
            ps.executeUpdate();
            databaseConnection.finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
