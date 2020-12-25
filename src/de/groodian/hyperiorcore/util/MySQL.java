package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private static final int CONNECTIONS_KEEP_OPEN = 1;

    private List<MySQLConnection> connections = new ArrayList<>();

    protected String hostname;
    protected int port;
    protected String database;
    protected String username;
    protected String password;

    public MySQL(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * This method returns a new or reused connection and lock it.
     * If the DB operation is done use finish() to unlock the connection, that the connection can
     * be reused or closed (depends on the amount of open connections). Otherwise the connection will never close or reused!
     */
    public synchronized MySQLConnection getMySQLConnection() {
        for (MySQLConnection connection : connections) {
            if (!connection.isLocked()) {
                connection.lock();
                return connection;
            }
        }

        MySQLConnection connection = openNewMySQLConnection();
        connection.lock();
        return connection;
    }

    public void disconnect() {
        for (MySQLConnection connection : connections) {
            connection.disconnect();
        }
        connections.clear();
    }

    protected synchronized void connectionFinished(MySQLConnection connection) {
        if (connections.size() > CONNECTIONS_KEEP_OPEN) {
            connection.disconnect();
            connections.remove(connection);
        }
    }

    private MySQLConnection openNewMySQLConnection() {
        MySQLConnection connection = new MySQLConnection(this);
        connection.connect();
        connections.add(connection);
        return connection;
    }

}
