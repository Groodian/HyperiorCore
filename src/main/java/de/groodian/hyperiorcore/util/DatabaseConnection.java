package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.main.Output;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {

    private final DatabaseManager databaseManager;
    private final Map<String, PreparedStatement> cache;

    private Connection connection;

    public DatabaseConnection(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.cache = new HashMap<>();
    }

    public PreparedStatement getPreparedStatement(String sql) {
        PreparedStatement returnValue = cache.get(sql);

        if (returnValue == null) {
            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                cache.put(sql, ps);
                returnValue = ps;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return returnValue;
    }

    protected boolean connect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://" + databaseManager.hostname + ":" + databaseManager.port + "/" + databaseManager.database,
                                                         databaseManager.username,
                                                         databaseManager.password);

                Output.send(Main.PREFIX + "§aSuccessfully connected to the database.");
                return true;
            } catch (SQLException e) {
                Output.send(Main.PREFIX + "§4An error occurred while connecting to the database:");
                Output.send(Main.PREFIX + "§4" + e.getMessage());
            }
        }
        return false;
    }

    protected boolean disconnect() {
        if (connection != null) {
            try {
                connection.close();
                Output.send(Main.PREFIX + "§aThe connection to the database was successfully disconnected.");
                return true;
            } catch (SQLException e) {
                Output.send(Main.PREFIX + "§4An error occurred while disconnecting from the database:");
                Output.send(Main.PREFIX + "§4" + e.getMessage());
            }
        }
        return false;
    }

    private Connection getConnection() {
        try {
            if (!connection.isValid(2)) {
                connection.close();
                connection = null;
                cache.clear();
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void finish() {
        databaseManager.connectionFinished(this);
    }

}
