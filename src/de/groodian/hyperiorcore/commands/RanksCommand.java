package de.groodian.hyperiorcore.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.ranks.Rank;
import de.groodian.hyperiorcore.util.UUIDFetcher;

public class RanksCommand implements CommandExecutor {

	private Main plugin;

	private UUIDFetcher uuidFetcher;

	public RanksCommand(Main plugin) {
		this.plugin = plugin;
		this.uuidFetcher = new UUIDFetcher();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
			if (sender instanceof Player) {
				if (!(plugin.getRanks().has(((Player) sender).getUniqueId(), "ranks.all"))) {
					return false;
				}
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("info")) {
					if (args.length == 2) {
						sender.sendMessage(Main.PREFIX + "§7Downloading UUID...");
						String uuid = uuidFetcher.getUUID(args[1]);
						String name = uuidFetcher.getName(args[1]);
						if (uuid == null) {
							sender.sendMessage(Main.PREFIX + "§cThis player does not exist.");
						} else {
							if (plugin.getRanks().hasRank(uuid)) {
								Rank rank = plugin.getRanks().getRank(uuid);
								sender.sendMessage(Main.PREFIX + "§a" + name + "§7 has the rank " + rank.getColor() + rank.getName() + "§7.");
							} else {
								sender.sendMessage(Main.PREFIX + "§a" + name + "§7 has no rank.");
							}
						}
					} else
						sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr info <player>");

				} else if (args[0].equalsIgnoreCase("set")) {
					if (args.length == 3) {
						sender.sendMessage(Main.PREFIX + "§7Downloading UUID...");
						String uuid = uuidFetcher.getUUID(args[1]);
						String name = uuidFetcher.getName(args[1]);
						if (uuid == null) {
							sender.sendMessage(Main.PREFIX + "§cThis player does not exist.");
						} else {
							if (plugin.getRanks().setRank(uuid, name, args[2])) {
								if (plugin.getRanks().hasRank(uuid)) {
									Rank rank = plugin.getRanks().getRank(uuid);
									sender.sendMessage(Main.PREFIX + "§a" + name + "§7 has now the rank " + rank.getColor() + rank.getName() + "§7.");
								} else {
									sender.sendMessage(Main.PREFIX + "§cAn error occurred.");
								}
							} else {
								sender.sendMessage(Main.PREFIX + "§cAn error occurred. Is the rank name correct?");
							}
						}
					} else
						sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr set <player> <rank>");
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						sender.sendMessage(Main.PREFIX + "§7Downloading UUID...");
						String uuid = uuidFetcher.getUUID(args[1]);
						String name = uuidFetcher.getName(args[1]);
						if (uuid == null) {
							sender.sendMessage(Main.PREFIX + "§cThis player does not exist.");
						} else {
							if (plugin.getRanks().hasRank(uuid)) {
								Rank rank = plugin.getRanks().getRank(uuid);
								if (plugin.getRanks().removeRank(uuid)) {
									sender.sendMessage(Main.PREFIX + "§a" + name + "§7 is no longer a " + rank.getColor() + rank.getName() + "§7.");
								} else {
									sender.sendMessage(Main.PREFIX + "§cAn error occurred.");
								}
							} else {
								sender.sendMessage(Main.PREFIX + "§a" + name + "§7 has no rank.");
							}
						}
					} else
						sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr remove <player>");
				} else if (args[0].equalsIgnoreCase("list")) {
					if (args.length == 1) {
						try {
							PreparedStatement ps = plugin.getMySQLManager().getCoreMySQL().getConnection().prepareStatement("SELECT UUID, playername, rank FROM core ORDER BY rank");
							ResultSet rs = ps.executeQuery();
							sender.sendMessage(Main.PREFIX + "§7List:");
							while (rs.next()) {
								if (rs.getString("rank") != null) {
									sender.sendMessage(plugin.getRanks().getRank(rs.getString("UUID")).getLongPrefix() + rs.getString("playername"));
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else
						sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr list");
				} else if (args[0].equalsIgnoreCase("getop")) {
					if (args.length == 1) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							player.setOp(true);
							player.sendMessage(Main.PREFIX + "§aYou are now OP on this server.");
						} else
							sender.sendMessage(Main.PREFIX + "This command has to be executed by a player.");
					} else
						sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr getop");
				} else
					sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr <info/set/remove/list/getop>");
			} else
				sender.sendMessage(Main.PREFIX + "§cUsage: §6/hr <info/set/remove/list/getop>");

		} else
			sender.sendMessage(Main.PREFIX + "This command has to be executed by a player or the console.");
		return false;
	}
}