package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseManager;
import de.groodian.hyperiorcore.util.UUIDFetcher;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Ranks {

    private final DatabaseManager databaseManager;
    private final UserManager userManager;
    private final UUIDFetcher uuidFetcher;

    public Ranks(DatabaseManager databaseManager, UserManager userManager) {
        this.databaseManager = databaseManager;
        this.userManager = userManager;
        this.uuidFetcher = new UUIDFetcher();
    }

    /**
     * This method should be executed async
     */
    public String setRank(String name, String rankName) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return "§cThis player does not exist.";
        }

        // make sure the user is created
        User user = userManager.getOrCreateUser(result.getUUID(), result.getName());

        Rank rank = null;
        for (Rank current : Rank.RANKS) {
            if (current.name().equalsIgnoreCase(rankName)) {
                rank = current;
            }
        }
        if (rank == null) {
            return "§cCould not find the rank!";
        }

        try {
            DatabaseConnection databaseConnection = databaseManager.getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "UPDATE hyperior_mc.users SET rank = ? WHERE uuid = ?");

            ps.setInt(1, rank.value());
            ps.setObject(2, result.getUUID());
            ps.executeUpdate();

            databaseConnection.finish();

            User cachedUser = userManager.get(result.getUUID());
            if (cachedUser != null) {
                cachedUser.setRank(rank);
            }

            return "§a" + result.getName() + "§7 has now the rank " + rank.color() + rank.name() +
                    "§7. (Previous rank: " + user.getRank().color() + user.getRank().name() + "§7";

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "§cAn error occurred";
    }

    /**
     * This method should be executed async
     */
    public String removeRank(String name) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return "§cThis player does not exist.";
        }

        User user = userManager.loadUser(result.getUUID());

        if (user != null) {

            try {
                DatabaseConnection databaseConnection = databaseManager.getConnection();
                PreparedStatement ps = databaseConnection.getPreparedStatement(
                        "UPDATE hyperior_mc.users SET rank = 0 WHERE uuid = ?");

                ps.setObject(1, result.getUUID());
                ps.executeUpdate();

                databaseConnection.finish();

                User cachedUser = userManager.get(result.getUUID());
                if (cachedUser != null) {
                    cachedUser.setRank(Rank.DEFAULT_RANK);
                }

                return "§a" + result.getName() + "§7 is no longer a " + user.getRank().color() +
                        user.getRank().name() + "§7.";

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            return "§a" + result.getName() + "§7 has no rank.";
        }

        return "§cAn error occurred";
    }

    /**
     * This method should be executed async
     */
    public String info(String name) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return "§cThis player does not exist.";
        }

        User user = userManager.loadUser(result.getUUID());

        if (user != null) {
            return "§a" + result.getName() + "§7 has the rank " + user.getRank().color() +
                    user.getRank().name() + "§7.";
        } else {
            return "§a" + result.getName() + "§7 is not in the database.";
        }
    }

    /**
     * This method should be executed async
     */
    public String list() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§7List:");

        try {
            DatabaseConnection databaseConnection = databaseManager.getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "SELECT name, rank FROM hyperior_mc.users ORDER BY rank");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int rankValue = rs.getInt("rank");
                for (Rank rank : Rank.RANKS) {
                    if (rank.value() == rankValue) {
                        stringBuilder.append("\n").append(rank.longPrefix()).append(rs.getString("name"));
                    }
                }
            }

            databaseConnection.finish();

            return stringBuilder.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "§cAn error occurred";
    }

}
