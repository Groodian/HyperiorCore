package de.groodian.hyperiorcore.ranks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.groodian.hyperiorcore.util.MySQL;

public class Ranks {

	private MySQL coreMySQL;
	private List<Rank> ranks;

	public Ranks(MySQL coreMySQL) {
		this.coreMySQL = coreMySQL;
		
		this.ranks = Arrays.asList(
				
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
		
	}

	public boolean setRank(UUID uuid, String name, String rankName) {
		return setRank(uuid.toString(), name, rankName);
	}

	public boolean setRank(String uuid, String name, String rankName) {
		String stringUUID = uuid.toString().replaceAll("-", "");

		int rankValue = -1;
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(rankName)) {
				rankValue = rank.getValue();
			}
		}
		if (rankValue == -1) {
			return false;
		}

		if (isInDatabase(uuid)) {
			try {
				PreparedStatement ps = coreMySQL.getConnection().prepareStatement("UPDATE core SET rank = ?, playername = ? WHERE UUID = ?");
				ps.setInt(1, rankValue);
				ps.setString(2, name);
				ps.setString(3, stringUUID);
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				PreparedStatement ps = coreMySQL.getConnection().prepareStatement("INSERT INTO core (UUID, playername, rank) VALUES(?,?,?)");
				ps.setString(1, stringUUID);
				ps.setString(2, name);
				ps.setInt(3, rankValue);
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public Rank getRank(UUID uuid) {
		return getRank(uuid.toString());
	}

	public Rank getRank(String uuid) {
		String stringUUID = uuid.toString().replaceAll("-", "");
		if (hasRank(uuid)) {
			try {
				PreparedStatement ps = coreMySQL.getConnection().prepareStatement("SELECT rank FROM core WHERE UUID = ?");
				ps.setString(1, stringUUID);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					for (Rank rank : ranks) {
						if (rank.getValue() == rs.getInt("rank")) {
							return rank;
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Rank(9, "Player", new ArrayList<String>(), "§a", "§a", "§a");
	}

	public boolean has(UUID uuid, String permission) {
		return has(uuid.toString(), permission);
	}

	public boolean has(String uuid, String permission) {
		Rank rank = getRank(uuid);
		for (String current : rank.getPermissions()) {
			if (current.equals(permission)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRank(UUID uuid) {
		return hasRank(uuid.toString());
	}

	public boolean hasRank(String uuid) {
		String stringUUID = uuid.toString().replaceAll("-", "");
		try {
			PreparedStatement ps = coreMySQL.getConnection().prepareStatement("SELECT rank FROM core WHERE UUID = ?");
			ps.setString(1, stringUUID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("rank") > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isInDatabase(String uuid) {
		String stringUUID = uuid.toString().replaceAll("-", "");
		try {
			PreparedStatement ps = coreMySQL.getConnection().prepareStatement("SELECT rank FROM core WHERE UUID = ?");
			ps.setString(1, stringUUID);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeRank(UUID uuid) {
		return removeRank(uuid.toString());
	}

	public boolean removeRank(String uuid) {
		String stringUUID = uuid.toString().replaceAll("-", "");
		if(hasRank(uuid)) {
			try {
				PreparedStatement ps = coreMySQL.getConnection().prepareStatement("UPDATE core SET rank = NULL WHERE UUID = ?");
				ps.setString(1, stringUUID);
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}
		return false;
	}

}
