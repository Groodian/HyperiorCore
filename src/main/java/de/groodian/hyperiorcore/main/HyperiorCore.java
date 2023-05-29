package de.groodian.hyperiorcore.main;

import de.groodian.hyperiorcore.boards.HScoreboard;
import de.groodian.hyperiorcore.boards.Prefix;
import de.groodian.hyperiorcore.user.CoinSystem;
import de.groodian.hyperiorcore.user.Level;
import de.groodian.hyperiorcore.user.Ranks;
import de.groodian.hyperiorcore.user.UserManager;
import de.groodian.hyperiorcore.util.DatabaseManager;

public class HyperiorCore {

    public static DatabaseManager getDatabaseManager() {
        if (Mode.getModeType() == ModeType.BUKKIT)
            return Main.getInstance().getDatabaseManager();
        else if (Mode.getModeType() == ModeType.BUNGEECORD)
            return null;
            //return BungeeMain.getInstance().getMySQLManager();
        else
            return null;
    }

    public static UserManager getUserManager() {
        if (Mode.getModeType() == ModeType.BUKKIT)
            return Main.getInstance().getUserManager();
        else if (Mode.getModeType() == ModeType.BUNGEECORD)
            return null;
            //return BungeeMain.getInstance().getMySQLManager();
        else
            return null;
    }

    public static Ranks getRanks() {
        if (Mode.getModeType() == ModeType.BUKKIT)
            return Main.getInstance().getRanks();
        else if (Mode.getModeType() == ModeType.BUNGEECORD)
            return null;
            //return BungeeMain.getInstance().getRanks();
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
        if (Mode.getModeType() == ModeType.BUKKIT)
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
