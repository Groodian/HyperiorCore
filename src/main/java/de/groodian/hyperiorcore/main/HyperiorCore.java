package de.groodian.hyperiorcore.main;

import de.groodian.hyperiorcore.boards.HScoreboard;
import de.groodian.hyperiorcore.boards.Prefix;
import de.groodian.hyperiorcore.command.HCommandManagerPaper;
import de.groodian.hyperiorcore.command.HCommandManagerVelocity;
import de.groodian.hyperiorcore.spawnable.SpawnAbleManager;
import de.groodian.hyperiorcore.user.CoinSystem;
import de.groodian.hyperiorcore.user.DailyBonus;
import de.groodian.hyperiorcore.user.Level;
import de.groodian.hyperiorcore.user.Ranks;
import de.groodian.hyperiorcore.user.UserManager;
import de.groodian.hyperiorcore.util.DatabaseManager;

public class HyperiorCore {

    public static final String DB_ADDRESS = "localhost";
    public static final int DB_PORT = 5444;
    public static final String DB_DATABASE = "postgres";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "toor";

    public static DatabaseManager getDatabaseManager() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getDatabaseManager();
        else if (Mode.getModeType() == ModeType.VELOCITY)
            return VelocityMain.getInstance().getDatabaseManager();
        else
            return null;
    }

    public static UserManager getUserManager() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getUserManager();
        else if (Mode.getModeType() == ModeType.VELOCITY)
            return VelocityMain.getInstance().getUserManager();
        else
            return null;
    }

    public static SpawnAbleManager getSpawnAbleManager() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getSpawnAbleManager();
        else
            return null;
    }

    public static HCommandManagerPaper getHCommandManagerPaper() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getHCommandManagerPaper();
        else
            return null;
    }

    public static HCommandManagerVelocity getHCommandManagerVelocity() {
        if (Mode.getModeType() == ModeType.VELOCITY)
            return VelocityMain.getInstance().getHCommandManagerVelocity();
        else
            return null;
    }

    public static Ranks getRanks() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getRanks();
        else
            return null;
    }

    public static Prefix getPrefix() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getPrefix();
        else
            return null;
    }

    public static HScoreboard getSB() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getScoreboard();
        else
            return null;
    }

    public static CoinSystem getCoinSystem() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getCoinSystem();
        else
            return null;
    }

    public static Level getLevel() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getLevel();
        else
            return null;
    }

    public static DailyBonus getDailyBonus() {
        if (Mode.getModeType() == ModeType.PAPER)
            return Main.getInstance().getDailyBonus();
        else
            return null;
    }

}
