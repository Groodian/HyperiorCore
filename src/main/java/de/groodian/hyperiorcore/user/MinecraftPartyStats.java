package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseManager;
import de.groodian.hyperiorcore.util.DatabaseTransaction;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

public class MinecraftPartyStats extends DatabaseTransaction {

    private final PlayerFinishedGame playerFinishedGame;

    public MinecraftPartyStats(PlayerFinishedGame playerFinishedGame) {
        this.playerFinishedGame = playerFinishedGame;
    }

    @Override
    public void runOnSuccess() {
    }

    @Override
    public boolean run(DatabaseConnection databaseConnection) {
        Player player = loadPlayer(databaseConnection, playerFinishedGame.uuid);

        if (player == null) {
            createPlayer(databaseConnection, playerFinishedGame.uuid);
            player = loadPlayer(databaseConnection, playerFinishedGame.uuid);
        }

        if (player == null) {
            return false;
        }

        return finishedGameInDatabase(databaseConnection, player, playerFinishedGame);
    }

    public static class PlayerFinishedGame {
        public final UUID uuid;
        public int points = 0;
        public int playtime = 0;
        public boolean gameEnded = false;
        public int miniGamesPlayed = 0;
        public int winnerPlace = -1;
        public int miniGamesFirst = 0;
        public int miniGamesSecond = 0;
        public int miniGamesThird = 0;
        public int miniGamesFourth = 0;
        public int miniGamesFifth = 0;
        public List<PlayerFinishedGameRecord> records = new ArrayList<>();

        public PlayerFinishedGame(UUID uuid) {
            this.uuid = uuid;
        }

    }

    public record PlayerFinishedGameRecord(String name, int record, OffsetDateTime achievedAt, boolean mustBeHigher) {
    }

    public record Player(UUID uuid, int rank, int points, int playtime, int gamesPlayed, int gamesEnded, int miniGamesPlayed,
                         int gamesFirst, int gamesSecond, int gamesThird, int gamesFourth, int gamesFifth, int miniGamesFirst,
                         int miniGamesSecond, int miniGamesThird, int miniGamesFourth, int miniGamesFifth, List<Record> records) {
    }

    public record Record(String name, int record, OffsetDateTime achievedAt) {
    }

    public record Top10(UUID uuid, String name, int points, int wins) {
    }

    private boolean finishedGameInDatabase(DatabaseConnection databaseConnection, Player player, PlayerFinishedGame playerFinishedGame) {
        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "UPDATE hyperior_mc.minecraft_party SET points = points + ?, playtime = playtime + ?, games_played = games_played + ?, games_ended = games_ended + ?, mini_games_played = mini_games_played + ?, games_first = games_first + ?, games_second = games_second + ?, games_third = games_third + ?, games_fourth = games_fourth + ?, games_fifth = games_fifth + ?, mini_games_first = mini_games_first + ?, mini_games_second = mini_games_second + ?, mini_games_third = mini_games_third + ?, mini_games_fourth = mini_games_fourth + ?, mini_games_fifth = mini_games_fifth + ? WHERE uuid = ?");

            ps.setInt(1, playerFinishedGame.points);
            ps.setInt(2, playerFinishedGame.playtime);
            ps.setInt(3, 1);
            ps.setInt(4, playerFinishedGame.gameEnded ? 1 : 0);
            ps.setInt(5, playerFinishedGame.miniGamesPlayed);
            ps.setInt(6, playerFinishedGame.winnerPlace == 1 ? 1 : 0);
            ps.setInt(7, playerFinishedGame.winnerPlace == 2 ? 1 : 0);
            ps.setInt(8, playerFinishedGame.winnerPlace == 3 ? 1 : 0);
            ps.setInt(9, playerFinishedGame.winnerPlace == 4 ? 1 : 0);
            ps.setInt(10, playerFinishedGame.winnerPlace == 5 ? 1 : 0);
            ps.setInt(11, playerFinishedGame.miniGamesFirst);
            ps.setInt(12, playerFinishedGame.miniGamesSecond);
            ps.setInt(13, playerFinishedGame.miniGamesThird);
            ps.setInt(14, playerFinishedGame.miniGamesFourth);
            ps.setInt(15, playerFinishedGame.miniGamesFifth);
            ps.setObject(16, player.uuid);

            if (ps.executeUpdate() != 1) {
                return false;
            }

