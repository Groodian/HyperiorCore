package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseManager {

    /**
     * If the number of max connections is greater than one, problems can occur when two threads reading/writing on the same data!
     */
    private static final int MAX_CONNECTIONS = 1;

    private final List<DatabaseConnection> connections = new ArrayList<>();
    private final BlockingQueue<DatabaseConnection> availableConnections = new ArrayBlockingQueue<>(MAX_CONNECTIONS);

    protected String hostname;
    protected int port;
    protected String database;
    protected String username;
    protected String password;

    public DatabaseManager(String hostname, int port, String database, String username, String password) {
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
    public synchronized DatabaseConnection getConnection() {
        try {
            if (connections.size() < MAX_CONNECTIONS) {
                openNewConnection();
            }
            return availableConnections.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        for (DatabaseConnection connection : connections) {
            connection.disconnect();
        }

        connections.clear();
        availableConnections.clear();
    }

    protected void connectionFinished(DatabaseConnection connection) {
        if (!availableConnections.offer(connection)) {
            System.out.println("\n\n\n!!! COULD NOT OFFER DATABASE CONNECTION !!!\n\n\n");
        }
    }

    private void openNewConnection() {
        DatabaseConnection connection = new DatabaseConnection(this);
        connection.connect();
        connections.add(connection);
        availableConnections.add(connection);
    }

}
