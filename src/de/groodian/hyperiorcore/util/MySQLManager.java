package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Mode;
import de.groodian.hyperiorcore.main.ModeType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLManager {

    private MySQL coreMySQL;
    private MySQL dataMySQL;
    private MySQL minecraftPartyMySQL;
    private MySQL cosmeticMySQL;

    public MySQLManager() {
        coreMySQL = new MySQL("localhost", 3306, "core", "admin", "test321");
        if (Mode.getModeType() == ModeType.BUNGEECORD) {
            dataMySQL = new MySQL("localhost", 3306, "data", "admin", "test321");
        } else if (Mode.getModeType() == ModeType.BUKKIT) {
            minecraftPartyMySQL = new MySQL("localhost", 3306, "minecraftparty", "admin", "test321");
            cosmeticMySQL = new MySQL("localhost", 3306, "cosmetic", "admin", "test321");
        }
    }

    public void connect() {
        try {

            if (coreMySQL != null) {
                coreMySQL.connect();
                PreparedStatement ps01 = coreMySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS core (UUID VARCHAR(100), playername VARCHAR(100), rank INT(100), level INT(100), coins INT(100), dailybonus VARCHAR(100), dailybonusvip VARCHAR(100))");
                ps01.executeUpdate();
            }

            if (dataMySQL != null) {
                dataMySQL.connect();
                PreparedStatement ps01 = dataMySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS data (UUID VARCHAR(100), playername VARCHAR(100), logins INT(100), firstlogin VARCHAR(100), lastlogin VARCHAR(100), lastlogout VARCHAR(100), lastip VARCHAR(100), logindays INT(100), connectiontime BIGINT(100))");
                PreparedStatement ps02 = dataMySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ban (UUID VARCHAR(100), playername VARCHAR(100), ban VARCHAR(100), reason VARCHAR(100), history TEXT(99999), reports INT(100), reporthistory TEXT(99999))");
                ps01.executeUpdate();
                ps02.executeUpdate();
            }

            if (minecraftPartyMySQL != null) {
                minecraftPartyMySQL.connect();
                PreparedStatement ps01 = minecraftPartyMySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS stats (UUID VARCHAR(100), playername VARCHAR(100), points INT(100), playtime BIGINT(100), minigamesplayed INT(100), gamesplayed INT(100), gamesended INT(100), gamesfirst INT(100), gamessecond INT(100), gamesthird INT(100), gamesfourth INT(100), gamesfifth INT(100), minigamesfirst INT(100), minigamessecond INT(100), minigamesthird INT(100), minigamesfourth INT(100), minigamesfifth INT(100))");
                PreparedStatement ps02 = minecraftPartyMySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS records (UUID VARCHAR(100), playername VARCHAR(100))");
                ps01.executeUpdate();
                ps02.executeUpdate();

            }

            if (cosmeticMySQL != null) {
                cosmeticMySQL.connect();
                PreparedStatement ps01 = cosmeticMySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS cosmetic (UUID VARCHAR(100), playername VARCHAR(100), cosmetics TEXT(99999), particle INT(100), block INT(100), helmet INT(100), chestplate INT(100), pants INT(100), shoes INT(100), gadget INT(100))");
                ps01.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {
        if (coreMySQL != null)
            coreMySQL.disconnect();

        if (dataMySQL != null)
            dataMySQL.disconnect();

        if (minecraftPartyMySQL != null)
            minecraftPartyMySQL.disconnect();

        if (cosmeticMySQL != null)
            cosmeticMySQL.disconnect();

    }

    public MySQL getCoreMySQL() {
        return coreMySQL;
    }

    public MySQL getDataMySQL() {
        return dataMySQL;
    }

    public MySQL getMinecraftPartyMySQL() {
        return minecraftPartyMySQL;
    }

    public MySQL getCosmeticMySQL() {
        return cosmeticMySQL;
    }

}
