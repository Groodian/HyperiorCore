package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.main.Output;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    private Connection connection;
    private MySQL mySQL;
    private boolean locked;

    public MySQLConnection(MySQL mySQL) {
        this.mySQL = mySQL;
        this.locked = false;
    }

    protected boolean connect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + mySQL.hostname + ":" + mySQL.port + "/" + mySQL.database, mySQL.username, mySQL.password);
                Output.send(Main.PREFIX + "§aSuccessfully connected to the §bMySQL §adatabase §b" + mySQL.database + "§a.");
                return true;
            } catch (SQLException e) {
                Output.send(Main.PREFIX + "§4An error occurred while connecting to the §bMySQL §4database §b" + mySQL.database + "§4:");
                Output.send(Main.PREFIX + "§4" + e.getMessage());
            }
        }
        return false;
    }

    protected boolean disconnect() {
        if (connection != null) {
            try {
                connection.close();
                Output.send(Main.PREFIX + "§aSuccessfully disconnected from the §bMySQL §adatabase §b" + mySQL.database + "§a.");
                return true;
            } catch (SQLException e) {
                Output.send(Main.PREFIX + "§4An error occurred while disconnecting from the §bMySQL §4database §b" + mySQL.database + "§4:");
                Output.send(Main.PREFIX + "§4" + e.getMessage());
            }
        }
        return false;
    }

    public Connection getConnection() {
        try {
            if (!connection.isValid(2)) {
                connection.close();
                connection = null;
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void finish() {
        mySQL.connectionFinished(this);
        locked = false;
    }

    protected void lock() {
        locked = true;
    }

    protected boolean isLocked() {
        return locked;
    }

}
