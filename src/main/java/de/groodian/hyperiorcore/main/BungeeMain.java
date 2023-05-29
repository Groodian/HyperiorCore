/*
package de.groodian.hyperiorcore.main;

import de.groodian.hyperiorcore.listeners.BungeeMainListener;
import de.groodian.hyperiorcore.user.Ranks;
import de.groodian.hyperiorcore.util.MySQLManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

    private static BungeeMain instance;

    private MySQLManager mySQLManager;
    private Ranks ranks;

    public void onEnable() {
        Mode.setModeType(ModeType.BUNGEECORD);

        Output.send(Main.PREFIX + "�aDas Plugin wird geladen....");

        instance = this;

        BungeeCord.getInstance().getPluginManager().registerListener(this, new BungeeMainListener(this));

        mySQLManager = new MySQLManager();
        mySQLManager.connect();

        ranks = new Ranks(mySQLManager.getCoreMySQL());

        Output.send(Main.PREFIX + "�aGeladen!");
    }

    public void onDisable() {
        Output.send(Main.PREFIX + "�cDas Plugin wird gestoppt....");

        mySQLManager.disconnect();

        Output.send(Main.PREFIX + "�cGestoppt!");
    }

    public static BungeeMain getInstance() {
        return instance;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    public Ranks getRanks() {
        return ranks;
    }

}*/
