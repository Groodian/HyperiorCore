package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final DatabaseManager databaseManager;
    private final Map<UUID, User> cache;

    public UserManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.cache = Collections.synchronizedMap(new HashMap<>());
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

    public User loadUser(String name) {
        User user = null;

        DatabaseConnection databaseConnection = databaseManager.getConnection();

        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "SELECT uuid, name, rank, total_xp, coins, daily_bonus, daily_bonus_vip, ban, logins, first_login, last_login, last_logout, login_days, connection_time FROM hyperior_mc.users WHERE UPPER(name) = UPPER(?)");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            user = userFromResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseConnection.finish();

        return user;
    }

    public User loadUser(UUID uuid) {
        User user = null;

        DatabaseConnection databaseConnection = databaseManager.getConnection();

        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "SELECT uuid, name, rank, total_xp, coins, daily_bonus, daily_bonus_vip, ban, logins, first_login, last_login, last_logout, login_days, connection_time FROM hyperior_mc.users WHERE uuid = ?");
            ps.setObject(1, uuid);
            ResultSet rs = ps.executeQuery();

            user = userFromResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseConnection.finish();

        return user;
    }

    private User userFromResultSet(ResultSet rs) throws SQLException {
        User user = null;

        if (rs.next()) {
            int rankValue = rs.getInt("rank");
            Rank rank = Rank.DEFAULT_RANK;

            for (Rank allRank : Rank.RANKS) {
                if (allRank.value() == rankValue) {
                    rank = allRank;
                }
            }

            user = new User(
                    rs.getObject("uuid", UUID.class),
                    rs.getString("name"),
                    rank,
                    rs.getInt("total_xp"),
                    rs.getInt("coins"),
                    rs.getObject("daily_bonus", OffsetDateTime.class),
                    rs.getObject("daily_bonus_vip", OffsetDateTime.class),
                    rs.getObject("ban", UUID.class),
                    rs.getInt("logins"),
                    rs.getObject("first_login", OffsetDateTime.class),
                    rs.getObject("last_login", OffsetDateTime.class),
                    rs.getObject("last_logout", OffsetDateTime.class),
                    rs.getInt("login_days"),
                    rs.getInt("connection_time")
            );
        }

        return user;
    }

    private void createUser(UUID uuid, String name) {
        DatabaseConnection databaseConnection = databaseManager.getConnection();

        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "INSERT INTO hyperior_mc.users (uuid, name, rank, total_xp, coins, logins, first_login, last_login, login_days, connection_time) VALUES (?, ?, 0, 0, 0, 0, now(), now(), 0, 0) ON CONFLICT (uuid) DO NOTHING");
            ps.setObject(1, uuid);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseConnection.finish();
    }

}
