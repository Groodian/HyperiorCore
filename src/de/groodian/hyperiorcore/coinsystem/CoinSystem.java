package de.groodian.hyperiorcore.coinsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import de.groodian.hyperiorcore.main.Main;

public class CoinSystem {

	private Main plugin;

	public CoinSystem(Main plugin) {
		this.plugin = plugin;
	}

	public void addCoins(Player player, int coins, boolean endOfRound) {
		if (endOfRound) {
			if (plugin.getRanks().has(player.getUniqueId(), "coins")) {
				coins = coins * 2;
			}
		}
		changeCoins(player, coins);
		if (endOfRound) {
			if (plugin.getRanks().has(player.getUniqueId(), "coins")) {
				player.sendMessage("§7[§eCoinSystem§7] §e+" + coins + " §6Coins §7(x2 §cGründer-Boost§7)");
			} else {
				player.sendMessage("§7[§eCoinSystem§7] §e+" + coins + " §6Coins §7(x2 mit dem §cGründer-Rang§7)");
			}
		} else {
			player.sendMessage("§7[§eCoinSystem§7] §e+" + coins + " §6Coins");
		}
	}

	public void removeCoins(Player player, int coins, boolean notify) {
		changeCoins(player, -coins);
		if (notify) {
			player.sendMessage("§7[§eCoinSystem§7] §c-" + coins + " §6Coins");
		}
	}

	private void changeCoins(Player player, int coins) {
		String uuid = player.getUniqueId().toString().replaceAll("-", "");
		String name = player.getName();
		if (hasCoins(player)) {
			try {
				PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("UPDATE core SET coins = ?, playername = ? WHERE UUID = ?");
				ps.setInt(1, getCoins(player) + coins);
				ps.setString(2, name);
				ps.setString(3, uuid);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("INSERT INTO core (UUID, playername, coins) VALUES(?,?,?)");
				ps.setString(1, uuid);
				ps.setString(2, name);
				ps.setInt(3, coins);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int getCoins(Player player) {
		String uuid = player.getUniqueId().toString().replaceAll("-", "");
		if (hasCoins(player)) {
			try {
				PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT coins FROM core WHERE UUID = ?");
				ps.setString(1, uuid);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					return rs.getInt("coins");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public boolean hasCoins(Player player) {
		String uuid = player.getUniqueId().toString().replaceAll("-", "");
		try {
			PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT coins FROM core WHERE UUID = ?");
			ps.setString(1, uuid);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
