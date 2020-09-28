package de.groodian.hyperiorcore.main;

import de.groodian.hyperiorcore.boards.HScoreboard;
import de.groodian.hyperiorcore.boards.Prefix;
import de.groodian.hyperiorcore.coinsystem.CoinSystem;
import de.groodian.hyperiorcore.level.Level;
import de.groodian.hyperiorcore.ranks.Ranks;
import de.groodian.hyperiorcore.util.MySQLManager;

public class HyperiorCore {

	public static MySQLManager getMySQLManager() {
		if (Mode.getModeType() == ModeType.BUKKIT)
			return Main.getInstance().getMySQLManager();
		else if (Mode.getModeType() == ModeType.BUNGEECORD)
			return BungeeMain.getInstance().getMySQLManager();
		else
			return null;
	}

	public static Ranks getRanks() {
		if (Mode.getModeType() == ModeType.BUKKIT)
			return Main.getInstance().getRanks();
		else if (Mode.getModeType() == ModeType.BUNGEECORD)
			return BungeeMain.getInstance().getRanks();
		else
			return null;
	}

	public static Prefix getPrefix() {
		if (Mode.getModeType() == ModeType.BUKKIT)
			return Main.getInstance().getPrefix();
		else
			return null;
	}

	public static HScoreboard getSB() {
		if (Mode.getModeType() == ModeType.BUKKIT)
			return Main.getInstance().getScoreboard();
		else
			return null;
	}

	public static CoinSystem getCoinSystem() {
		if(Mode.getModeType() == ModeType.BUKKIT) 
			return Main.getInstance().getCoinSystem();
			else
				return null;
	}

	public static Level getLevel() {
		if (Mode.getModeType() == ModeType.BUKKIT)
			return Main.getInstance().getLevel();
		else
			return null;
	}

}
