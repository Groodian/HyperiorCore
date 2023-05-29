package de.groodian.hyperiorcore.main;

import org.bukkit.Bukkit;

public class Output {

    public static void send(String msg) {
        if (Mode.getModeType() == ModeType.BUKKIT)
            Bukkit.getConsoleSender().sendMessage(msg);
        else if (Mode.getModeType() == ModeType.BUNGEECORD)
            Bukkit.getConsoleSender().sendMessage(msg);
            //BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(msg));
        else
            System.out.println(Main.PREFIX + "§4ERROR no mode for the output defined!");
    }

}