            for (PlayerFinishedGameRecord recordGame : playerFinishedGame.records) {
                boolean updateRecord = false;
                boolean recordFound = false;

                for (Record record : player.records) {
                    if (recordGame.name.equals(record.name)) {
                        if (recordGame.mustBeHigher) {
                            if (recordGame.record > record.record) {
                                updateRecord = true;
                            }
                        } else {
                            if (recordGame.record < record.record) {
                                updateRecord = true;
                            }
                        }
                        recordFound = true;
                        break;
                    }
                }

                if (recordFound) {
                    if (updateRecord) {
                        PreparedStatement psRec = databaseConnection.getPreparedStatement(
                                "UPDATE hyperior_mc.minecraft_party_records SET record = ?, achieved_at = ? WHERE uuid = ? AND name = ? AND record " +
                                (recordGame.mustBeHigher ? "<" : ">") + " ?");
                        psRec.setInt(1, recordGame.record);
                        psRec.setObject(2, recordGame.achievedAt);
                        psRec.setObject(3, player.uuid);
                        psRec.setString(4, recordGame.name);
                        psRec.setInt(5, recordGame.record);
                        psRec.executeUpdate();
                    }
                } else {
                    PreparedStatement psRec = databaseConnection.getPreparedStatement(
                            "INSERT INTO hyperior_mc.minecraft_party_records (uuid, name, record, achieved_at) VALUES (?, ?, ?, ?) ON CONFLICT (uuid, name) DO NOTHING");
                    psRec.setObject(1, player.uuid);
                    psRec.setString(2, recordGame.name);
                    psRec.setInt(3, recordGame.record);
                    psRec.setObject(4, recordGame.achievedAt);
                    psRec.executeUpdate();
                }


            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage("§4Error while saving finished minecraft party game!");

        return false;
    }

    /**
     * This method should be executed async
     */
    public static Player loadPlayer(DatabaseManager databaseManager, UUID uuid) {
        DatabaseConnection databaseConnection = databaseManager.getConnection();
        Player player = loadPlayer(databaseConnection, uuid);
        databaseConnection.finish();
        return player;
    }

    /**
     * This method should be executed async
     */
    public static List<Top10> getTop10(DatabaseManager databaseManager) {
        List<Top10> top10 = new ArrayList<>();

        DatabaseConnection databaseConnection = databaseManager.getConnection();

        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "SELECT m.uuid, u.name, m.points, m.games_first FROM hyperior_mc.minecraft_party m INNER JOIN hyperior_mc.users u ON u.uuid = m.uuid ORDER BY m.points DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                top10.add(new Top10(
                        rs.getObject("uuid", UUID.class),
                        rs.getString("name"),
                        rs.getInt("points"),
                        rs.getInt("games_first")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseConnection.finish();

        return top10;
    }

    private static Player loadPlayer(DatabaseConnection databaseConnection, UUID uuid) {
        Player player = null;

        try {
            PreparedStatement ps1 = databaseConnection.getPreparedStatement(
                    "SELECT name, record, achieved_at FROM hyperior_mc.minecraft_party_records WHERE uuid = ?");
            ps1.setObject(1, uuid);
            ResultSet rs1 = ps1.executeQuery();

            List<Record> records = new ArrayList<>();
            while (rs1.next()) {
                records.add(new Record(
                        rs1.getString("name"),
                        rs1.getInt("record"),
                        rs1.getObject("achieved_at", OffsetDateTime.class)
                ));
            }

            PreparedStatement ps2 = databaseConnection.getPreparedStatement(
                    "SELECT rank, points, playtime, games_played, games_ended, mini_games_played, games_first, games_second, games_third, games_fourth, games_fifth, mini_games_first, mini_games_second, mini_games_third, mini_games_fourth, mini_games_fifth FROM hyperior_mc.minecraft_party INNER JOIN(SELECT RANK() OVER (ORDER BY points DESC) rank, uuid as rank_uuid FROM hyperior_mc.minecraft_party) rank_table ON rank_uuid = uuid WHERE uuid = ?");
            ps2.setObject(1, uuid);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                player = new Player(uuid,
                        rs2.getInt("rank"),
                        rs2.getInt("points"),
                        rs2.getInt("playtime"),
                        rs2.getInt("games_played"),
                        rs2.getInt("games_ended"),
                        rs2.getInt("mini_games_played"),
                        rs2.getInt("games_first"),
                        rs2.getInt("games_second"),
                        rs2.getInt("games_third"),
                        rs2.getInt("games_fourth"),
                        rs2.getInt("games_fifth"),
                        rs2.getInt("mini_games_first"),
                        rs2.getInt("mini_games_second"),
                        rs2.getInt("mini_games_third"),
                        rs2.getInt("mini_games_fourth"),
                        rs2.getInt("mini_games_fifth"),
                        records
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return player;
    }

    private void createPlayer(DatabaseConnection databaseConnection, UUID uuid) {
        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "INSERT INTO hyperior_mc.minecraft_party (uuid, points, playtime, games_played, games_ended, mini_games_played, games_first, games_second, games_third, games_fourth, games_fifth, mini_games_first, mini_games_second, mini_games_third, mini_games_fourth, mini_games_fifth) VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON CONFLICT (uuid) DO NOTHING");
            ps.setObject(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
