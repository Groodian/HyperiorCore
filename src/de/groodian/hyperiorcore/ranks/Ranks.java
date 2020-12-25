package de.groodian.hyperiorcore.ranks;

import de.groodian.hyperiorcore.util.MySQL;
import de.groodian.hyperiorcore.util.MySQLConnection;
import de.groodian.hyperiorcore.util.UUIDFetcher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks {

    private MySQL coreMySQL;
    private UUIDFetcher uuidFetcher;
    private Map<String, Rank> cache;
    private List<Rank> ranks;
    private Rank defaultRank;

    public Ranks(MySQL coreMySQL) {
        this.coreMySQL = coreMySQL;
        uuidFetcher = new UUIDFetcher();
        cache = new HashMap<>();

        ranks = Arrays.asList(

                // don´t use 0!!!

                new Rank(
                        1,
                        "Admin",
                        Arrays.asList("cosmetics.buy", "dailybonus.vip", "minecraftparty.start", "coins", "minecraftparty.premiumjoin", "joinfullserver", "team", "kick", "lookup", "unban", "ban", "broadcast", "motd", "lobby.setup", "lobby.build", "maintenance", "pban", "serverstarter", "minecraftparty.build", "minecraftparty.setup", "commandsblock.bypass", "ranks.all", "slots"),
                        "§4",
                        "§4Admin §7| §4",
                        "§4Admin §7| §4"),

                new Rank(
                        2,
                        "Moderator",
                        Arrays.asList("cosmetics.buy", "dailybonus.vip", "minecraftparty.start", "coins", "minecraftparty.premiumjoin", "joinfullserver", "team", "kick", "lookup", "unban", "ban", "broadcast"),
                        "§9",
                        "§9Moderator §7| §9",
                        "§9Mod §7| §9"),

                new Rank(
                        3,
                        "Builder",
                        Arrays.asList("cosmetics.buy", "dailybonus.vip", "minecraftparty.start", "coins", "minecraftparty.premiumjoin", "joinfullserver", "team"),
                        "§3",
                        "§3Builder §7| §3",
                        "§3Builder §7| §3"),

                new Rank(
                        4,
                        "Supporter",
                        Arrays.asList("cosmetics.buy", "dailybonus.vip", "minecraftparty.start", "coins", "minecraftparty.premiumjoin", "joinfullserver", "team", "kick"),
                        "§2",
                        "§2Supporter §7| §2",
                        "§2Sup §7| §2"),

                new Rank(
                        5,
                        "YouTuber",
                        Arrays.asList("cosmetics.buy", "dailybonus.vip", "minecraftparty.start", "coins", "minecraftparty.premiumjoin", "joinfullserver"),
                        "§d",
                        "§dYouTuber §7| §d",
                        "§dYT §7| §d"),

                new Rank(
                        6,
                        "VIP",
                        Arrays.asList("cosmetics.buy", "dailybonus.vip", "minecraftparty.start", "coins", "minecraftparty.premiumjoin", "joinfullserver"),
                        "§e",
                        "§eVIP §7| §e",
                        "§eVIP §7| §e")

        );

        defaultRank = new Rank(9, "Player", new ArrayList<String>(), "§a", "§a", "§a");

    }

    /**
     * This method can be executed sync
     */
    public boolean has(UUID uuid, String permission) {
        return has(uuid.toString(), permission);
    }

    /**
     * This method can be executed sync
     */
    public boolean has(String uuid, String permission) {
        uuid = uuid.replaceAll("-", "");
        if (cache.containsKey(uuid)) {
            for (String current : cache.get(uuid).getPermissions()) {
                if (current.equalsIgnoreCase(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method can be executed sync
     */
    public Rank get(UUID uuid) {
        return get(uuid.toString());
    }

    /**
     * This method can be executed sync
     */
    public Rank get(String uuid) {
        uuid = uuid.replaceAll("-", "");
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        return defaultRank;
    }

    /**
     * This method should be executed async
     */
    public void login(UUID uuid) {
        String stringUUID = uuid.toString().replaceAll("-", "");
        cache.put(stringUUID, getRankFromDatabase(stringUUID));
    }

    /**
     * This method can be executed sync
     */
    public void logout(UUID uuid) {
        String stringUUID = uuid.toString().replaceAll("-", "");
        cache.remove(stringUUID);
    }

    /**
     * This method should be executed async
     */
    public String setRank(String name, String rankName) {
        UUIDFetcher.Result result = uuidFetcher.getNameAndUUIDFromName(name);

        if (result == null) {
            return "§cThis player does not exist.";
        }

        Rank rank = null;
        for (Rank current : ranks) {
            if (current.getName().equalsIgnoreCase(rankName)) {
                rank = current;
            }
        }
        if (rank == null) {
            return "§cCould not find the rank!";
        }

        try {

            if (isInDatabase(result.getUUID())) {
                MySQLConnection connection = coreMySQL.getMySQLConnection();
                PreparedStatement ps = connection.getConnection().prepareStatement("UPDATE core SET rank = ?, playername = ? WHERE UUID = ?");
                ps.setInt(1, rank.getValue());
                ps.setString(2, result.getName());
                ps.setString(3, result.getUUID());
                ps.executeUpdate();
                ps.close();
                connection.finish();
            } else {
                MySQLConnection connection = coreMySQL.getMySQLConnection();
                PreparedStatement ps = connection.getConnection().prepareStatement("INSERT INTO core (UUID, playername, rank) VALUES(?,?,?)");
                ps.setString(1, result.getUUID());
                ps.setString(2, result.getName());
                ps.setInt(3, rank.getValue());
                ps.executeUpdate();
                ps.close();
                connection.finish();
            }

            cache.put(result.getUUID(), rank);

            return "§a" + result.getName() + "§7 has now the rank " + rank.getColor() + rank.getName() + "§7.";

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

        if (hasRank(result.getUUID())) {

            try {

                Rank rank = getRankFromDatabase(result.getUUID());

                MySQLConnection connection = coreMySQL.getMySQLConnection();
                PreparedStatement ps = connection.getConnection().prepareStatement("UPDATE core SET rank = NULL WHERE UUID = ?");
                ps.setString(1, result.getUUID());
                ps.executeUpdate();
                ps.close();
                connection.finish();

                cache.remove(result.getUUID());

                return "§a" + result.getName() + "§7 is no longer a " + rank.getColor() + rank.getName() + "§7.";

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

        if (hasRank(result.getUUID())) {
            Rank rank = getRankFromDatabase(result.getUUID());
            return "§a" + result.getName() + "§7 has the rank " + rank.getColor() + rank.getName() + "§7.";
        } else {
            return "§a" + result.getName() + "§7 has no rank.";
        }

    }

    /**
     * This method should be executed async
     */
    public String list() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§7List:");
        try {
            MySQLConnection connection = coreMySQL.getMySQLConnection();
            PreparedStatement ps = connection.getConnection().prepareStatement("SELECT UUID, playername, rank FROM core ORDER BY rank");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                for (Rank rank : ranks) {
                    if (rank.getValue() == rs.getInt("rank")) {
                        stringBuilder.append("\n" + rank.getLongPrefix() + rs.getString("playername"));
                    }
                }
            }
            ps.close();
            connection.finish();

            return stringBuilder.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "§cAn error occurred";
    }

    /**
     * This method should be executed async
     */
    public Rank getRankFromDatabase(String uuid) {
        uuid = uuid.replaceAll("-", "");
        if (hasRank(uuid)) {
            try {
                MySQLConnection connection = coreMySQL.getMySQLConnection();
                PreparedStatement ps = connection.getConnection().prepareStatement("SELECT rank FROM core WHERE UUID = ?");
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                Rank returnRank = defaultRank;
                if (rs.next()) {
                    for (Rank rank : ranks) {
                        if (rank.getValue() == rs.getInt("rank")) {
                            returnRank = rank;
                        }
                    }
                }
                ps.close();
                connection.finish();
                return returnRank;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return defaultRank;
    }

    private boolean hasRank(String uuid) {
        uuid = uuid.replaceAll("-", "");
        try {
            MySQLConnection connection = coreMySQL.getMySQLConnection();
            PreparedStatement ps = connection.getConnection().prepareStatement("SELECT rank FROM core WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            boolean hasRank = false;
            if (rs.next()) {
                if (rs.getInt("rank") > 0) {
                    hasRank = true;
                }
            }
            ps.close();
            connection.finish();
            return hasRank;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isInDatabase(String uuid) {
        uuid = uuid.replaceAll("-", "");
        try {
            MySQLConnection connection = coreMySQL.getMySQLConnection();
            PreparedStatement ps = connection.getConnection().prepareStatement("SELECT rank FROM core WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            boolean isInDatabase = rs.next();
            ps.close();
            connection.finish();
            return isInDatabase;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
