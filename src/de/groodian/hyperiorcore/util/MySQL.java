package de.groodian.hyperiorcore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.main.Output;

public class MySQL {

	private Connection connection;

	private String hostname;
	private int port;
	private String database;
	private String username;
	private String password;

	public MySQL(String hostname, int port, String database, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public void connect() {
		if (!isConnected()) {
			try {
				connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);
				Output.send(Main.PREFIX + "§aSuccessfully connected to the §bMySQL §adatabase §b" + database + "§a.");
			} catch (SQLException e) {
				Output.send(Main.PREFIX + "§4An error occurred while connecting to the §bMySQL §4database §b" + database + "§4:");
				Output.send(Main.PREFIX + "§4" + e.getMessage());
			}
		}
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				connection.close();
				Output.send(Main.PREFIX + "§aSuccessfully disconnected to the §bMySQL §adatabase §b" + database + "§a.");
			} catch (SQLException e) {
				Output.send(Main.PREFIX + "§4An error occurred while disconnecting to the §bMySQL §4database §b" + database + "§4:");
				Output.send(Main.PREFIX + "§4" + e.getMessage());
			}
		}
	}

	public boolean isConnected() {
		return (connection != null);
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

}
