package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseManager;
import de.groodian.hyperiorcore.util.UUIDFetcher;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

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
    public Component setRank(String name, String rankName) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return Component.text("This player does not exist.", NamedTextColor.RED);
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
            return Component.text("Could not find the rank!", NamedTextColor.RED);
        }

        try {
            DatabaseConnection databaseConnection = databaseManager.getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement("UPDATE hyperior_mc.users SET rank = ? WHERE uuid = ?");

            ps.setInt(1, rank.value());
            ps.setObject(2, result.getUUID());
            ps.executeUpdate();

            databaseConnection.finish();

            User cachedUser = userManager.get(result.getUUID());
            if (cachedUser != null) {
                cachedUser.setRank(rank);
            }

            return Component.text(result.getName(), NamedTextColor.GREEN)
                    .append(Component.text(" has now the rank ", NamedTextColor.GRAY))
                    .append(Component.text(rank.name(), rank.color()))
                    .append(Component.text(". (Previous rank: ", NamedTextColor.GRAY))
                    .append(Component.text(user.getRank().name(), user.getRank().color()))
                    .append(Component.text(")", NamedTextColor.GRAY));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Component.text("An error occurred!", NamedTextColor.DARK_RED);
    }

    /**
     * This method should be executed async
     */
    public Component removeRank(String name) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return Component.text("This player does not exist.", NamedTextColor.RED);
        }

        User user = userManager.loadUser(result.getUUID());

        if (user != null) {

            try {
                DatabaseConnection databaseConnection = databaseManager.getConnection();
                PreparedStatement ps = databaseConnection.getPreparedStatement("UPDATE hyperior_mc.users SET rank = 0 WHERE uuid = ?");

                ps.setObject(1, result.getUUID());
                ps.executeUpdate();

                databaseConnection.finish();

                User cachedUser = userManager.get(result.getUUID());
                if (cachedUser != null) {
                    cachedUser.setRank(Rank.DEFAULT_RANK);
                }

                return Component.text(result.getName(), NamedTextColor.GREEN)
                        .append(Component.text(" is no longer a ", NamedTextColor.GRAY))
                        .append(Component.text(user.getRank().name(), user.getRank().color()))
                        .append(Component.text(".", NamedTextColor.GRAY));

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            return Component.text(result.getName(), NamedTextColor.GREEN).append(Component.text(" has no rank.", NamedTextColor.GRAY));
        }

        return Component.text("An error occurred!", NamedTextColor.DARK_RED);
    }

    /**
     * This method should be executed async
     */
    public Component info(String name) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return Component.text("This player does not exist.", NamedTextColor.RED);
        }

        User user = userManager.loadUser(result.getUUID());

        if (user != null) {
            return Component.text(result.getName(), NamedTextColor.GREEN)
                    .append(Component.text(" has the rank ", NamedTextColor.GRAY))
                    .append(Component.text(user.getRank().name(), user.getRank().color()))
                    .append(Component.text(".", NamedTextColor.GRAY));
        } else {
            return Component.text(result.getName(), NamedTextColor.GREEN)
                    .append(Component.text(" is not in the database.", NamedTextColor.GRAY));
        }
    }

    /**
     * This method should be executed async
     */
    public Component list() {
        TextComponent.Builder builder = Component.text().append(Component.text("List:", NamedTextColor.GRAY));

        try {
            DatabaseConnection databaseConnection = databaseManager.getConnection();
            PreparedStatement ps = databaseConnection.getPreparedStatement("SELECT name, rank FROM hyperior_mc.users ORDER BY rank");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int rankValue = rs.getInt("rank");
                for (Rank rank : Rank.RANKS) {
                    if (rank.value() == rankValue) {
                        builder.appendNewline().append(rank.longPrefix()).append(Component.text(rs.getString("name"), rank.color()));
                    }
                }
            }

            databaseConnection.finish();

            return builder.build();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Component.text("An error occurred!", NamedTextColor.DARK_RED);
    }

}
